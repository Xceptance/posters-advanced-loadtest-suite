package posters.jsondata;

import posters.jsondata.checkout.AddressValued;
import posters.jsondata.checkout.Customer;
import posters.jsondata.checkout.Order;
import posters.jsondata.common.StringValue;

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
