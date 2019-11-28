package com.xceptance.loadtest.headless.jsondata.cart;

import com.google.gson.annotations.SerializedName;

public class ShippingMethod
{
    public String ID;
    public String displayName;
    public String description;
    public String estimatedArrivalTime;

    @SerializedName("default")
    public boolean _default;

    public String shippingCost;
    public boolean selected;
}
