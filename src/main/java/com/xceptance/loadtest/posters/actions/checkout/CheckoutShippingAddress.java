package com.xceptance.loadtest.posters.actions.checkout;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.checkout.ShippingAddressPage;

/**
 * Enters the checkout.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutShippingAddress extends PageAction<CheckoutShippingAddress>
{
	private Account account;
	
	public CheckoutShippingAddress(Account account)
	{
		this.account = account;
	}
	
	@Override
	public void precheck()
	{
		super.precheck();
		
		Assert.assertTrue("Expected shipping address form", ShippingAddressPage.shippingAddressForm.exists());		
	}
	
	@Override
	protected void doExecute() throws Exception
	{
		// Fill shipping address form
		ShippingAddressPage.shippingAddressForm.fillForm(account);
		
		// Click continue button
		loadPageByClick(ShippingAddressPage.shippingAddressForm.getContinueButton());
	}

	@Override
	protected void postValidate() throws Exception
	{
        Validator.validatePageSource();

        // TODO Validate the billing page
	}
}