package com.xceptance.loadtest.headless.tests.flash;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.cart.AddToCart;
import com.xceptance.loadtest.headless.actions.cart.ViewCart;
import com.xceptance.loadtest.headless.actions.catalog.flash.DirectProductDetails;
import com.xceptance.loadtest.headless.actions.checkout.Checkout;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutGuest;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutPlaceOrder;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSelectShipping;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutShippingAddress;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitBilling;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitShipping;
import com.xceptance.loadtest.headless.flows.CartCleanUpFlow;
import com.xceptance.loadtest.headless.flows.ConfigureProductFlow;
import com.xceptance.loadtest.headless.pages.cart.CartPage;

/**
 * Open the landing page and browse the catalog to a random product. Configure
 * this product and add it to the cart. Finally process the checkout including
 * the final order placement step.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TGuestFlashOrder extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // jump to the product directly
        new DirectProductDetails(Context.configuration().flashUrls.value).run();

        // do we have to configure?
        new ConfigureProductFlow().run();

        // add to cart
        new AddToCart().runIfPossible();

        // see cart
        new ViewCart().runIfPossible();

        if (CartPage.instance.isOrderable() == false)
        {
            new CartCleanUpFlow().run();
        }

        // we have not touched any account yet
        // attach it to the context, this method will complain if we
        // set one up already
        Context.get().data.attachAccount();

        // we can only checkout if we still got a cart
        if (CartPage.instance.isOrderable())
        {
            new Checkout().run();

            new CheckoutGuest().run();

            new CheckoutShippingAddress().run();
            new CheckoutSelectShipping().run();
            new CheckoutSubmitShipping().run();

            new CheckoutSubmitBilling().run();

            new CheckoutPlaceOrder().run();
        }
        else
        {
            EventLogger.CHECKOUT.warn("Empty Cart", "Cart was empty or product not orderable before checkout was started");
        }
    }
}
