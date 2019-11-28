package com.xceptance.loadtest.api.data;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xceptance.loadtest.api.configuration.EnumConfigList;
import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.configuration.interfaces.Initable;
import com.xceptance.loadtest.api.util.RoundRobin;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Default implementation of {@link Account}.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Account implements Initable
{
    /**
     * Email address
     */
    @Property(key = "email", required = false)
    public String email;

    /**
     * Login, just in case it is different from email
     */
    @Property(key = "login", required = false)
    public String login;

    /**
     * First name
     */
    @Property(key = "firstname", required = false)
    public String firstname;

    /**
     * Last name
     */
    @Property(key = "lastname", required = false)
    public String lastname;

    /**
     * Password
     */
    @Property(key = "password", required = false)
    public String password;

    /**
     * Shipping address.
     */
    @Property(key = "shippingAddress", required = false)
    public Address shippingAddress;

    /**
     * Billing address
     */
    @Property(key = "billingAddress", required = false)
    public Address billingAddress;

    /**
     * Alternative address for changing data, mainly not for checkout
     */
    @Property(key = "alternativeAddress", required = false)
    public Address alternativeAddress;

    /**
     * Optional value for orderID's, if this account is used for order history scenarios.
     */
    @Property(key = "orderID", required = false)
    public String orderID;

    /**
     * Credit cards
     */
    @EnumProperty(key = "creditcards", clazz = CreditCard.class, compact = true, stopOnGap = false, from = 0, to = 20, required = false)
    public EnumConfigList<CreditCard> creditCards;

    /**
     * Registered flag
     */
    public boolean isRegistered;

    /**
     * Origin, where did we get that account from
     */
    public AccountOrigin origin;

    private int primaryCreditCardIndex = -1;

    private int secondaryCreditCardIndex = -1;

    /**
     * Create generic generated account. This account will be based on the configured account (email
     * address, password, registration state) if any.
     */
    public Account()
    {
        this.origin = AccountOrigin.RANDOM;
    }

    @Override
    public void init()
    {
        // Do we have at least one credit card in our list? If so calculate a random index for one
        // of those cards
        final int numberOfCreditCards = creditCards.size();
        if (numberOfCreditCards >= 1)
        {
            // pick a random index
            primaryCreditCardIndex = XltRandom.nextInt(0, numberOfCreditCards - 1);

            // If we have at least two credit cards pick another one which is not the same index as
            // the first one
            if (numberOfCreditCards >= 2)
            {
                final int offset = 1 + XltRandom.nextInt(0, numberOfCreditCards - 2);
                secondaryCreditCardIndex = RoundRobin.getIndex(numberOfCreditCards, primaryCreditCardIndex, offset);
            }
        }
    }

    /**
     * Fork a new account from the base data
     *
     * @param f
     *            a function to fork an account
     * @return a new account
     */
    public Account fork(final UnaryOperator<Account> f)
    {
        return f.apply(this);
    }

    /**
     * Fork a new account from the base data, with the optional interface, for read an data element.
     *
     * @param f
     *            a function to fork an account
     * @return a new account
     */
    public Optional<Account> optFork(final UnaryOperator<Account> f)
    {
        return Optional.ofNullable(f.apply(this));
    }

    /**
     * Convert account object to json formated string.
     *
     * @return json format string of account.
     */
    @Override
    public String toString()
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    /**
     * Returns a random credit card from the list of available credit cards based on a previously
     * calculated index. The index won't change.
     */
    public CreditCard getPrimaryCard()
    {
        if (primaryCreditCardIndex < 0)
        {
            Assert.fail(MessageFormat.format("No preferred creditcard available for {0}", this));
        }

        return creditCards.get(primaryCreditCardIndex);
    }

    /**
     * Returns another random credit card based on an index. The index of this card is definitely
     * different from the primary card's index.
     */
    public CreditCard getSecondardyCard()
    {
        if (secondaryCreditCardIndex < 0)
        {
            Assert.fail(MessageFormat.format("No secondary creditcard available for {0}", this));
        }

        return creditCards.get(secondaryCreditCardIndex);
    }
}
