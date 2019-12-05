package posters.tests.debug;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.account.Logout;
import posters.flows.CreateAccountFlow;
import posters.flows.LoginFlow;
import posters.flows.VisitFlow;

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
