package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.account.GoToLogin;
import com.xceptance.loadtest.posters.actions.account.Login;

/**
 * Create a new account
 *
 * @author rschwietzke
 */
public class LoginFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // Go to login page
        new GoToLogin().run();

        // Fill form and login
        new Login(Context.get().data.getAccount().get()).run();

        return true;
    }
}