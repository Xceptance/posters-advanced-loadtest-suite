package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.posters.util.LoadTestCase;
import com.xceptance.loadtest.posters.util.Context;
import com.xceptance.loadtest.posters.flows.NavigateCategoriesFlow;
import com.xceptance.loadtest.posters.flows.NavigateToProductPageFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Starts visit at landing page, browses categories or searches, executes product listing page actions
 * and visits product pages.
 * 
 * @author Xceptance Software Technologies
 */
public class TBrowse extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page
        new VisitFlow().run();

        // Determine the number of times to descent from the top categories into the catalog
        final int rounds = Context.get().configuration.fullBrowseFlow.value;
        for (int i = 0; i < rounds; i++)
        {
            final int categoryRounds = Context.get().configuration.browseCategoriesFlow.value;
            for (int j = 0; j < categoryRounds; j++)
            {
                // Browse available categories
                new NavigateCategoriesFlow().run();
            }

            final int productPageRounds = Context.get().configuration.browseRefineFlow.value;
            for (int j = 0; j < productPageRounds; j++)
            {
                // Browse the resulting pages and open product detail pages
                new NavigateToProductPageFlow().run();
            }
        }
    }
}