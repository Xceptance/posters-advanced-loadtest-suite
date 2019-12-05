package com.xceptance.loadtest.posters.jsondata.checkout;

import java.util.List;

import com.xceptance.loadtest.posters.jsondata.cart.DiscountTotal;
import com.xceptance.loadtest.posters.jsondata.cart.Discounts;

public class Totals
{
    public String subTotal;
    public String totalShippingCost;
    public String grandTotal;
    public String totalTax;

    public DiscountTotal orderLevelDiscountTotal;
    public DiscountTotal shippingLevelDiscountTotal;
    public List<Discounts> discounts;
    public String discountHtml;
}
