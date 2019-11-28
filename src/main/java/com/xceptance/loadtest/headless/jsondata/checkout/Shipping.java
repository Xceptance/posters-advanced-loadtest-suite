package com.xceptance.loadtest.headless.jsondata.checkout;

import java.util.List;

import com.xceptance.loadtest.headless.jsondata.cart.ShippingMethod;

public class Shipping
{
    public String UUID;
    public ProductLineItems productLineItems;
    public List<ShippingMethod> applicableShippingMethods;
    public ShippingMethod selectedShippingMethod;

    public Address shippingAddress;
}
