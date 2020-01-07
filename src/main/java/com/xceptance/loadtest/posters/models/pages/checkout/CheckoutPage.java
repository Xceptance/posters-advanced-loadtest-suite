package com.xceptance.loadtest.posters.models.pages.checkout;

import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.posters.models.components.checkout.CheckoutProgressIndicator;

/**
 * Represents a checkout page.
 * 
 * @author Xceptance Software Technologies
 */
public abstract class CheckoutPage extends Page
{
    public static final CheckoutProgressIndicator checkoutProgressIndicator = new CheckoutProgressIndicator();

    @Override
    public void validate()
    {
        validate(has(checkoutProgressIndicator));
    }

    @Override
    public boolean is()
    {
        return matches(has(checkoutProgressIndicator));
    }
}