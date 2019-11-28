package com.xceptance.loadtest.headless.tests;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.cart.ViewCart;
import com.xceptance.loadtest.headless.actions.checkout.Checkout;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutGuest;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSelectShipping;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutShippingAddress;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitBilling;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitShipping;
import com.xceptance.loadtest.headless.flows.AddToCartFlow;
import com.xceptance.loadtest.headless.flows.CartCleanUpFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;
import com.xceptance.loadtest.headless.pages.cart.CartPage;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

/**
 * Open the landing page and browse the catalog to a random product. Configure
 * this product, add it to the cart and process the checkout steps but do not
 * place the order finally.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TGuestCheckout extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

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

        // we have not touched any account yet
        // attach it to the context, this method will complain if we
        // set one up already
        Context.get().data.attachAccount();

        // we can only checkout if we still got a cart
        if (GeneralPages.instance.miniCart.isEmpty() == false)
        {
            new Checkout().run();

            new CheckoutGuest().run();

            new CheckoutShippingAddress().run();
            new CheckoutSelectShipping().run();
            new CheckoutSubmitShipping().run();

            new CheckoutSubmitBilling().run();
        }
        else
        {
            EventLogger.CHECKOUT.warn("Empty Cart", "Cart was empty before checkout was started");
        }
    }
}
