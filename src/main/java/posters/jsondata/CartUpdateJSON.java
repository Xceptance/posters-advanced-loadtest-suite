package posters.jsondata;

import java.util.List;

import posters.jsondata.cart.ActionUrls;
import posters.jsondata.cart.Item;
import posters.jsondata.cart.Resources;
import posters.jsondata.cart.Shipments;
import posters.jsondata.cart.Totals;
import posters.jsondata.cart.Valid;

/**
 * It would have been too nice if the cart json would be standardized.
 *
 * @author rschwietzke
 *
 */
public class CartUpdateJSON
{
    public String action;
    public String queryString;
    public String locale;

    // well... another json another name, let's call it basket
    // this time... that is fun, isn't it
    public ActionUrls actionUrls;
    public int numOfShipments;
    public Totals totals;
    public List<Shipments> shipments;
    public List<Item> items;
    public int numItems;

    public Valid valid;
    public Resources resources;
}
