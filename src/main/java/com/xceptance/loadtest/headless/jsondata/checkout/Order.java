package com.xceptance.loadtest.headless.jsondata.checkout;

import java.util.List;

public class Order
{
    public List<Shipping> shipping;
    public Billing billing;
    public Totals totals;
}
