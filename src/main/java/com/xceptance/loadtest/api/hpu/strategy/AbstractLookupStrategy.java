package com.xceptance.loadtest.api.hpu.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.loadtest.api.hpu.Strategy;

/**
 * Common lookup strategy methods.
 */
public abstract class AbstractLookupStrategy implements Strategy
{
    /**
     * Node/Strategy based handler.
     */
    private final BaseHandler baseHandler;

    /**
     * Element locator.
     */
    private final String locator;

    /**
     * Lookup elements based on the given element.
     *
     * @param parent
     *            lookup base
     * @param locator
     *            element locator
     */
    protected AbstractLookupStrategy(final DomNode parent, final String locator)
    {
        ParameterCheckUtils.isNonEmptyString(locator, getStrategyName() + " locator");

        this.baseHandler = new DomNodeBaseHandler(parent);
        this.locator = locator;
    }

    /**
     * Lookup elements based on the the predecessor's results.
     *
     * @param parentStrategy
     *            predecessor in strategy chain
     * @param locator
     *            element locator
     */
    protected AbstractLookupStrategy(final Strategy parentStrategy, final String locator)
    {
        ParameterCheckUtils.isNonEmptyString(locator, getStrategyName() + " locator");

        this.baseHandler = new StrategyBaseHandler(parentStrategy);
        this.locator = locator;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLocator()
    {
        return locator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocatorDescription()
    {
        return baseHandler.getLocatorDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> lookup()
    {
        return baseHandler.baseLookup();
    }

    /**
     * Get the the lookup strategy's name.
     *
     * @return
     */
    abstract protected String getStrategyName();

    /**
     * Get the locator's results based on the given parent element.
     *
     * @param parent
     *            lookup base
     * @return the results according to the given locator
     */
    protected abstract List<?> lookup(final DomNode parent);

    private abstract class BaseHandler
    {
        /**
         * Lookup elements
         *
         * @return list of result elements (never <code>null</code>).
         */
        abstract List<?> baseLookup();

        /**
         * Get Locator description in format <code>{&lt;LookupStrategyName&gt;=&lt;locator&gt;}</code> and its
         * predecessors if any. If a predecessor failed to find elements the current locator description is not
         * appended.
         *
         * @return locator description
         */
        abstract String getLocatorDescription();
    }

    /**
     * Lookup is based on element.
     */
    private class DomNodeBaseHandler extends BaseHandler
    {
        /**
         * Parent node the lookup is based on.
         */
        private final DomNode parent;

        /**
         * Constructor
         *
         * @param parent
         *            Parent node the lookup is based on.
         */
        private DomNodeBaseHandler(final DomNode parent)
        {
            this.parent = parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        List<?> baseLookup()
        {
            return lookup(parent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        String getLocatorDescription()
        {
            return new StringBuilder().append("{").append(getStrategyName()).append("=")
                                      .append(getLocator())
                                      .append("}").toString();
        }
    }

    /**
     * Lookup is based on proceeding strategy chain results.
     */
    private class StrategyBaseHandler extends BaseHandler
    {
        /**
         * Proceeding strategy in chain.
         */
        private final Strategy parentStrategy;

        /**
         * Parent strategy (proceeding strategy) has not any resulting elements.
         */
        private boolean parentLookupFailed = false;

        /**
         * Constructor
         *
         * @param parentStrategy
         *            proceeding strategy
         */
        private StrategyBaseHandler(final Strategy parentStrategy)
        {
            this.parentStrategy = parentStrategy;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        List<?> baseLookup()
        {
            // if we have our empty strategy, it has no parent or page for performance reasons
            if (parentStrategy != null)
            {
                @SuppressWarnings("unchecked")
                final List<DomNode> parentResults = (List<DomNode>) parentStrategy.lookup();

                if (!parentResults.isEmpty())
                {

                    final List<Object> results = new ArrayList<>();
                    for (final DomNode parentResult : parentResults)
                    {
                        results.addAll(lookup(parentResult));
                    }
                    return results;
                }
            }

            parentLookupFailed = true;
            return Collections.emptyList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        String getLocatorDescription()
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(parentStrategy.getLocatorDescription());
            // if this parent strategy had at least 1 result, append chain connector
            if (!parentLookupFailed)
            {
                sb.append(" -> ");
            }

            // if the parent strategy had results append the local strategy and locator
            if (!parentLookupFailed)
            {
                sb.append("{").append(getStrategyName()).append("=").append(getLocator())
                  .append("}");
            }
            return sb.toString();
        }
    }
}
