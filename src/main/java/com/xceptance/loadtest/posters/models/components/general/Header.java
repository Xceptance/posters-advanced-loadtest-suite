package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class Header implements Component
{
    public final static Header instance = new Header();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("header");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
