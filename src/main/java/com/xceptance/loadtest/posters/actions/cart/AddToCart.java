package com.xceptance.loadtest.posters.actions.cart;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Format;
import com.xceptance.loadtest.posters.jsondata.AddToCartJSON;
import com.xceptance.loadtest.posters.pages.components.general.MiniCart;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

/**
 * Adds the currently shown product or more specifically one of its variations to the cart.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class AddToCart extends AjaxAction<AddToCart>
{
    private AddToCartJSON cart;

    private int previousCartQuantity;

    /**
     * Constructor
     */
    public AddToCart()
    {
        super();
        
        // Set the timername already plus one... might happen that we are not doing it
        if (Context.configuration().reportCartBySize)
        {
            // Start simply with the qty, later find a way to capture and store the state better
            setTimerName(Format.timerName(getTimerName(), Integer.valueOf(Context.get().data.totalAddToCartCount + 1)));
        }
    }

    @Override
    public void precheck()
    {
        // Retrieve current quantity
        previousCartQuantity = GeneralPages.instance.miniCart.getQuantity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
    	// TODO
    }

//    private AddToCartJSON updateMiniCart(final String responseJson)
//    {
//        final Gson gson = Context.getGson();
//        final AddToCartJSON cart = gson.fromJson(responseJson, AddToCartJSON.class);
//
//        GeneralPages.instance.miniCart.updateQuantity(cart.quantityTotal, cart.cart.items.size());
//
//        return cart;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Error while adding
        Assert.assertFalse("Add to cart failed with message: " + cart.message, cart.error);

        // Validate the response cart in addition to what we might have done already
        final int currentCartQuantity = MiniCart.instance.getQuantity();
        Assert.assertTrue("Cart quantity did not change", currentCartQuantity > previousCartQuantity);

        // Increase total add to cart count if successful
        Context.get().data.totalAddToCartCount++;
    }
}