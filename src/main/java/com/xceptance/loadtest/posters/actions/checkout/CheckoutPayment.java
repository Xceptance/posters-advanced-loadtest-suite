package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.api.actions.HtmlUnitPageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.checkout.OrderReviewPage;
import com.xceptance.loadtest.posters.models.pages.checkout.PaymentPage;
import com.xceptance.loadtest.posters.util.Context;

import org.junit.Assert;

/**
 * Handles the payment page.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutPayment extends HtmlUnitPageAction<CheckoutPayment>
{
    private final Account account;

    private final CreditCard creditCard;

    public CheckoutPayment()
    {
        this(Context.get().testData.getAccount().get(), Context.get().testData.getAccount().get().getPrimaryCard());
    }

    public CheckoutPayment(final Account account, final CreditCard creditCard)
    {
        this.account = account;
        this.creditCard = creditCard;
    }

    @Override
    public void precheck()
    {
        super.precheck();

        Assert.assertTrue("Expected payment form", PaymentPage.paymentForm.exists());		
    }

    @Override
    protected void doExecute() throws Exception
    {
        // Fill payment form
        PaymentPage.paymentForm.fillForm(account, creditCard);

        // Click continue button
        loadPageByClick(PaymentPage.paymentForm.getContinueButton());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        // Validate that it is the order review (place order) page
        OrderReviewPage.instance.validate();
    }
}