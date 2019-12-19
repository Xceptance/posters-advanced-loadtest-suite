package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.account.CreateAccountPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Opens the account creation page.
 * 
 * @author Xceptance Software Technologies
 */
public class GoToCreateAccount extends PageAction<GoToCreateAccount>
{
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(GeneralPages.instance.user.getCreateAccountLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        CreateAccountPage.instance.validate();
    }
}