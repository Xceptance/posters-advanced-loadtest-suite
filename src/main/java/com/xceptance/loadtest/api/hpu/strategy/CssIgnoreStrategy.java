package com.xceptance.loadtest.api.hpu.strategy;

import java.util.Collections;
import java.util.List;

import org.htmlunit.html.DomNode;
import com.google.common.collect.ImmutableList;
import com.xceptance.loadtest.api.hpu.Strategy;

/**
 * CSS lookup strategy.
 * 
 * @autor Xceptance Software Technologies
 */
public class CssIgnoreStrategy extends AbstractLookupStrategy
{
    /**
     * CSS ignore strategy constructor.
     *
     * @param parent
     *            parent ignore strategy
     * @param locator
     *            locator string
     */
    public CssIgnoreStrategy(final Strategy parentStrategy, final String locator)
    {
        super(parentStrategy, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        // Keep the parent if NO elements were found, as we are in an IGNORE strategy. Drop it otherwise.
        return parent.querySelectorAll(getLocator()).isEmpty() ? ImmutableList.of(parent) : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "CSS-Ignore";
    }
}