package com.xceptance.loadtest.api.models.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.function.Predicate;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.hpu.By;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.render.HtmlMapRenderer;
import com.xceptance.loadtest.api.render.HtmlMapper;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DOMUtils;
import com.xceptance.loadtest.api.util.Log;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Base class for our page concept
 */
public abstract class Page implements PageInterface
{
    public interface ValidateType
    {
        /**
         * Checks only
         *
         * @param component
         *            component to check
         * @return true if exists, false otherwise
         */
        default boolean exists(final Component component)
        {
            final boolean result = component.exists();
            if (result)
            {
                Log.infoWhenDev("  ''{0}'' found", component.getClass().getSimpleName());
            }
            else
            {
                Log.infoWhenDev("  ''{0}'' not found", component.getClass().getSimpleName());
            }
            return result;
        }

        /**
         * Runs assertions, will break if it fails
         */
        public void validate(final Class<?> caller);

        /**
         * Check that the claim of the ValidateTyp is true
         */
        public boolean matches(final Class<?> caller);
    }

    /**
     * All exists
     *
     * @author rschwietzke
     */
    public class Has implements ValidateType
    {
        protected final Component[] components;

        Has(final Component... components)
        {
            this.components = components;
        }

        @Override
        public void validate(final Class<?> caller)
        {
            Log.infoWhenDev("{0} has...", caller.getSimpleName());

            for (final Component component : components)
            {
                Assert.assertTrue(
                            MessageFormat.format("Component ''{0}'' NOT found but expected", component.getClass().getSimpleName()),
                                exists(component));
            }
        }

        boolean exists()
        {
            boolean result = true;

            for (final Component component : components)
            {
                result = result && exists(component);
            }

            return result;
        }

        @Override
        public boolean matches(final Class<?> caller)
        {
            Log.infoWhenDev("{0} has...", caller.getSimpleName());

            return exists();
        }
    }

    /**
     * Non exists
     *
     * @author rschwietzke
     */
    public class HasNot implements ValidateType
    {
        protected final Component[] components;

        HasNot(final Component... components)
        {
            this.components = components;
        }

        boolean exists()
        {
            boolean result = false;
            for (final Component component : components)
            {
                result = result || exists(component);
            }

            return result;
        }

        @Override
        public void validate(final Class<?> caller)
        {
            Log.infoWhenDev("{0} has not...", caller.getSimpleName());

            for (final Component component : components)
            {
                Assert.assertFalse(
                            MessageFormat.format("Component ''{0}'' found but NOT expected", component.getClass().getSimpleName()),
                                exists(component));
            }
        }

        @Override
        public boolean matches(final Class<?> caller)
        {
            Log.infoWhenDev("{0} has not...", caller.getSimpleName());
            return !exists();
        }
    }

    /**
     * Only one of the list exists
     *
     * @author rschwietzke
     */
    public class HasOneOf implements ValidateType
    {
        protected final Component[] components;

        HasOneOf(final Component... components)
        {
            this.components = components;
        }

        @Override
        public boolean matches(final Class<?> caller)
        {
            Log.infoWhenDev("{0} has one of...", caller.getSimpleName());

            // count down if not found
            int counter = components.length;

            Log.infoWhenDev("  OneOf [");

            for (final Component c : components)
            {
                final boolean result = exists(c);
                if (result == false)
                {
                    counter--;
                }
            }
            Log.infoWhenDev("  ] = {0}", counter == 1);

            // if we have one left, super, otherwise we either found none or more than one
            return counter == 1 ? true : false;
        }

        @Override
        public void validate(final Class<?> caller)
        {
            Assert.assertTrue(
                            MessageFormat.format("Components NOT or TOO MANY found but only one expected: [{0}, {1}...]", (Object[]) components),
                            matches(caller));
        }
    }

    public class HasAnyOf implements ValidateType
    {
        protected final ValidateType[] expressions;

        public HasAnyOf(final ValidateType[] expressions)
        {
            this.expressions = expressions;
        }

        @Override
        public void validate(final Class<?> caller)
        {
            Assert.assertTrue("None of the expression matches", matches(caller));
        }

        @Override
        public boolean matches(final Class<?> caller)
        {
            for (final ValidateType v : expressions)
            {
                try
                {
                    v.validate(this.getClass());
                    // ValidateType exists -> we can confirm that expression hasAnyOf
                    return true;
                }
                catch (final AssertionError assertion)
                {
                    // suppress assertion -> we only need to know whether one ValidateTyp exists
                }
            }

            return false;
        }
    }

    public Has has(final Component... components)
    {
        return new Has(components);
    }

    public HasNot hasNot(final Component... components)
    {
        return new HasNot(components);
    }

    public HasAnyOf hasAnyOf(final ValidateType... expressions)
    {
        return new HasAnyOf(expressions);
    }

    public HasOneOf hasOneOf(final Component... components)
    {
        return new HasOneOf(components);
    }

    public void validate(final String msg, final ValidateType... toValidate)
    {
        // check if we want to convey a message
        if (msg != null)
        {
            Log.infoWhenDev(msg);
        }

        for (final ValidateType v : toValidate)
        {
            v.validate(this.getClass());
        }
    }

    public void validate(final ValidateType... toValidate)
    {
        validate(null, toValidate);
    }

