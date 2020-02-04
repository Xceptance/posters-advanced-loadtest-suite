package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.flows.AddToCartFlow;
import com.xceptance.loadtest.posters.flows.CheckoutFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;

/**
 * Starts visit at landing page, adds products to the cart via searching or browsing,
 * and proceeds through checkout to finally place the order.
 * 
 * @author Xceptance Software Technologies
 */
public class TGuestOrder extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page
        new VisitFlow().run();

        // Add items to the cart via browsing and searching the catalog
        new AddToCartFlow(Context.configuration().addToCartCount.value).run();

        // View the cart if not just done
        if (!CartPage.instance.is())
        {
            new ViewCart().run();
        }

        // Validate that the cart is not empty
        CartPage.instance.validateIsNotEmpty();

        // Attach an account to the Context, so it can be used in the following actions
        Context.get().data.attachAccount();

        // Follow checkout steps
        new CheckoutFlow(true).run();
    }
}