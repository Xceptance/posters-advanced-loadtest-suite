package com.xceptance.loadtest.headless.jsondata;

import com.xceptance.loadtest.headless.jsondata.checkout.AddressValued;
import com.xceptance.loadtest.headless.jsondata.checkout.Customer;
import com.xceptance.loadtest.headless.jsondata.checkout.Order;
import com.xceptance.loadtest.headless.jsondata.common.StringValue;

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
