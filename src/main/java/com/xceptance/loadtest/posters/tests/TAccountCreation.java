package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.account.GoToMyAccount;
import com.xceptance.loadtest.posters.actions.account.Login;
import com.xceptance.loadtest.posters.actions.account.Logout;
import com.xceptance.loadtest.posters.flows.CreateAccountFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;

/**
 * Visits the home page, opens the account creation page and creates an account, logs in with the newly created account, opens the MyAccount page and logs out.
 * 
 * @author Xceptance Software Technologies
 */
public class TAccountCreation extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void test() throws Throwable
    {
        // Start at the landing page
        new VisitFlow().run();

        // Attach an account to the current context
        Context.get().data.attachAccount();

        // Register user
        new CreateAccountFlow().run();
        
        // Fill form and login
        new Login(Context.get().data.getAccount().get()).run();
        
        // Open account page
        new GoToMyAccount().run();        

        // Log out
        new Logout().run();
    }
}