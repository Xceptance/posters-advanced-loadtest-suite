package com.xceptance.loadtest.posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.flows.AddToCartFlow;
import com.xceptance.loadtest.posters.flows.VisitFlow;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;

/**
 * Starts visit at landing page, browses categories or searches, executes product listing page actions, visits product pages, configures products, adds to cart and views the cart page.
 * 
 * @author Xceptance Software Technologies
 */
public class TAddToCart extends LoadTestCase
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

        // View the cart if not yet done
        if (!CartPage.instance.is())
        {
            new ViewCart().run();
        }
    }
}