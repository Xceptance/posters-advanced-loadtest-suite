package com.xceptance.loadtest.posters.actions.checkout;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.loadtest.posters.models.pages.checkout.ShippingAddressPage;

/**
 * Enters the checkout.
 * 
 * @author Xceptance Software Technologies
 */
public class Checkout extends PageAction<Checkout>
{
	@Override
	public void precheck()
	{
		super.precheck();
		
		Assert.assertTrue("Expected checkout button exists and is clickable", CartPage.instance.checkoutButton.exists());		
	}
	
	@Override
	protected void doExecute() throws Exception
	{
		loadPageByClick(CartPage.instance.checkoutButton.locate().asserted("Expected checkout button").first());
	}

	@Override
	protected void postValidate() throws Exception
	{
        Validator.validatePageSource();

        // Validate that it is the shipping address page
        ShippingAddressPage.instance.validate();
	}
}