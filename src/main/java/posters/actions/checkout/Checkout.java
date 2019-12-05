package posters.actions.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.validators.Validator;

import posters.pages.cart.CartPage;
import posters.pages.checkout.CheckoutEntryPage;

public class Checkout extends AbstractCheckout<Checkout>
{
    private HtmlElement checkoutButton;

    @Override
    public void precheck()
    {
        super.precheck();
        checkoutButton = CartPage.instance.getCheckoutButton().asserted().single();
    }

    @Override
    protected void doExecute() throws Exception
    {
        // click on checkout button
        loadPageByClick(checkoutButton);
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        CheckoutEntryPage.instance.validate();
    }
}
