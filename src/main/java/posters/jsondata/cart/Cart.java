package posters.jsondata.cart;

import java.util.List;

public class Cart
{
    public ActionUrls actionUrls;
    public int numOfShipments;
    public Totals totals;
    public List<Shipments> shipments;
    public List<Item> items;
    public int numItems;

    public Valid valid;
    public Resources resources;
}
