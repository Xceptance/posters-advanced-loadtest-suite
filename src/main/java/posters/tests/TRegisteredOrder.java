package posters.tests;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.account.Logout;
import posters.actions.cart.ViewCart;
import posters.actions.checkout.Checkout;
import posters.actions.checkout.CheckoutLogin;
import posters.actions.checkout.CheckoutPlaceOrder;
import posters.actions.checkout.CheckoutSelectShipping;
import posters.actions.checkout.CheckoutShippingAddress;
import posters.actions.checkout.CheckoutSubmitBilling;
import posters.actions.checkout.CheckoutSubmitShipping;
import posters.flows.AddToCartFlow;
import posters.flows.CartCleanUpFlow;
import posters.flows.CreateAccountFlow;
import posters.flows.VisitFlow;
import posters.pages.cart.CartPage;
import posters.pages.general.GeneralPages;

/**
 * Open the landing page, register account if necessary and browse the catalog to a random product. Configure this
 * product and add it to the cart. Finally process the checkout including the final order placement step.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TRegisteredOrder extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        Context.requiresRegisteredAccount(true);

        // Start at the landing page.
        new VisitFlow().run();

        if (Context.get().data.attachAccount().get().isRegistered == false)
        {
            // Register user
            new CreateAccountFlow().run();

            // Logout from freshly created account, but login later during checkout again
            new Logout().run();
        }

        // Fill
        new AddToCartFlow(Context.configuration().addToCartCount.value).run();

        // View the cart if not just done
        if (!CartPage.instance.is())
        {
            new ViewCart().runIfPossible();
        }

        if (CartPage.instance.isOrderable() == false)
        {
            new CartCleanUpFlow().run();
        }

        // we can only checkout if we still got a cart
        if (GeneralPages.instance.miniCart.isEmpty() == false)
        {
            new Checkout().run();

            // use the attached account to avoid that we get a new one, either we have one or we
            // don't, hence prefer to fail
            new CheckoutLogin(Context.get().data.getAccount().get()).run();

            new CheckoutShippingAddress().run();
            new CheckoutSelectShipping().run();
            new CheckoutSubmitShipping().run();

            new CheckoutSubmitBilling().run();

            new CheckoutPlaceOrder().run();

            new Logout().run();
        }
        else
        {
            EventLogger.CHECKOUT.warn("Empty Cart", "Cart was empty before checkout was started");
        }
    }
}
