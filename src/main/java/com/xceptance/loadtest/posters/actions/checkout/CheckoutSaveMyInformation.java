package com.xceptance.loadtest.posters.actions.checkout;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.models.pages.checkout.OrderConfirmationPage;

public class CheckoutSaveMyInformation extends AbstractCheckout<CheckoutSaveMyInformation>
{
    @Override
    protected void doExecute() throws Exception
    {
        OrderConfirmationPage.instance.saveMyInformationCard.fillForm(Context.get().data.getAccount().get());

        super.submitSaveMyInformation();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // TODO
    }
}
