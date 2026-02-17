package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.posters.util.Context;
import com.xceptance.loadtest.posters.actions.account.CreateAccount;
import com.xceptance.loadtest.posters.actions.account.GoToCreateAccount;

/**
 * Opens the account creation page and create a new account.
 * 
 * @author Xceptance Software Technologies
 */
public class CreateAccountFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // Open create account page
        new GoToCreateAccount().run();

        // Fill form and submit new account
        new CreateAccount(Context.get().testData.getAccount().get()).run();

        return true;
    }
}