package com.xceptance.loadtest.posters.jsondata;

import com.xceptance.loadtest.posters.jsondata.cart.Cart;

/**
 * It would have been too nice if the cart json would be standardized.
 * 
 * @author rschwietzke
 *
 */
public class AddToCartJSON
{
    public String action;
    public String queryString;
    public String locale;
    public int quantityTotal;
    public String message;

    public Cart cart;

    public boolean error;
}
