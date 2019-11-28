package com.xceptance.loadtest.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.xlt.api.util.XltRandom;

public class RandomUtilsTest
{
    /**
     * Test selection from a list
     */
    @Test
    public void randomEntryUnWeighted()
    {
        // fix up random
        XltRandom.setSeed(1);
        Assert.assertEquals(1, XltRandom.getSeed());

        final List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        final int[] result = new int[10];

        for (int i = 0; i < 10000; i++)
        {
            result[RandomUtils.randomEntry(list)]++;
        }

        for (int i = 0; i < 10; i++)
        {
            Assert.assertTrue(result[i] > (10000 / 10) * 0.95);
        }
    }

    /**
     * Just a helper for the later tests
     *
     * @param bucketSize
     *            the bucket size to use
     * @param size
     *            the size of the list to get entries from
     * @return
     */
    private int[] helperWeightedList(final int bucketSize, final int size)
    {
        final List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            list.add(i);
        }

        final int[] result = new int[list.size()];

        // will result in equally weighted due to having a bucket
        for (int i = 0; i < 10000; i++)
        {
            result[RandomUtils.weightedRandomEntry(list, bucketSize)]++;
        }

        return result;
    }

    /**
     * Test a weighted selection from a list, edge case with equal weight
     */
    @Test
    public void randomEntryWeighted()
    {
        // full random because 0 buckets are not supported
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 1025, 1003, 960, 984, 986, 955, 1024, 998, 1000, 1065 };
            final int[] result = helperWeightedList(0, 10);
            Assert.assertArrayEquals(target, result);
        }

        // bucket too large
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 1025, 1003, 960, 984, 986, 955, 1024, 998, 1000, 1065 };
            final int[] result = helperWeightedList(20, 10);
            Assert.assertArrayEquals(target, result);
        }

        // only last entry is interesting
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 10000 };
            final int[] result = helperWeightedList(1, 10);
            Assert.assertArrayEquals(target, result);
        }

        // last two entries are mine
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 0, 0, 0, 0, 0, 0, 0, 0, 4982, 5018 };
            final int[] result = helperWeightedList(2, 10);
            Assert.assertArrayEquals(target, result);
        }

        // 10 / 3 is 3 remainder 1 hence our last bucket is larger
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 0, 0, 0, 0, 0, 0, 0, 3273, 3332, 3395 };
            final int[] result = helperWeightedList(3, 10);
            Assert.assertArrayEquals(target, result);
        }

        // 10 of 11
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 0, 1002, 957, 988, 1000, 1035, 967, 1015, 960, 1088, 988 };
            final int[] result = helperWeightedList(10, 11);
            Assert.assertArrayEquals(target, result);
        }

        // 10 of 12
        {
            // fix up random
            XltRandom.setSeed(1);
            final int[] target = { 0, 0, 1002, 957, 988, 1000, 1035, 967, 1015, 960, 1088, 988 };
            final int[] result = helperWeightedList(10, 12);
            Assert.assertArrayEquals(target, result);
        }
    }
}
