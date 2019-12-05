package com.xceptance.loadtest.posters.jsondata.cart;

import java.util.List;

public class Totals
{
    public String subTotal;
    public String totalShippingCost;
    public String grandTotal;
    public String totalTax;
    public DiscountTotal orderLevelDiscountTotal;
    public DiscountTotal shippingLevelDiscountTotal;
    public List<Discounts> discounts;
    public String discountsHtml;
}
