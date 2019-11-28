package com.xceptance.loadtest.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xceptance.loadtest.api.configuration.ConfigProbability;
import com.xceptance.loadtest.api.configuration.ConfigRange;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.configuration.interfaces.Initable;

/**
 * Credit Card
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class PaymentLimitations implements Initable
{
    /** Cart total permitted value. */
    @Property(key = "limits.permitted")
    public ConfigRange permitted;

    /** Cart total blocked value. */
    @Property(key = "limits.blocked")
    public ConfigRange blocked;

    /** Probability to use gift cards. */
    @Property(key = "limits.giftcard")
    public ConfigProbability giftCardProbability;

    /** Probability to use paypal checkout. */
    @Property(key = "limits.paypal")
    public ConfigProbability paypalProbability;

    @Override
    public void init()
    {
        // all good we just need the values.
    }

    /**
     * Convert oayment limitations object to json formated string.
     *
     * @return json format string of payment limitations.
     */
    @Override
    public String toString()
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
