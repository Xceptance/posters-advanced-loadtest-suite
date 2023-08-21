package com.xceptance.loadtest.posters.actions.checkout;

import org.junit.Assert;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.checkout.OrderReviewPage;
import com.xceptance.loadtest.posters.models.pages.general.OrderConfirmationPage;

/**
 * Handles the place order step on the order review page.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutPlaceOrder extends PageAction<CheckoutPlaceOrder>
{
	@Override
	public void precheck()
	{
		super.precheck();

		Assert.assertTrue("Expected order review page", OrderReviewPage.instance.is());		
	}
	
	@Override
	protected void doExecute() throws Exception
	{
		// Click continue button
		loadPageByClick(OrderReviewPage.instance.getPlaceOrderButton());
	}

	@Override
	protected void postValidate() throws Exception
	{
        Validator.validatePageSource();

        // Validate that we are at the order confirmation page
		OrderConfirmationPage.instance.validate();

		// Validate that the cart is empty
		Assert.assertTrue("Expected empty cart at Order Confirmation Page after successful order placement", GeneralPages.instance.miniCart.isEmpty());
	}
}