package com.xceptance.loadtest.posters.actions.account;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.account.CreateAccountPage;
import com.xceptance.loadtest.posters.models.pages.account.LoginPage;

/**
 * Creates a new account.
 * 
 * @author Xceptance Software Technologies
 */
public class CreateAccount extends PageAction<CreateAccount>
{
    private final Account account;

    public CreateAccount(final Account account)
    {
        this.account = account;
    }

    @Override
    protected void doExecute() throws Exception
    {
        CreateAccountPage.instance.createAccountForm.fillCreateAccountForm(account);
       
        loadPageByClick(CreateAccountPage.instance.createAccountForm.getCreateAccountButton());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        Assert.assertTrue("Failed to register. Expected login form.", LoginPage.instance.loginForm.exists());        

        account.isRegistered = true;
    }
}