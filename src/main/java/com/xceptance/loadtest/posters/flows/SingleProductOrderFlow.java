package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.actions.checkout.Checkout;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutGuest;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutPlaceOrder;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutSelectShipping;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutShippingAddress;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutSubmitBilling;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutSubmitShipping;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Order a single configured product.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class SingleProductOrderFlow extends Flow
{
    @Override
    public boolean execute() throws Throwable
    {
        // Configure product.
        if (ProductDetailPage.instance.is())
        {
            new ConfigureProductFlow().run();
        }

        // how often do we want to do an add 2 cart?
        final int add2Cart = Context.configuration().cartProductQuantity.value;
        // Add product variation to cart.
        for (int i = 0; i < add2Cart; i++)
        {
            new AddToCart().run();
        }

        new ViewCart().runIfPossible();

         if (CartPage.instance.isOrderable() == false)
         {
         new CartCleanUpFlow().run();
         }

        // final we can final only checkout if final we still got final a cart
         if (GeneralPages.instance.miniCart.isEmpty() == false)
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
            EventLogger.CHECKOUT.warn("Empty Cart", "Cart was empty before checkout was started");
         }
        return false;
    }
}
