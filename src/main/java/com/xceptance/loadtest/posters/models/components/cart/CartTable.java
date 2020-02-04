package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Cart table component.
 * 
 * @author Xceptance Software Technologies
 */
public class CartTable implements Component
{
	public static final CartTable instance = new CartTable();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("cartOverviewTable");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    // Add line item access to work the cart (e.g. remove items / cart clean up)
}