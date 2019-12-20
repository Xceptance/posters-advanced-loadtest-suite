package com.xceptance.loadtest.api.models.components;

import org.junit.Assert;

import com.xceptance.loadtest.api.hpu.LookUpResult;

/**
 * Component interface.
 * 
 * @author Xceptance Software Technologies.
 */
public interface Component
{
    /**
     * Find the component on the current page.
     *
     * @return A lookup result.
     */
    public LookUpResult locate();

    /**
     * Checks if the component exists on the current page.
     *
     * @return true if this component exists, false otherwise.
     */
    public boolean exists();

    /**
     * Enables stack traces to contain the failing component.
     *
     * @param message The message to print
     * @param value The value to check
     */
    default void assertTrue(final String message, final boolean value)
    {
        Assert.assertTrue(message, value);
    }

    /**
     * Enables stack traces to contain the failing component.
     *
     * @param message The message to print
     * @param value The value to check
     */
    default void assertFalse(final String message, final boolean value)
    {
        Assert.assertFalse(message, value);
    }
}