package com.xceptance.loadtest.api.hpu;

/**
 * Html page utils.
 * 
 * @autor Xceptance Software Technologies
 */
public class HPU
{
    /**
     * Private constructor to prevent instantiation.
     */
    private HPU()
    {
        // nothing
    }

    /**
     * Initializes finder.
     * 
     * @return Lookup base setter
     */
    public static In find()
    {
        return new In();
    }
}