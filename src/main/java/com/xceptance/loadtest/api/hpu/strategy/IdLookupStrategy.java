package com.xceptance.loadtest.api.hpu.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * ID lookup strategy.
 * 
 * @autor Xceptance Software Technologies
 */
public class IdLookupStrategy extends AbstractLookupStrategy
{
    /**
     * ID lookup strategy constructor.
     *
     * @param parent
     *            lookup base
     * @param locator
     *            locator string
     */
    public IdLookupStrategy(final DomNode parent, final String locator)
    {
        super(parent, locator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<?> lookup(final DomNode parent)
    {
        final DomNode result = ((HtmlPage) parent.getPage()).getElementById(getLocator());
        if (result != null)
        {
            final List<DomNode> results = new ArrayList<>(1);
            results.add(result);
            return results;
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStrategyName()
    {
        return "ID";
    }
}