package posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.account.CreateAccount;
import posters.actions.account.GoToLogin;

/**
 * Create a new account
 *
 * @author rschwietzke
 *
 */
public class CreateAccountFlow extends Flow
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
        new CreateAccount(Context.get().data.getAccount().get()).run();

        return true;
    }
}
