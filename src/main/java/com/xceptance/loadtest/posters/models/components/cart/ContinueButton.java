package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Continue checkout button component.
 * 
 * @author Xceptance Software Technologies
 */
public class ContinueButton implements HtmlComponent
{
    public static final ContinueButton instance = new ContinueButton();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("btn-add-shipping-address");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}