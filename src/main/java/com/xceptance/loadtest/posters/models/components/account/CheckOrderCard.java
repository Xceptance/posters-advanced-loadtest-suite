package com.xceptance.loadtest.posters.models.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class CheckOrderCard implements Component
{
    public final static CheckOrderCard instance = new CheckOrderCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("linkOrderOverview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
