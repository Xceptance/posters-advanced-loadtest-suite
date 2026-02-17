package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.posters.util.Context;
import com.xceptance.loadtest.posters.actions.Homepage;

/**
 * Visits the given (home) page.
 * 
 * @author Xceptance Software Technologies
 */
public class VisitFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        new Homepage(Context.get().configuration.siteUrlHomepage).run();

        return true;
    }
}