    public boolean matches(final String msg, final ValidateType... toValidate)
    {
        // result
        boolean result = true;

        // check if we want to convey a message
        if (msg != null)
        {
            Log.infoWhenDev(msg);
            Log.infoWhenDev("[");
        }

        for (final ValidateType v : toValidate)
        {
            result = result && v.matches(this.getClass());

            // see if we can break earlier, because the result stays the same
            // and we safe cycles, but only during load testing
            if (result == false && Context.isLoadTest)
            {
                // short cut
                break;
            }
        }

        // check if we want to convey a message
        if (msg != null)
        {
            Log.infoWhenDev("Result = {0}]", result);
        }

        return result;
    }

    public boolean matches(final ValidateType... toValidate)
    {
        return matches(null, toValidate);
    }

    public static boolean hostContains(final String hostSubString)
    {
        return Context.getPage().getUrl().getHost().contains(hostSubString);
    }

    /**
     * Find elements in current page.<br>
     * This is just a shortcut for:<br>
     * <code>HPU.find().in(currentPage)</code>
     */
    public static By find()
    {
        return HPU.find().in(Context.getPage());
    }

    /**
     * Returns the body element of the current page.
     *
     * @return the body
     */
    public static HtmlElement getBody()
    {
        return Context.getPage().getBody();
    }

    /**
     * Get us the current page
     *
     * @return the current page
     */
    public static HtmlPage getCurrent()
    {
        return Context.getPage();
    }

    /**
     * Creates a new element without checkout if this already exists. Should be used if we know that
     * it cannot exist.
     *
     * @param id
     *            the id for the wanted element
     * @param parent
     *            the parent node where to create into
     * @return the found or created element
     */
    public static HtmlElement createByID(final String id, final HtmlElement parent)
    {
        final HtmlElement element = HtmlPageUtils.createHtmlElement("div", parent);
        element.setAttribute("id", id);

        return element;
    }

    /**
     * Creates a new element without checkout if this already exists. Should be used if we know that
     * it cannot exist.
     *
     * @param id
     *            the id for the wanted element
     * @return the found or created element
     */
    public static HtmlElement createByID(final String id)
    {
        return createByID(id, getBody());
    }

    /**
     * Searches an element by a given id in the current page. If no such element exists, create one
     * in the body of the current page.
     *
     * @param id
     *            the id for the wanted element
     * @return the found or created element
     */
    public static HtmlElement getOrCreateByID(final String id)
    {
        return getOrCreateByID(id, getBody());
    }

    /**
     * Searches an element by a given ID in the current page. If no such element
     * exists, create one in the given parent node.
     *
     * @param id
     *            the id for the wanted element
     * @param parent
     *            the parent node where to create into
     * @return the found or created element
     * @throws AssertionError
     *             If an element with given ID exists, but it is not a
     *             descendant of the given parent.
     * @throws AssertionError
     *             There are multiple elements with the given ID
     */
    public static HtmlElement getOrCreateByID(final String id, final HtmlElement parent)
    {
        final HtmlElement lookupResult;

        final LookUpResult element = find().byId(id);
        if (!element.exists())
        {
            lookupResult = HtmlPageUtils.createHtmlElement("div", parent);
            lookupResult.setAttribute("id", id);
        }
        else
        {
            lookupResult = element.asserted("Too many elements with ID " + id).single();

            // in case we are looking for an element inside a certain parent
            // container, check we found it at the right position
            if (!"body".equals(parent.getNodeName()))
            {
                Assert.assertTrue("Element with ID '" + id + "' found, but at unexpected position (not descendant of given parent).", DOMUtils.isSubnodeOf(lookupResult, parent));
            }
        }

        return lookupResult;
    }

    /**
     * Turns a passed url into a fully qualified one in the context of the current page. If there is not context yet, we return what we got to avoid breaking.
     *
     * @param url
     *            the relative url
     * @return the fully qualified url or an exception if we cannot create a full url yet due to the lack of context
     */
    public static String makeFullyQualifiedUrl(final String url)
    {
        // if we do not have a context yet, because this is with the very first call, we just ignore that and return what we got
        // in case that url is incorrect, the later call will fail
        if (Context.getPage() == null)
        {
            throw new RuntimeException("No context available to build full url from relative url.");
        }

        try
        {
            final URL pageURL = Context.getPage().getFullyQualifiedUrl(url);

            return pageURL.toString();
        }
        catch (final MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a renderer with Page context
     *
     * @return renderer with Page context of the current page
     */
    public static HtmlRenderer renderHtml()
    {
        return HtmlRenderer.page();
    }

    /**
     * Return a renderer with Page context
     *
     * @return renderer with Page context of the current page
     */
    public static HtmlMapRenderer renderHtmlAndMap()
    {
        return HtmlMapRenderer.page();
    }

    /**
     * Return a renderer with Page context
     *
     * @return renderer with Page context of the current page
     */
    public static HtmlMapper mapHtml()
    {
        return HtmlMapper.page(Context.getPage());
    }

    /**
     * Filter predicate for links without href, local anchors (#), java script or empty href
     * attributes.
     */
    public static Predicate<HtmlElement> VALIDLINKS = e ->
    {
        // avoid links without href
        if (e.hasAttribute("href") == false)
        {
            return false;
        }

        // get the attribute
        final String value = e.getAttribute("href").trim();

        // empty attributes
        if (value.length() == 0)
        {
            return false;
        }

        // avoid hash links
        if (value.startsWith("#"))
        {
            return false;
        }

        // avoid javascript links
        if (value.startsWith("javascript"))
        {
            return false;
        }

        // ok, all fine, say true and keep it
        return true;
    };
}
