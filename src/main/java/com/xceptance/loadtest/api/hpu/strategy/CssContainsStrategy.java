package com.xceptance.loadtest.api.hpu.strategy;

import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.google.common.collect.ImmutableList;
import com.xceptance.loadtest.api.hpu.Strategy;

/**
 * CSS lookup strategy
 */
public class CssContainsStrategy extends AbstractLookupStrategy
{
    /**
     * CSS ignore strategy constructor.
     *
     * @param parent
     *            parent ignore strategy
     * @param locator
     *            locator string
     */
    public CssContainsStrategy(final Strategy parentStrategy, final String locator)
    {
        super(parentStrategy, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        // Drop the parent if NO elements were found, as we are in an CONTAINS strategy. Keep it otherwise.
        return parent.querySelectorAll(getLocator()).isEmpty() ? Collections.emptyList() : ImmutableList.of(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "CSS-Contains";
    }
}
