package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.pages.account.AccountDashboardPage;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class GoToMyAccount extends PageAction<GoToMyAccount>
{
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(GeneralPages.instance.user.getMyAccountLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        AccountDashboardPage.instance.validate();
    }
}
