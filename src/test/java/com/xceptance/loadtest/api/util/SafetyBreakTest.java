package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.Test;

import util.TestUtils;

public class SafetyBreakTest
{
    /**
     * Creates a safety break with a positive amount of tries. No exception
     * should be thrown.
     */
    @Test
    public void safetyBreakConstructor()
    {
        new SafetyBreak(1);
        new SafetyBreak(5);
        new SafetyBreak(Integer.MAX_VALUE);
    }

    /**
     * Create a safety break and decrease its value, but don't exceed the limit.
     * There should be no exceptions thrown.
     */
    @Test
    public void check() throws FlowStoppedException
    {
        // Don't reach limit
        createAndcheckSafetyBreak(2, 1);
        createAndcheckSafetyBreak(5, 1);
        createAndcheckSafetyBreak(5, 4);
        createAndcheckSafetyBreak(Integer.MAX_VALUE, 1);

        // Reach limit exactly, but don't exceed it
        createAndcheckSafetyBreak(1, 1);
        createAndcheckSafetyBreak(5, 5);
    }

    @Test
    public void check_maxInteger() throws Exception
    {
        // Don't reach limit
        final SafetyBreak safety1 = new SafetyBreak(Integer.MAX_VALUE);
        // Skip tries until there are only two left
        TestUtils.setFieldValue(safety1, "currentValue", 2);
        // Almost reach limit. There should be no Exception thrown
        safety1.check();

        // Reach limit exactly, but don't exceed it
        final SafetyBreak safety2 = new SafetyBreak(Integer.MAX_VALUE);
        // Skip tries until there is only one left
        TestUtils.setFieldValue(safety2, "currentValue", 1);
        // Reach limit. There should be no Exception thrown
        safety2.check();
    }

    /**
     * Create a Safety break and decrease its value. Do not exceed the limit and
     * check if the expected result is returned.
     */
    @Test
    public void reached()
    {
        // Don't reach limit. Result should be "false"
        createAndReachSafetyBreak(2, 1, false);
        createAndReachSafetyBreak(5, 1, false);
        createAndReachSafetyBreak(5, 4, false);

        // Reach limit exactly, but don't exceed it. Result should be "false"
        createAndReachSafetyBreak(1, 1, false);
        createAndReachSafetyBreak(5, 5, false);
    }

    /**
     * Create a Safety break and decrease its value. Exceed the limit and check
     * if the expected result is returned.
     */
    @Test
    public void reached_exceedLimit()
    {
        // Exceed the limit. Result should be "true"
        createAndReachSafetyBreak(1, 2, true);
        createAndReachSafetyBreak(5, 6, true);
        createAndReachSafetyBreak(5, 12, true);
    }

    @Test
    public void reached_maxInteger() throws Exception
    {
        // Don't reach limit. Result should be "false"
        final SafetyBreak safety1 = new SafetyBreak(Integer.MAX_VALUE);
        // Skip tries until there are only two left
        TestUtils.setFieldValue(safety1, "currentValue", 2);
        Assert.assertFalse(safety1.reached());

        // Reach limit exactly, but don't exceed it. Result should be "false"
        final SafetyBreak safety2 = new SafetyBreak(Integer.MAX_VALUE);
        // Skip tries until there is only one left
        TestUtils.setFieldValue(safety2, "currentValue", 1);
        Assert.assertFalse(safety2.reached());
    }

    @Test
    public void reached_maxInteger_eceedLimit() throws Exception
    {
        // Exceed the limit. Result should be "true"
        final SafetyBreak safety3 = new SafetyBreak(Integer.MAX_VALUE - 1);
        // Skip tries until there are none left
        TestUtils.setFieldValue(safety3, "currentValue", 0);
        Assert.assertTrue(safety3.reached());
    }

    /**
     * Checks if the safety break can handle maximum negative values.
     *
     * @throws Exception
     */
    @Test
    public void reached_exceedMinInteger() throws Exception
    {
        final int maxTries = 5;
        final int failedAttempts = 2 * maxTries;

        final SafetyBreak safetyBreak = new SafetyBreak(maxTries);

        // Exceed the safety break limit
        reachSafetyBreak(safetyBreak, maxTries, failedAttempts, true);

        // Exceed the limit by more than Integer.MAX_VALUE. The result should
        // still be "true". However, the number of tries might go below
        // Integer.MIN_VALUE
        TestUtils.setFieldValue(safetyBreak, "currentValue", Integer.MIN_VALUE);
        Assert.assertTrue(safetyBreak.reached());
        Assert.assertTrue(safetyBreak.reached());
    }

