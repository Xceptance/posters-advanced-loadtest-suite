package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Checkout button component.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutButton implements Component
{
    public final static CheckoutButton instance = new CheckoutButton();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("btn-start-checkout");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}