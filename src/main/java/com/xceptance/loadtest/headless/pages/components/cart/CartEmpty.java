package com.xceptance.loadtest.headless.pages.components.cart;

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
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".page > .container.cart-empty");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
