package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Indicator of an empty cart
 *
 * @author rschwietzke
 *
 */
public class CartEmpty implements Component
{
    public final static CartEmpty instance = new CartEmpty();

    @Override
    public LookUpResult locate()
    {
    	// Detect 'cart empty' error
    	return Page.find().byId("errorCartMessage");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
