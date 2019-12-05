package posters.actions.checkout;

import posters.pages.checkout.CheckoutPage;

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
