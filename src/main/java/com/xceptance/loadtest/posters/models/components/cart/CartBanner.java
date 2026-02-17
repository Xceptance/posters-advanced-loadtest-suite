package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Cart banner (title) component.
 * 
 * @author Xceptance Software Technologies
 */
public class CartBanner implements HtmlComponent
{
	public static final CartBanner instance = new CartBanner();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("cart-title");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}