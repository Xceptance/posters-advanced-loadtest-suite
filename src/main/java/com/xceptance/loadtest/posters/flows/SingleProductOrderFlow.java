package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;

/**
 * Opens, configures and orders a single product.
 *
 * @author Xceptance Software Technologies
 */
public class SingleProductOrderFlow extends Flow
{
    @Override
    public boolean execute() throws Throwable
    {
        // Configure product
        if (ProductDetailPage.instance.is())
        {
            new ConfigureProductFlow().run();
        }

        // Add the product to the cart for the given number of times
        final int addToCartCount = Context.configuration().cartProductQuantity.value;
        for (int i = 0; i < addToCartCount; i++)
        {
            new AddToCart().run();
        }

        // Open the cart page
        new ViewCart().run();

        // Validate that the cart is not empty
        CartPage.instance.validateIsNotEmpty();

        // Attach an account to the Context, so it can be used in the following actions
        Context.get().data.attachAccount();

        // Follow checkout steps
        new CheckoutFlow(true).run();        

        // Mark successful execution of flow
        return true;
    }
}