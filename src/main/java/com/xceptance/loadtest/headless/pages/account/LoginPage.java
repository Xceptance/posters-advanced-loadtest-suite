package com.xceptance.loadtest.headless.pages.account;

import com.xceptance.loadtest.headless.pages.components.account.CheckOrderCard;
import com.xceptance.loadtest.headless.pages.components.account.LoginAndCreateAccountCard;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

public class LoginPage extends GeneralPages
{
    public static final LoginPage instance = new LoginPage();

    public final CheckOrderCard checkOrderCard = CheckOrderCard.instance;
    public final LoginAndCreateAccountCard loginAndCreateAccountCard = LoginAndCreateAccountCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(checkOrderCard, loginAndCreateAccountCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(checkOrderCard, loginAndCreateAccountCard));
    }
}
