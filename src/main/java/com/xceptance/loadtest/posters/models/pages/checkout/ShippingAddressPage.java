package com.xceptance.loadtest.posters.models.pages.checkout;

import org.junit.Assert;

import com.xceptance.loadtest.posters.models.components.checkout.ShippingAddressForm;

/**
 * Represents a shipping address page.
 * 
 * @author Xceptance Software Technologies
 */
public class ShippingAddressPage extends CheckoutPage
{
    public static final ShippingAddressPage instance = new ShippingAddressPage();
    
    public static ShippingAddressForm shippingAddressForm = new ShippingAddressForm();
       
    @Override
    public void validate()
    {
    	super.validate();
    	
    	validate(has(shippingAddressForm));
           	
    	Assert.assertTrue("Expected shipping address step", checkoutProgressIndicator.isStepAvailable("Shipping Address"));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(shippingAddressForm)) && checkoutProgressIndicator.isStepAvailable("Shipping Address");
    }
}