package com.xceptance.loadtest.headless.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.catalog.RefineByCategory;

/**
 *
 */
public class RefineByCategoryFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // If we are a typical category surfer, we might do category
        // refinement now
        if (Context.configuration().categoryRefinementProbability.random())
        {
            // Refine by category.
            new RefineByCategory().runIfPossible();
        }

        return true;
    }
}
