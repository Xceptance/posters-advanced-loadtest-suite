package com.xceptance.loadtest.posters.models.pages.checkout;

import org.junit.Assert;

import com.xceptance.loadtest.posters.models.components.checkout.PaymentForm;

/**
 * Represents the payment page.
 * 
 * @author Xceptance Software Technologies
 */
public class PaymentPage extends CheckoutPage
{
    public static final PaymentPage instance = new PaymentPage();
    
    public static PaymentForm paymentForm = new PaymentForm();
       
    @Override
    public void validate()
    {
    	super.validate();
    	
    	validate(has(paymentForm));
           	
    	Assert.assertTrue("Expected payment step", checkoutProgressIndicator.isStepAvailable("Payment"));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(paymentForm)) && checkoutProgressIndicator.isStepAvailable("Payment");
    }
}