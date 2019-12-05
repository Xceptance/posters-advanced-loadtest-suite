package posters.jsondata;

import posters.jsondata.cart.Cart;

/**
 * It would have been too nice if the cart json would be standardized.
 *
 * @author rschwietzke
 *
 */
public class CartJSON
{
    public String action;
    public String queryString;
    public String locale;

    // well... another json another name, let's call it basket
    // this time... that is fun, isn't it
    public Cart basket;
}
