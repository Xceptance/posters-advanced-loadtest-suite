package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.pages.general.HomepagePage;

public class Logout extends PageAction<Logout>
{
    @Override
    protected void doExecute() throws Exception
    {
        // click on checkout button
        loadPageByClick(GeneralPages.instance.user.getLogoutLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        HomepagePage.instance.validate();
        HomepagePage.instance.miniCart.isEmpty();
        HomepagePage.instance.user.isNotLoggedIn();
    }
}
