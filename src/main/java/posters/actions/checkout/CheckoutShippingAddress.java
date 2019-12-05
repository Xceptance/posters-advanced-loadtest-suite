package posters.actions.checkout;

import com.xceptance.loadtest.api.actions.NonPageView;
import com.xceptance.loadtest.api.util.Context;

import posters.pages.checkout.CheckoutPage;

public class CheckoutShippingAddress extends CheckoutAjaxAction<CheckoutShippingAddress> implements NonPageView
{
    @Override
    protected void doExecute() throws Exception
    {
        // enter shipping data
        CheckoutPage.instance.shippingAddressCard.fillForm(Context.get().data.getAccount().get());

        // change of the state causes this
        super.updateShippingMethod();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // no page load
        CheckoutPage.instance.validate();
    }
}
