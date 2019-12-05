package posters.jsondata.checkout;

import java.util.List;

import posters.jsondata.cart.ShippingMethod;

public class Shipping
{
    public String UUID;
    public ProductLineItems productLineItems;
    public List<ShippingMethod> applicableShippingMethods;
    public ShippingMethod selectedShippingMethod;

    public Address shippingAddress;
}
