package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;

/**
 * Add products to the cart via navigating the catalog by browsing or searching and handling the resulting product listing and product pages.
 * 
 * Optionally visits the cart page.
 */
public class AddToCartFlow extends Flow
{
    /**
     * Maximum number of attempts to add the desired number of products to the cart.
     */
    private final SafetyBreak addToCartSafetyBreak = new SafetyBreak(5);

    /**
     * What our item count should be at the end
     */
    private final int targetItemCount;

    /**
     * Force the cart to be updated by setting another cart size target number
     *
     * @param addToCartOverride
     *            custom target size of the cart
     */
    public AddToCartFlow(final int targetItemCount)
    {
        this.targetItemCount = targetItemCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // Add another item to cart if we are under target and if we have not tried twice the amount already
        while (Context.get().data.cartLineItemCount < targetItemCount && Context.get().data.totalAddToCartCount < targetItemCount * 2)
        {
            // Check if the maximum number of attempts is reached.Flow
            addToCartSafetyBreak.check("Unable to add the desired number of products to the cart.");

            // Apply searching or browsing activities to navigate to product details
            searchOrBrowse();

            // Configure if required
            if (new ConfigureProductFlow().run())
            {
                // Add the configured product to the cart 
                new AddToCart().runIfPossible().ifPresent(e -> addToCartSafetyBreak.reset());
            }

            // View the cart if desired
            if (Context.configuration().viewCartProbability.random())
            {
                new ViewCart().runIfPossible();
            }
        }

        return true;
    }

    private void searchOrBrowse() throws Throwable
    {
        // Either search or use category navigation to navigate the site
        if (Context.configuration().searchOnAddToCartProbability.random())
        {
            new SearchFlow().run();
        }
        else
        {
            // Navigate the categories
            new NavigateCategoriesFlow().run();

            // Work the 
            new NavigateToProductPageFlow().run();
        }
    }
}
