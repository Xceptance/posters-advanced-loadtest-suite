package posters.actions.checkout;

import com.xceptance.loadtest.api.util.Context;

import posters.pages.checkout.OrderConfirmationPage;

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
