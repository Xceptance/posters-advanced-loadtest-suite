package com.xceptance.loadtest.headless.tests.debug;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.account.Logout;
import com.xceptance.loadtest.headless.flows.CreateAccountFlow;
import com.xceptance.loadtest.headless.flows.LoginFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;

/**
 * Simple test to get all possible add to cart operations tested easily
 */
public class TDebugRegisterAndLogin extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        Context.get().data.attachAccount();
        new CreateAccountFlow().run();
        new Logout().run();

        new LoginFlow().run();
        new Logout().run();
    }
}
