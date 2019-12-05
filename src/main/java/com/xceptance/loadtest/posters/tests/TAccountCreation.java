package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.account.Logout;
import com.xceptance.loadtest.posters.flows.CreateAccountFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Open landing page and navigate to the registration form. Register a new customer and log out afterwards.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TAccountCreation extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // we have not touched any account yet
        // attach it to the context, this method will complain if we
        // set one up already
        // the idea is that we explicitly create accounts and not magically
        // have one
        Context.get().data.attachAccount();

        // Register user
        new CreateAccountFlow().run();

        // Logout from freshly created account
        new Logout().run();
    }
}
