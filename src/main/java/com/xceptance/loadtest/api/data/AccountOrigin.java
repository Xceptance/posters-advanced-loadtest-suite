package com.xceptance.loadtest.api.data;

/**
 * Captures the origin of an account.
 *
 * @author Xceptance Software Technologies
 */
public enum AccountOrigin
{
    RANDOM("RANDOM"), POOL("POOL"), FILE("FILE"), PROPERTIES("PROPERTIES"), OVERRIDE("OVERRIDE"), EXCLUSIVE("EXCLUSIVE");

    /**
     * Plain text account origin.
     */
    private final String origin;

    /**
     * Constructor
     *
     * @param origin The verbal source for later toString
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