package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.cart.AddToCart;
import com.xceptance.loadtest.posters.actions.cart.ShowMiniCart;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;

/**
 * Browses the catalog, adds a number of products (respectively variations of products) to the cart and opens the cart
 * page finally.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
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
        // Add another item to cart if we are under target and if we have not tried twice the amount
        // already
        while (Context.get().data.cartLineItemCount < targetItemCount && Context.get().data.totalAddToCartCount < targetItemCount * 2)
        {
            // Check if the maximum number of attempts is reached.Flow
            addToCartSafetyBreak.check("Unable to add the desired number of products to the cart.");

            // take us to a product
            searchOrBrowse();

            // do we have to configure
            final boolean configureProductFlow = new ConfigureProductFlow().run();
            if (configureProductFlow)
            {
                // could we configure, if so reset safety break
                new AddToCart().runIfPossible().ifPresent(e -> addToCartSafetyBreak.reset());
            }

            // view the mini cart of desired
            if (Context.configuration().viewMiniCartProbability.random())
            {
                new ShowMiniCart().runIfPossible();
            }

            // view the cart if desired
            if (Context.configuration().viewCartProbability.random())
            {
                new ViewCart().runIfPossible();
            }
        }

        return true;
    }

    private void searchOrBrowse() throws Throwable
    {
        // Decide if a search is desired or the main navigation should be
        // used.
        if (Context.configuration().searchOnAddToCartProbability.random())
        {
            new SearchFlow().run();
        }
        else
        {
            // get us a category context
            new CategoryFlow().run();

            // work on it
            new RefineByFlow().run();
        }
    }
}
