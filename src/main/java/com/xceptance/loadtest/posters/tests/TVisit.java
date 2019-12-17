package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Visits the landing page.
 */
public class TVisit extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Visits the landing page and leave immediately
        new VisitFlow().run();
    }
}