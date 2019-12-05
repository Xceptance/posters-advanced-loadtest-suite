package com.xceptance.loadtest.posters.jsondata;

import com.xceptance.loadtest.api.data.Address;
import com.xceptance.loadtest.posters.jsondata.checkout.Customer;
import com.xceptance.loadtest.posters.jsondata.checkout.Order;

public class CheckoutJSON
{
    public String action;
    public String queryString;
    public String locale;

    public Customer customer;
    public Order order;
    public Address address;

    public boolean shippingBillingSame;
    public String shippingMethod;
}
