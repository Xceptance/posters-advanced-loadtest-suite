package com.xceptance.loadtest.posters.models.pages.checkout;

import org.junit.Assert;

import org.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Represents the order review page.
 * 
 * @author Xceptance Software Technologies
 */
public class OrderReviewPage extends CheckoutPage
{
    public static final OrderReviewPage instance = new OrderReviewPage();
    
    @Override
    public void validate()
    {
    	super.validate();
    	
    	Assert.assertTrue("Expected order review step", checkoutProgressIndicator.isStepAvailable("Place Order"));
    }

    @Override
    public boolean is()
    {
        return checkoutProgressIndicator.isStepAvailable("Place Order");
    }
    
    public HtmlElement getPlaceOrderButton()
    {
    	return Page.find().byId("btnOrder").asserted("Expected single place order button").single();
    }
}