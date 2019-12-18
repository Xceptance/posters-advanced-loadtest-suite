package com.xceptance.loadtest.api.models.components;

import org.junit.Assert;

import com.xceptance.loadtest.api.hpu.LookUpResult;

public interface Component
{
    /**
     * Find this component on the page
     *
     * @return a lookup result
     */
    public LookUpResult locate();

    /**
     * Check if this component exists on the page
     *
     * @return true if this component exists
     */
    public boolean exists();

    /**
     * Methods to make stacktraces containing the failing component
     *
     * @param message
     *            message to print
     * @param value
     *            value to check
     */
    default void assertTrue(final String message, final boolean value)
    {
        Assert.assertTrue(message, value);
    }

    /**
     * Methods to make stacktraces containing the failing component
     *
     * @param message
     *            message to print
     * @param value
     *            value to check
     */
    default void assertFalse(final String message, final boolean value)
    {
        Assert.assertFalse(message, value);
    }
}
