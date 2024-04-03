package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Cart banner (title) component.
 * 
 * @author Xceptance Software Technologies
 */
public class CartBanner implements Component
{
	public static final CartBanner instance = new CartBanner();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("title-cart");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}