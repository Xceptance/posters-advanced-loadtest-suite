package posters.actions.checkout;

import com.xceptance.loadtest.api.validators.Validator;

import posters.pages.checkout.CheckoutEntryPage;
import posters.pages.checkout.CheckoutPage;

public class CheckoutGuest extends AbstractCheckout<CheckoutGuest>
{
    @Override
    protected void doExecute() throws Exception
    {
        // click on checkout button
        loadPageByClick(CheckoutEntryPage.instance.guestCheckoutCard.getGuestCheckoutButton().asserted().single());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        CheckoutPage.instance.validate();
    }
}
