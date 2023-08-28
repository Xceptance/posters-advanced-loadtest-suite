package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.posters.actions.checkout.Checkout;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutPayment;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutPlaceOrder;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutShippingAddress;

/**
 * Follows the checkout process, optionally placing an order.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutFlow extends Flow
{
	private boolean placeOrder = false;
	
	public CheckoutFlow(boolean placeOrder)
	{
		this.placeOrder = placeOrder;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
    	// Enter checkout
    	new Checkout().run();
    	
    	// Provide shipping address
    	new CheckoutShippingAddress().run();
    	
    	// Provide payment details
    	new CheckoutPayment().run();
    	
    	// Place the order and proceed to the Order Confirmation Page
    	if(placeOrder)
    	{
    		new CheckoutPlaceOrder().run();
    	}
        return true;
    }
}