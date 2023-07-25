package com.xceptance.loadtest.api.hpu.strategy;

import java.util.List;

import org.htmlunit.html.DomNode;
import com.xceptance.loadtest.api.hpu.Strategy;

/**
 * CSS lookup strategy.
 * 
 * @autor Xceptance Software Technologies
 */
public class CssLookupStrategy extends AbstractLookupStrategy
{
    /**
     * CSS lookup strategy constructor.
     *
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public CssLookupStrategy(final DomNode parent, final String locator)
    {
        super(parent, locator);
    }

    /**
     * CSS lookup strategy constructor.
     *
     * @param parent
     *            parent lookup strategy
     * @param locator
     *            locator string
     */
    public CssLookupStrategy(final Strategy parentStrategy, final String locator)
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
        return parent.querySelectorAll(getLocator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "CSS";
    }
}