package com.xceptance.loadtest.api.data;

/**
 * Where did we get the account from.
 *
 * @author rschwietzke
 *
 */
public enum AccountOrigin
{
    RANDOM("RANDOM"), POOL("POOL"), FILE("FILE"), PROPERTIES("PROPERTIES"), OVERRIDE("OVERRIDE"), EXCLUSIVE("EXCLUSIVE");

    /**
     * plain text for debugging
     */
    private final String origin;

    /**
     * Constructor, just for a nice to string
     *
     * @param origin
     *            the verbal source for later toString
     */
    private AccountOrigin(final String origin)
    {
        this.origin = origin;
    }

    @Override
    public String toString()
    {
        return origin;
    }
}
