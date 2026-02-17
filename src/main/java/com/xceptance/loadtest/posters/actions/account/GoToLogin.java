package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.HtmlUnitPageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.account.LoginPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Opens the login page.
 * 
 * @author Xceptance Software Technologies
 */
public class GoToLogin extends HtmlUnitPageAction<GoToLogin>
{
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(GeneralPages.instance.user.getLoginLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        LoginPage.instance.validate();
    }
}
