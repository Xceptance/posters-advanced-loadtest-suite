package com.xceptance.loadtest.headless.actions.checkout;

public class CheckoutPlaceOrder extends AbstractCheckout<CheckoutPlaceOrder>
{
    @Override
    protected void doExecute() throws Exception
    {
        // place the order and reload the target url, which is not safe... let's see when this wiull
        super.submitOrder();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // verify order confirmation
    }
}
