package com.xceptance.loadtest.posters.models.pages.cart;

import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.posters.models.components.cart.CartBanner;
import com.xceptance.loadtest.posters.models.components.cart.CartEmpty;
import com.xceptance.loadtest.posters.models.components.cart.CartTable;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents the cart page.
 * 
 * @author Xceptance Software Technologies
 */
public class CartPage extends GeneralPages
{
    public static final CartPage instance = new CartPage();

    public final CartBanner cartBanner = CartBanner.instance;
    
    public final CartEmpty cartEmpty = CartEmpty.instance;
    
    public final CartTable cartTable = CartTable.instance;
    
    @Override
    public void validate()
    {
        super.validate();

        validate(has(cartBanner), hasOneOf(cartEmpty, cartTable));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(cartBanner), hasOneOf(cartEmpty, cartTable));
    }

    public LookUpResult getCheckoutButton()
    {
        return HPU.find().in(cartTable.locate().first()).byId("btnStartCheckout");
    }
}