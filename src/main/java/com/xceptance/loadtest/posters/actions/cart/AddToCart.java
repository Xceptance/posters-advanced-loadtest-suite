package com.xceptance.loadtest.posters.actions.cart;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Format;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.jsondata.AddToCartJSON;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

/**
 * Adds the product items of the current product page to the cart
 */
public class AddToCart extends AjaxAction<AddToCart>
{
    private AddToCartJSON cart;

    private int previousCartQuantity;
    
    private String productId;
    
    private String size;
    
    private String finish;

    /**
     * Constructor
     */
    public AddToCart()
    {
        super();
        
        // If enabled, modifies the timer name to include the increased cart count (even though the action might fail)
        if (Context.configuration().reportCartBySize)
        {
            setTimerName(Format.timerName(getTimerName(), Integer.valueOf(Context.get().data.totalAddToCartCount + 1)));
        }
    }

    @Override
    public void precheck()
    {
        // Retrieve current quantity
        previousCartQuantity = GeneralPages.instance.miniCart.getQuantity();

        // Retrieve PID
        productId = Page.find().byId("btnAddToCart").asserted("Expected add to cart button").single().getAttribute("onclick");
        productId = RegExUtils.getFirstMatch(productId, "addToCart\\((\\d+)\\,", 1);
        Assert.assertTrue("Expected valid productId", !StringUtils.isBlank(productId));
        
        // Retrieve selected size
        size = Page.find().byId("selectSize").asserted("Expected size attribute").single().getAttribute("value");
        Assert.assertTrue("Expected valid size attribute", !StringUtils.isBlank(size));
        
        // Retrieve selected finish
        finish = Page.find().byCss("#addToCartForm input[name=finish]:checked").asserted("Expected selected finish attribute").single().getAttribute("value");
        Assert.assertTrue("Expected valid finish attribute", !StringUtils.isBlank(finish));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
    	// Send add to cart request
    	WebResponse response = new HttpRequest()
    		.XHR()
    		.GET()
    		.url("/posters/addToCartSlider")
    		.param("productId", productId)
    		.param("finish", finish)
    		.param("size", size)
    		.assertJSONObject("Expected product to be contained in add to cart response", true, json -> json.has("product"))
    		.fire();
    	
    	// Create the add to cart JSON object from the response
    	cart = Context.getGson().fromJson(response.getContentAsString(), AddToCartJSON.class);
    	
        // Error in add to cart response
        Assert.assertFalse("Add to cart failed with message: " + cart.message, cart.error);

        // Validate the item count in the add to cart response
        Assert.assertTrue("Cart quantity did not change", cart.itemsInCart > previousCartQuantity);

        // Update the mini cart item count
    	GeneralPages.instance.miniCart.updateQuantity(cart.itemsInCart);

    	// Increase total add to cart count if successful
        Context.get().data.totalAddToCartCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
    	// Nothing to validate
    }
}