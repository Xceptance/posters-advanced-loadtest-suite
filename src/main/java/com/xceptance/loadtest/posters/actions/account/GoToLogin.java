package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.pages.account.LoginPage;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class GoToLogin extends PageAction<GoToLogin>
{
    @Override
    protected void doExecute() throws Exception
    {
        // click on login button
        loadPageByClick(GeneralPages.instance.user.getLoginLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        LoginPage.instance.validate();
    }
}
