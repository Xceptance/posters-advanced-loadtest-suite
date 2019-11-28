package com.xceptance.loadtest.api.util;

import com.google.common.base.Strings;

/**
 * Just a class with utility formatters for easier reuse
 *
 * @author rschwietzke
 */
public class Format
{
    // value must be 1 or higher
    private static final int BUCKETSIZE = 1;

    /**
     * Just make the counter format a little more compact, not yet configurable
     *
     * @param timerName
     *            the timername to use
     * @param count
     *            the number to put into the new name
     *
     * @return a formatter time, such as ViewCart-001-020
     */
    @SuppressWarnings("unused")
    public static String timerName(final String timerName, final int count)
    {
        final int bucketNumber = (count == 0 ? 1 : count) / BUCKETSIZE;

        // when we have no buckets, we spare the 'to' part
        if (BUCKETSIZE > 1)
        {
            return timerName
                            + "-" +
                            Strings.padStart(String.valueOf((bucketNumber * BUCKETSIZE) + 1), 3, '0')
                            + "-to-"
                            + Strings.padStart(String.valueOf((bucketNumber + 1) * BUCKETSIZE), 3, '0');
        }
        else
        {
            return timerName + "-" +
                            Strings.padStart(String.valueOf((bucketNumber * BUCKETSIZE) + 1), 3, '0');
        }
    }
}
