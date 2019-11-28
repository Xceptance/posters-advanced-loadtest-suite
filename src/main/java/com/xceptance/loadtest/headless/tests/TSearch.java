package com.xceptance.loadtest.headless.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.flows.SearchFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;

/**
 * Open the landing page and search for predefined key words as well as for
 * random string. If there are search results open a random product's quick or
 * detail view.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TSearch extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Get the number of searches determined from the configured min and max
        // products.
        final int searches = Context.configuration().searchesCount.value;
        for (int i = 0; i < searches; i++)
        {
            // Perform a search.
            new SearchFlow().run();
        }
    }
}
