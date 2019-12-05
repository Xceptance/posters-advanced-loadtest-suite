package posters.jsondata;

import com.xceptance.loadtest.api.data.Address;

import posters.jsondata.checkout.Customer;
import posters.jsondata.checkout.Order;

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
