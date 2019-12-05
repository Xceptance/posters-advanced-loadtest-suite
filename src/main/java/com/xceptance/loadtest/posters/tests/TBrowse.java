package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.flows.CategoryFlow;
import com.xceptance.loadtest.posters.flows.RefineByFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Open the landing page, browse the catalog. If there's a result grid open a random product's quick or detail view.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TBrowse extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */

    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Determine how often we want to decent from the top categories into the catalog
        final int rounds = Context.configuration().fullBrowseFlow.value;

        for (int i = 0; i < rounds; i++)
        {
            final int categoryRounds = Context.configuration().browseCategoriesFlow.value;

            for (int j = 0; j < categoryRounds; j++)
            {
                // work on categories
                new CategoryFlow().run();
            }

            final int refineRounds = Context.configuration().browseRefineFlow.value;

            for (int j = 0; j < refineRounds; j++)
            {
                // Browse the results, open product details, and configure product
                // if possible
                new RefineByFlow().run();
            }
        }
    }
}
