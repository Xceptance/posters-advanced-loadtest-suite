package com.xceptance.loadtest.posters.pages.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

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
