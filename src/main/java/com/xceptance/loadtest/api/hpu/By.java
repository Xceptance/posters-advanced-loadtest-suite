package com.xceptance.loadtest.api.hpu;

import org.apache.commons.lang3.StringUtils;

import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.hpu.strategy.CssLookupStrategy;
import com.xceptance.loadtest.api.hpu.strategy.IdLookupStrategy;
import com.xceptance.loadtest.api.hpu.strategy.XPathLookupStrategy;
import com.xceptance.xlt.api.engine.Session;

/**
 * Lookup strategy.
 * 
 * @autor Xceptance Software Technologies
 */
public class By
{
    private static final boolean isDevMode = !Session.getCurrent().isLoadTest();

    /**
     * Lookup base.
     */
    private final DomNode parent;

    /**
     * The strategy the current strategy is based on.
     */
    private final Strategy parentStrategy;

    /**
     * Constructor. Initializes object with lookup base.
     *
     * @param parent
     *            lookup base
     */
    public By(final DomNode parent)
    {
        this.parent = parent;
        this.parentStrategy = null;
    }

    /**
     * Constructor. Initializes object with a parent strategy. The current
     * lookup is based on the results of the given parent strategy.
     *
     * @param parentStrategy
     *            parent strategy base
     */
    public By(final Strategy parentStrategy)
    {
        this.parent = null;
        this.parentStrategy = parentStrategy;
    }

    /**
     * Set XPath lookup strategy and locator.
     *
     * @param locator
     *            XPath locator
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult byXPath(final String locator) throws IllegalArgumentException
    {
        // Check if it's a nested lookup
        if (parentStrategy != null && (parent == null || !(parent instanceof HtmlPage)))
        {
            ParameterCheckUtils.isNonEmptyString(locator, "locator");

            // absolute XPath as well as ID function are not allowed for nested
            // XPaths
            final String trimmedLocator = locator.trim();
            if (trimmedLocator.startsWith("/") || trimmedLocator.startsWith("id"))
            {
                throw new IllegalArgumentException("Absolute XPath or ID-function not allowed in nested lookup. Use relative path instead.");
            }
        }
        else if (isDevMode)
        {
            ParameterCheckUtils.isNonEmptyString(locator, "locator");

            // extract ID
            final String id = RegExUtils.getFirstMatch(locator.trim(), "^id\\(['\"]([^'\")]+)['\"]\\)", 1);
            checkForDoubleIds(id);
        }

        final Strategy strategy = parent != null ? new XPathLookupStrategy(parent, locator) : new XPathLookupStrategy(parentStrategy, locator);
        return by(strategy);
    }

    /**
     * Set CSS lookup strategy and locator.
     *
     * @param locator
     *            CSS locator
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult byCss(final String locator) throws IllegalArgumentException
    {
        final Strategy strategy = parent != null ? new CssLookupStrategy(parent, locator) : new CssLookupStrategy(parentStrategy, locator);
        return by(strategy);
    }

    /**
     * Set ID lookup strategy and ID. The ID lookup strategy finds the first
     * element with the given ID.
     *
     * @param id
     *            the element's ID
     * @return {@link LookUpResult} object to query the result from
     * @throws IllegalArgumentException
     *             if given ID is <code>null</code> or empty
     */
    public LookUpResult byId(final String id) throws IllegalArgumentException
    {
        if (isDevMode)
        {
            checkForDoubleIds(id);
        }

        return by(new IdLookupStrategy(parent, id));
    }

    /**
     * Get new result object based on given lookup strategy
     *
     * @param strategy
     *            lookup strategy
     * @return result
     */
    protected LookUpResult by(final Strategy strategy)
    {
        return new LookUpResult(strategy);
    }

    /**
     * Checks if an ID is used multiple times in the current page or element. If
     * so an IllegalStateException is thrown.
     *
     * @param id
     *
     * @throws IllegalStateException
     *             if id is used multiple times in page
     */
    private void checkForDoubleIds(final String id)
    {
        // This is checked and handled in other places so we need to ignore it
        if (StringUtils.isBlank(id))
        {
            return;
        }

        final In find = HPU.find();

        final By in;
        if (parent instanceof HtmlPage)
        {
            in = find.in((HtmlPage) parent);
        }
        else
        {
            in = find.in((HtmlElement) parent);
        }

        final int count = in.byCss("#" + id).count();
        if (count > 1)
        {
            throw new IllegalStateException("Id " + id + " is used multiple times in page.");
        }
    }
}