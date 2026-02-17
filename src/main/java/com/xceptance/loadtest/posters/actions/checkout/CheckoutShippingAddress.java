package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.api.actions.HtmlUnitPageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.checkout.PaymentPage;
import com.xceptance.loadtest.posters.models.pages.checkout.ShippingAddressPage;
import com.xceptance.loadtest.posters.util.Context;

import org.junit.Assert;

/**
 * Handles the shipping address page.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutShippingAddress extends HtmlUnitPageAction<CheckoutShippingAddress>
{
    private final Account account;

    public CheckoutShippingAddress()
    {
        this(Context.get().testData.getAccount().get());
    }

    public CheckoutShippingAddress(final Account account)
    {
        this.account = account;
    }

    @Override
    public void precheck()
    {
        super.precheck();

        // Validate that we have the shipping address form
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

        // Validate that it is the payment page
        PaymentPage.instance.validate();
    }
}