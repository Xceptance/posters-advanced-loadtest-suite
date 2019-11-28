package com.xceptance.loadtest.api.hpu;

/**
 * HPU is just an abbreviation for HtmlPageUtils.
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
     * @return lookup base setter
     */
    public static In find()
    {
        return new In();
    }
}
