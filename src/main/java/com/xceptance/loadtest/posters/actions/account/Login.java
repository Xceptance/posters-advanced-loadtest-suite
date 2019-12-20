package com.xceptance.loadtest.posters.actions.account;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.account.LoginPage;
import com.xceptance.loadtest.posters.models.pages.general.HomePage;

/**
 * Logs in with the given account.
 * 
 * @author Xceptance Software Technologies
 */
public class Login extends PageAction<Login>
{
    private final Account account;

    public Login(final Account account)
    {
        this.account = account;
    }

    @Override
    protected void doExecute() throws Exception
    {
        LoginPage.instance.loginForm.fillLoginForm(account);
        
        loadPageByClick(LoginPage.instance.loginForm.getSignInButton());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();
        
        Assert.assertTrue("User is not logged in", HomePage.instance.user.isLoggedIn());
    }
}