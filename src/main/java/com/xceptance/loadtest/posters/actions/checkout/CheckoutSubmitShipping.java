package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.posters.pages.checkout.CheckoutPage;

public class CheckoutSubmitShipping extends CheckoutAjaxAction<CheckoutSubmitShipping>
{
    @Override
    protected void doExecute() throws Exception
    {
        // hit next
        super.submitShipping();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // no page load
        CheckoutPage.instance.validate();
    }
}
