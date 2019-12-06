package com.xceptance.loadtest.posters.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public class ProfileCard implements Component
{
    public final static ProfileCard instance = new ProfileCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("linkPersonalData");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
