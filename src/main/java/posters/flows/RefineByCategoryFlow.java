package posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.catalog.RefineByCategory;

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
