package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.HtmlUnitLoadTestCase;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Visits the landing page.
 * 
 * @author Xceptance Software Technologies
 */
public class TVisit extends HtmlUnitLoadTestCase
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