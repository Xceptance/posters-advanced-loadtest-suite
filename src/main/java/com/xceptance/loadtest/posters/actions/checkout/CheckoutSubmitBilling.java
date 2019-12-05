package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.pages.checkout.CheckoutPage;

public class CheckoutSubmitBilling extends CheckoutAjaxAction<CheckoutSubmitBilling>
{
    @Override
    protected void doExecute() throws Exception
    {
        final Account account = Context.get().data.getAccount().get();
        final CreditCard cc = account.getPrimaryCard();

        CheckoutPage.instance.paymentCard.fillBillingAddressIn(account);
        CheckoutPage.instance.paymentCard.fillCreditCardIn(account, cc);

        super.submitPayment();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // no page load
        CheckoutPage.instance.validate();
    }
}
