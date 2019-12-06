package com.xceptance.loadtest.posters.pages.account;

import com.xceptance.loadtest.posters.pages.components.account.LoginCard;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class LoginPage extends GeneralPages
{
    public static final LoginPage instance = new LoginPage();

    public final LoginCard loginCard = LoginCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(loginCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(loginCard));
    }
}
