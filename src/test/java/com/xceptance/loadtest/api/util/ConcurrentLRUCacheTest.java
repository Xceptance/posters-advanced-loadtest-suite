package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.util.ConcurrentLRUCache;

/**
 * Tests for implementation of {@link ConcurrentLRUCacheTest}.
 *
 * @author Rene Schwietzke (Xceptance Software Technologies GmbH)
 */
public class ConcurrentLRUCacheTest
{
    @Test
    public final void testConcurrentLRUCache()
    {
        final ConcurrentLRUCache<String, String> cache = new ConcurrentLRUCache<>(100);
        Assert.assertNotNull(cache); // of course, it is not null...!
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testConcurrentLRUCache_TooSmall()
    {
        new ConcurrentLRUCache<String, String>(9);
    }

    @Test
    public final void testGet_CheckAllIn()
    {
        final ConcurrentLRUCache<Integer, Integer> cache = new ConcurrentLRUCache<>(12);

        for (int i = 1; i <= 12; i++)
        {
            cache.put(i, i);
        }
        for (int i = 1; i <= 12; i++)
        {
            Assert.assertTrue(i + " is in the cache", cache.contains(i));
        }

        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public final void testGet_CheckPushOut()
    {
        final ConcurrentLRUCache<Integer, Integer> cache = new ConcurrentLRUCache<>(12);

        // Because we have a cache of 3 blocks... the entire last block will go,
        // if the limit is reached
        for (int i = 1; i <= 13; i++)
        {
            cache.put(i, i);
        }
        for (int i = 5; i <= 12; i++)
        {
            Assert.assertTrue(i + " is in the cache", cache.contains(i));
        }
        for (int i = 1; i <= 4; i++)
        {
            Assert.assertFalse(i + " is in the cache", cache.contains(i));
        }

        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public final void testGet_TestLRU()
    {
        final ConcurrentLRUCache<Integer, Integer> cache = new ConcurrentLRUCache<>(12);

        // fill it
        for (int i = 1; i <= 12; i++)
        {
            cache.put(i, i);
        }
        // pull up 1 and 6
        cache.get(1);
        cache.get(6);

        // push out the last block, which is the old 1, and 2,3,4
        // but because 1 was moved up, we will find it again. The pull up of 1
        // made enough room for 13, so we do not push out anything else.
        cache.put(13, 13);

        Assert.assertTrue(cache.contains(1));
        Assert.assertFalse(cache.contains(2));
        Assert.assertFalse(cache.contains(3));
        Assert.assertFalse(cache.contains(4));
        Assert.assertTrue(cache.contains(5));
        Assert.assertTrue(cache.contains(6));
        Assert.assertTrue(cache.contains(7));
        Assert.assertTrue(cache.contains(8));
        Assert.assertTrue(cache.contains(9));
        Assert.assertTrue(cache.contains(10));
        Assert.assertTrue(cache.contains(11));
        Assert.assertTrue(cache.contains(12));
        Assert.assertTrue(cache.contains(13));

        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    /**
     * Test the size
     */
    @Test
    public final void testSize()
    {
        final ConcurrentLRUCache<Integer, Integer> cache = new ConcurrentLRUCache<>(12);
        Assert.assertEquals("Size differs", 0, cache.size());

        // fill it
        for (int i = 1; i <= 12; i++)
        {
            cache.put(i, i);
        }
        Assert.assertEquals("Size differs", 12, cache.size());

        // pull it up and we get rid of 1/3 of the cache due to the block
        // organization
        cache.get(1);
        Assert.assertEquals("Size differs", 9, cache.size());

        // we move the element 6 up and therefore it exists twice!!!
        cache.get(6);
        Assert.assertEquals("Size differs", 10, cache.size());

        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    /**
     * Test the size
     */
    @Test
    public final void testRemove()
    {
        final ConcurrentLRUCache<Integer, Integer> cache = new ConcurrentLRUCache<>(12);
        Assert.assertEquals("Size differs", 0, cache.size());

        // fill it
        for (int i = 1; i <= 12; i++)
        {
            cache.put(i, i);
        }
        Assert.assertEquals("Size differs", 12, cache.size());

        // remove something unknown
        Assert.assertNull("Object found, but not expected", cache.remove(20));

        // remove from level 1
        Assert.assertTrue("Wrong data found", cache.remove(12).compareTo(12) == 0);

        // remove from level 2
        Assert.assertTrue("Wrong data found", cache.remove(6).compareTo(6) == 0);

        // remove from level 3
        Assert.assertTrue("Wrong data found", cache.remove(1).compareTo(1) == 0);

        // pull up data
        cache.get(2);

        // remove
        Assert.assertNotNull(cache.remove(2));

        // check
        Assert.assertFalse(cache.contains(2));
    }

}
