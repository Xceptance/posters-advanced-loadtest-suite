package com.xceptance.loadtest.api.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Utils for DOM manipulation
 *
 * @author rschwietzke
 *
 */
public class DOMUtils
{
    /**
     * Append to the current element's content
     *
     * @param container
     *            the container to append to
     * @param content
     *            the content to parse in
     */
    public static void appendElement(final HtmlElement container, final String content)
    {
        try
        {
            HTMLParser.parseFragment(container, content);
        }
        catch (SAXException | IOException e)
        {
            // let's not discuss the details, we got a problem
            throw new AssertionError(e.getMessage(), e);
        }
    }

    /**
     * Replaces the entire content
     *
     * @param container
     *            the container to clean and insert into
     * @param content
     *            the content to parse in
     */
    public static void replaceContent(final HtmlElement container, final String content)
    {
        // get the target container and remove all of its children
        container.removeAllChildren();

        // parse the new content into the container
        appendElement(container, content);
    }

    /**
     * Replaces a full element
     *
     * @param container
     *            the element to replace
     * @param content
     *            the cotnent to parse in
     */
    public static HtmlElement replaceElement(final HtmlElement container, final String content)
    {
        Assert.assertNotNull("Container most not be null", container);

        try
        {
            // create a temporary container to parse the new content into
            final HtmlElement tmp = HtmlPageUtils.createHtmlElement("div", Page.getBody());
            HTMLParser.parseFragment(tmp, content);

            // ok, if we have more than on child in our tmp node, we cannot simply replace
            // the container, because that is not possible, hence we complain
            Assert.assertTrue("You can only replace a node with a single node.", tmp.getChildElementCount() == 1);

            final DomElement child = tmp.getChildElements().iterator().next();
            container.replace(child);

            // and erase the temporary container
            tmp.remove();

            return (HtmlElement) child;
        }
        catch (SAXException | IOException e)
        {
            // let's not discuss the details, we got a problem
            throw new AssertionError(e.getMessage(), e);
        }

    }

    public static boolean isSubnodeOf(final DomNode element, final DomNode ancestor)
    {
        if (element == null || ancestor == null)
        {
            return true;
        }

        final DomNode parent = element.getParentNode();

        // we do not have a parent at all
        if (parent == null)
        {
            return false;
        }

        // we have a match
        if (parent.equals(ancestor))
        {
            return true;
        }
        else
        {
            // we need to check further
            return isSubnodeOf(parent, ancestor);
        }
    }

    /**
     * Adds a class
     *
     * @param element
     *            the element to change
     * @param className
     *            class to add
     * @return the passed element
     */
    public static HtmlElement addClass(final HtmlElement element, final String className)
    {
        final String classValue = element.getAttribute("class");

        // short cut if we do not have anything yet
        if (classValue == null || classValue.length() == 0)
        {
            element.setAttribute("class", className);
            return element;
        }

        // if we do not know it, append it simply
        if (!classValue.contains(className))
        {
            element.setAttribute("class", classValue.length() == 0 ? className : classValue + " " + className);
            return element;
        }

        // we cannot be sure if this is not a substring and not a real full class, hence we have
        // to split it up first

        final StringBuilder sb = new StringBuilder(128);

        Arrays.stream(classValue.split("\\s"))
                        .forEach(s ->
                        {
                            // if we have it, skip it, append it later again to show that we have
                            // done stuff
                            if (!className.equals(s))
                            {
                                if (sb.length() > 0)
                                {
                                    sb.append(" ");
                                }
                                sb.append(s);
                            }
                            else
                            {
                                // skip it
                            }
                        });

        if (sb.length() > 0)
        {
            sb.append(" ");
        }
        sb.append(className);

        element.setAttribute("class", sb.toString());
        return element;
    }

    /**
     * Removes a class
     *
     * @param element
     *            the element to change
     * @param className
     *            class to add
     * @return the passed element
     */
    public static HtmlElement removeClass(final HtmlElement element, final String className)
    {
        final String classValue = element.getAttribute("class");

        // short cut if we do not have anything yet
        if (classValue == null || classValue.length() == 0)
        {
            return element;
        }

        // we cannot be sure if this is not a substring and not a reall full class, hence we have
        // to split it up first

        final StringBuilder sb = new StringBuilder(128);
        Arrays.stream(classValue.split("\\s"))
                        .forEach(s ->
                        {
                            if (!s.equals(className))
                            {
                                if (sb.length() > 0)
                                {
                                    sb.append(" ");
                                }
                                sb.append(s);
                            }
                            else
                            {
                                // skip it
                            }
                        });

        element.setAttribute("class", sb.toString());
        return element;
    }

    /**
     * Checks if a given element contains one or more classes.
     *
     * @param element
     *            the element to check
     * @param className
     *            class to search
     * @return true if we have this class
     */
    public static boolean hasClasses(final HtmlElement element, final String... classNames)
    {
        final String classValue = element.getAttribute("class");

        // short cut if we do not have anything yet
        if (classValue == null || classValue.length() == 0)
        {
            return false;
        }

        // quick set for easier checking
        final Set<String> currentClasses = new HashSet<>(4 * classNames.length + 1);
        Arrays.stream(classValue.split("\\s")).forEach(name -> currentClasses.add(name));

        // we cannot be sure if this is not a substring and not a reall full class, hence we have
        // to split it up first
        // TODO Split up cache, because it is almost the same all the time, we could even cache
        // the result and avoid that as well.
        for (final String classToTest : classNames)
        {
            if (!currentClasses.contains(classToTest))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a given element contains one or more classes.
     *
     * A CSS class locator is a class name in CSS style, i.e. '.class' or '#id'
     *
     * @param element
     *            The element to check
     * @param classLocators
     *            The class locator to search for.<br>
     *            Works for single classes only.<br>
     *            good:<code>".foo"</code><br>
     *            bad: <code>".foo.bar"</code>, <code>".foo .bar"</code>,
     * @return <code>True</code> if at least one given CSS class was found, <code>false</code>
     *         otherwise
     */
    public static boolean hasClassLocators(final HtmlElement element, final String... classLocators)
    {
        final String[] classNames = new String[classLocators.length];

        for (int i = 0; i < classLocators.length; i++)
        {
            classNames[i] = classLocators[i].replace(".", "");
        }

        return hasClasses(element, classNames);
    }
}
