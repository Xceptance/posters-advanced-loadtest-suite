package com.xceptance.loadtest.posters.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

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
