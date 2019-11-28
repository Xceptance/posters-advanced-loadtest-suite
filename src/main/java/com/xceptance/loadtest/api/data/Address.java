package com.xceptance.loadtest.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xceptance.loadtest.api.configuration.annotations.Property;

/**
 * Default implementation of {@link Address}.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Address
{
    /**
     * Address id aka identifier when more than one address is in use (account
     * area)
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

    /**
     * Default constructor to keep the configuration handling happy
     */
    private Address()
    {
    }

    /**
     * Convert address object to json formated string.
     *
     * @return to json format string of account.
     */
    @Override
    public String toString()
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
