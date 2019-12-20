package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.flows.AddToCartFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Open the landing page and browse the catalog to a random product. Configure
 * this product and add it to the cart. Finally process the checkout including
 * the final order placement step.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TGuestOrder extends LoadTestCase
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

// TODO        
//        if (CartPage.instance.isOrderable() == false)
//        {
//            new CartCleanUpFlow().run();
//        }

        // we have not touched any account yet
        // attach it to the context, this method will complain if we
        // set one up already
        Context.get().data.attachAccount();

        // we can only checkout if we still got a cart
        if (GeneralPages.instance.miniCart.isEmpty() == false)
        {
// TODO       	
//            new Checkout().run();
//
//            new CheckoutGuest().run();
//
//            new CheckoutShippingAddress().run();
//            new CheckoutSelectShipping().run();
//            new CheckoutSubmitShipping().run();
//
//            new CheckoutSubmitBilling().run();
//
//            new CheckoutPlaceOrder().run();
        }
        else
        {
            EventLogger.CHECKOUT.warn("Empty Cart", "Cart was empty before checkout was started");
        }
    }
}
