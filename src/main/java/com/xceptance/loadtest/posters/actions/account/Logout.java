package com.xceptance.loadtest.posters.actions.account;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.models.pages.general.HomePage;

/**
 * Logs out.
 * 
 * @author Xceptance Software Technologies
 */
public class Logout extends PageAction<Logout>
{
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(GeneralPages.instance.user.getLogoutLink().asserted().first());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        HomePage.instance.validate();
        HomePage.instance.miniCart.isEmpty();
        HomePage.instance.user.isNotLoggedIn();
    }
}
