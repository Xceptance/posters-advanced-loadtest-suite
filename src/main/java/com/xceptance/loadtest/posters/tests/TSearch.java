package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.posters.util.LoadTestCase;
import com.xceptance.loadtest.posters.util.Context;
import com.xceptance.loadtest.posters.flows.NavigateToProductPageFlow;
import com.xceptance.loadtest.posters.flows.SearchFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Starts visit at landing page, searches, executes product listing page actions and
 * visits product pages.
 * 
 * @author Xceptance Software Technologies
 */
public class TSearch extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page
        new VisitFlow().run();

        // Determine the number of times to execute a search operation
        final int searches = Context.get().configuration.searchesCount.value;
        for (int i = 0; i < searches; i++)
        {
            // Perform a search operation
            new SearchFlow().run();
            
            final int productPageRounds = Context.get().configuration.browseRefineFlow.value;
            for (int j = 0; j < productPageRounds; j++)
            {
                // Browse the resulting pages and open product detail pages
                new NavigateToProductPageFlow().run();
            }
        }
    }
}