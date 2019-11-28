package com.xceptance.loadtest.api.util;

import java.util.List;

import com.xceptance.xlt.api.util.XltRandom;

/**
 * List of useful random utilities
 *
 * @author rschwietzke
 *
 */
public class RandomUtils
{
    /**
     * Gets a random value from the list with equal weight on the full list using XltRandom
     *
     * @param list
     *            the list to get entries from
     *
     * @return a list entry or null if the list is empty, compare this to LookUpResult.random()
     */
    public static <T> T randomEntry(final List<T> list)
    {
        if (list.isEmpty() == false)
        {
            return list.get(XltRandom.nextInt(list.size()));
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets a random value from a list, weighting the later entries higher than the first based on
     * an assumed bucket size
     *
     * @param list
     *            the list to get entries from
     * @param bucketSize
     *            the assumed size of buckets to weight on
     *
     * @return a list entry or null if the list is empty, compare this to LookUpResult.random()
     */
    public static <T> T weightedRandomEntry(final List<T> list, final int bucketSize)
    {
        // ok, focus on the last bucket, if none, just do a regular random
        final int size = list.size();

        // nothing to weight on
        if (size <= bucketSize || bucketSize == 0)
        {
            return randomEntry(list);
        }

        final int offset = size - bucketSize;
        int pos = offset + (int) (bucketSize * XltRandom.nextDouble());

        // int pos = ((buckets - 1) * bucketSize) + (int) (lastBucketSize *
        // Math.sqrt(XltRandom.nextDouble()));

        if (pos > size - 1)
        {
            pos = size - 1;
        }

        return list.get(pos);
    }
}
