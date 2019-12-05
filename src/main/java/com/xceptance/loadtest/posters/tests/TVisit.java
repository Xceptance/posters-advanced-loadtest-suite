package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Single click visitor. The visitor opens the landing page and will not do any interaction.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class TVisit extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page and leave immediately.
        new VisitFlow().run();
    }
}
