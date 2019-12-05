package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.posters.pages.checkout.CheckoutPage;

public class CheckoutSelectShipping extends CheckoutAjaxAction<CheckoutSelectShipping>
{
    @Override
    protected void doExecute() throws Exception
    {
        super.selectShippingMethod();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // no page load
        CheckoutPage.instance.validate();
    }
}
