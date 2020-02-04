package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;

/**
 * Add products to the cart via navigating the catalog by browsing or searching and
 * handling the resulting product listing and product pages.
 * 
 * Optionally visits the cart page.
 * 
 * @author Xceptance Software Technologies
 */
public class AddToCartFlow extends Flow
{
    /**
     * Maximum number of attempts to add the desired number of products to the cart
     */
    private final SafetyBreak addToCartSafetyBreak = new SafetyBreak(5);

    /**
     * Item count that is expected after the add to cart flow has finished
     */
    private final int targetItemCount;

    /**
     * Creates and add to cart flow with given override for the cart item target  
     *
     * @param addToCartOverride Custom target size of the cart
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
        if (Context.configuration().searchOnAddToCartProbability.random())
        {
        	// Search
            new SearchFlow().run();
        }
        else
        {
            // Navigate the categories
            new NavigateCategoriesFlow().run();
        }
        
        // Handle resulting page, most likely product listing page
        new NavigateToProductPageFlow().run();
    }
}