package com.xceptance.loadtest.posters.models.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

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
