package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Configures and orders a single product.
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

        new ViewCart().runIfPossible();

        // TODO Cart clean up flow

        // Execute checkout steps
		if (GeneralPages.instance.miniCart.isEmpty() == false)
		{
			// TODO Checkout/order
		}
		else
		{
			EventLogger.CHECKOUT.warn("Empty Cart", "Could not checkout because cart was empty");
		}

        return false;
    }
}