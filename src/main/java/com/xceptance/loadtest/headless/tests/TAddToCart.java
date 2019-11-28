package com.xceptance.loadtest.headless.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.cart.ViewCart;
import com.xceptance.loadtest.headless.flows.AddToCartFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;
import com.xceptance.loadtest.headless.pages.cart.CartPage;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TAddToCart extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();//

        // Fill cart
        new AddToCartFlow(Context.configuration().addToCartCount.value).run();

        // View the cart if not just done
        if (!CartPage.instance.is())
        {
            new ViewCart().run();
        }
    }
}
