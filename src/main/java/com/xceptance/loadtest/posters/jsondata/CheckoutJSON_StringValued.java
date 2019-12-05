package com.xceptance.loadtest.posters.jsondata;

import com.xceptance.loadtest.posters.jsondata.checkout.AddressValued;
import com.xceptance.loadtest.posters.jsondata.checkout.Customer;
import com.xceptance.loadtest.posters.jsondata.checkout.Order;
import com.xceptance.loadtest.posters.jsondata.common.StringValue;

public class CheckoutJSON_StringValued
{
    public String action;
    public String queryString;
    public String locale;

    public Customer customer;
    public Order order;
    public AddressValued address;

    public boolean shippingBillingSame;
    public String shippingMethod;

    public StringValue email;
    public StringValue phone;
}
