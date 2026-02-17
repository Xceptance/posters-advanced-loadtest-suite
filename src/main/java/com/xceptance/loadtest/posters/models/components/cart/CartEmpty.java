package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Cart empty indicator component.
 * 
 * @author Xceptance Software Technologies
 */
public class CartEmpty implements HtmlComponent
{
	public static final CartEmpty instance = new CartEmpty();

    @Override
    public LookUpResult locate()
    {
    	return Page.find().byId("error-cart-message");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}