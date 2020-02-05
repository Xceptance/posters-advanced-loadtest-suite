package com.xceptance.loadtest.api.hpu.strategy;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.loadtest.api.hpu.Strategy;

/**
 * XPath lookup strategy.
 * 
 * @autor Xceptance Software Technologies
 */
public class XPathLookupStrategy extends AbstractLookupStrategy
{
    /**
     * XPath lookup strategy constructor.
     *
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public XPathLookupStrategy(final DomNode parent, final String locator)
    {
        super(parent, locator);
    }

    /**
     * XPath lookup strategy constructor.
     *
     * @param parent
     *            parent lookup strategy
     * @param locator
     *            locator string
     */
    public XPathLookupStrategy(final Strategy parentStrategy, final String locator)
    {
        super(parentStrategy, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        // do it
        final List<?> result = parent.getByXPath(getLocator());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "XPath";
    }
}