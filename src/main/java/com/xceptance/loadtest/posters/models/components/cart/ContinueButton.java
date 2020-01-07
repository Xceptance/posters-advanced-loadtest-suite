package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Continue checkout button component.
 * 
 * @author Xceptance Software Technologies
 */
public class ContinueButton implements Component
{
    public final static ContinueButton instance = new ContinueButton();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("btnAddDelAddr");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}