    /** Resets the number of tries in a safety break back to max. */
    @Test
    public void reset()
    {
        final int remainingTries = 5;

        final SafetyBreak safetyBreak = new SafetyBreak(remainingTries);

        // Decrease safety break value down to 0. Safety break should not be
        // triggered
        reachSafetyBreak(safetyBreak, remainingTries, remainingTries, false);

        // Reset the number of tries back to max.
        safetyBreak.reset();

        // Number of tries should be back to max. Decrease the value down to 0
        // again. Safety break should not be triggered
        reachSafetyBreak(safetyBreak, remainingTries, remainingTries, false);

        // Zero tries remaining. Decrease the value below zero. This should
        // trigger the safety break
        reachSafetyBreak(safetyBreak, 0, 1, true);
    }

    /**
     * Creates a safety break with zero maxTries. This should result in an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void safetyBreakConstructor_zero()
    {
        new SafetyBreak(0);

        Assert.fail("Creating a safety break with 0 max tries should throw an exception.");
    }

    /**
     * Creates a safety break with a negative amount of maxTries. This should
     * result in an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void safetyBreakConstructor_negative()
    {
        new SafetyBreak(-1);

        Assert.fail("Creating a safety break less than 0 max tries should throw an exception.");
    }

    /**
     * Safety break value is decreased one step past its limit. Should throw
     * FlowStoppedException.
     */
    @Test(expected = FlowStoppedException.class)
    public void check_exceedLimit() throws FlowStoppedException
    {
        createAndcheckSafetyBreak(5, 6);
    }

    /**
     * Decrease safety break value again after it already went beyond its limit.
     * Should throw FlowStoppedException.
     */
    @Test(expected = FlowStoppedException.class)
    public void check_exceedLimit_twice() throws FlowStoppedException
    {
        final int maxTries = 10;

        final SafetyBreak safetyBreak = new SafetyBreak(maxTries);

        try
        {
            // Exceed the safety break limit, but avoid the first exception
            checkSafetyBreak(safetyBreak, maxTries + 1);
        }
        catch (final FlowStoppedException e)
        {
            // Go even further past the safety break limit. This should throw an
            // exception
            safetyBreak.check();
        }

        Assert.fail("Safety Break should have thrown exception.");
    }

    /**
     * Creates a safety break and decreases the number of available tries using
     * the check()-method. Should throw a FlowStoppedException, if the safety
     * break limit is reached.
     */
    private void createAndcheckSafetyBreak(final int maxTries, final int failedAttempts) throws FlowStoppedException
    {
        final SafetyBreak safetyBreak = new SafetyBreak(maxTries);

        checkSafetyBreak(safetyBreak, failedAttempts);
    }

    private void checkSafetyBreak(final SafetyBreak safetyBreak, final int failedAttempts) throws FlowStoppedException
    {
        for (int i = 0; i < failedAttempts; i++)
        {
            safetyBreak.check();
        }
    }

    /**
     * Creates a safety break and decreases the number of available tries using
     * the reached()-method. Checks if this method returns the correct result.
     */
    private void createAndReachSafetyBreak(final int maxTries, final int failedAttempts, final boolean expected)
    {
        final SafetyBreak safetyBreak = new SafetyBreak(maxTries);

        reachSafetyBreak(safetyBreak, maxTries, failedAttempts, expected);
    }

    /**
     * Decreases the number of available tries of a safety break using the
     * reached()-method. Checks if this method returns the correct result.
     */
    private void reachSafetyBreak(final SafetyBreak safetyBreak, final int remainingTries, final int failedAttempts, final boolean expected)
    {
        boolean limitReached = false;

        for (int i = 0; i < failedAttempts; i++)
        {
            // Check if limit was reached (decreases available number of tries)
            limitReached = safetyBreak.reached();

            if (i < remainingTries)
            {
                // Limit should not be reached, when there are still tries
                // remaining
                Assert.assertFalse("Safety break limit was reached too early. Remaining tries: " +
                                remainingTries + ". Failed attempts: " + (i + 1) + ".", limitReached);
            }
            else
            {
                // Limit should be reached, if there are more failed attempts
                // than remaining tries
                Assert.assertTrue("Safety break limit should have been reached.", limitReached);
            }
        }

        // Check the end result
        Assert.assertEquals("Reached check returned an unexpected result.", expected, limitReached);
    }
}
