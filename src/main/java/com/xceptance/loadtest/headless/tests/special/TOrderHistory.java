package com.xceptance.loadtest.headless.tests.special;

import java.util.Optional;

import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.AccountSupplierManager;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.account.Logout;
import com.xceptance.loadtest.headless.flows.LoginFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;

/**
 * Open landing page and navigate to the registration form. Register a new customer and log out afterwards.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TOrderHistory extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // read the account.csv which was set in the project properties and set it into the context
        final Optional<Account> fromFile = AccountSupplierManager.getFromFile();
        Context.get().data.setAccount(fromFile);

        // Login user
        new LoginFlow().run();

        // Logout from freshly created account
        new Logout().run();
    }
}
