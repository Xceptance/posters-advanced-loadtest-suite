package com.xceptance.loadtest.posters.models.pages.account;

import com.xceptance.loadtest.posters.models.components.account.CreateAccountCard;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

public class CreateAccountPage extends GeneralPages
{
    public static final CreateAccountPage instance = new CreateAccountPage();
    
    public final CreateAccountCard createAccountCard = CreateAccountCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(createAccountCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(createAccountCard));
    }
}
