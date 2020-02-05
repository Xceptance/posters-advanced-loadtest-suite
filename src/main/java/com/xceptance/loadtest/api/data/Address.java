package com.xceptance.loadtest.api.data;

import com.xceptance.loadtest.api.configuration.annotations.Property;

/**
 * Representation of an address.
 *
 * @author Xceptance Software Technologies
 */
public class Address
{
    /**
     * Address id.
     * 
     * Required in case multiple addresses are in use.
     */
    @Property(key = "id")
    public String id;

    /** Addressline 1 mostly street */
    @Property(key = "addressLine1")
    public String addressLine1;

    /** Address line 2 */
    @Property(key = "addressLine2")
    public String addressLine2;

    /** ZIP code or postal code */
    @Property(key = "zip")
    public String zip;

    /** Phone number */
    @Property(key = "phone")
    public String phone;

    /** Town */
    @Property(key = "city")
    public String city;

    /** State code */
    @Property(key = "stateCode")
    public String stateCode;

    /** State */
    @Property(key = "state")
    public String state;

    /** Country code */
    @Property(key = "countryCode")
    public String countryCode;

    /** Country */
    @Property(key = "country")
    public String country;

    private Address()
    {
    }

	@Override
	public String toString() {
		return "Address [id=" + id + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", zip="
				+ zip + ", phone=" + phone + ", city=" + city + ", stateCode=" + stateCode + ", state=" + state
				+ ", countryCode=" + countryCode + ", country=" + country + "]";
	}
}
