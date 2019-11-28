package com.xceptance.loadtest.headless.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.account.GoToLogin;
import com.xceptance.loadtest.headless.actions.account.Login;

/**
 * Create a new account
 *
 * @author rschwietzke
 *
 */
public class LoginFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // goto sign up
        new GoToLogin().run();

        // fill form and submit new account
        new Login(Context.get().data.getAccount().get()).run();

        return true;
    }
}
