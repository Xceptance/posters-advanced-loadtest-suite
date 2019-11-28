package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.Test;

public class RoundRobinTest
{
    @Test
    public void getIndex_sizeOne()
    {
        final int i = RoundRobin.getIndex(1, 0, 0);
        Assert.assertEquals(0, i);
    }

    @Test
    public void getIndex_sizeOne_offsetOverflow()
    {
        final int i = RoundRobin.getIndex(1, 0, 1);
        Assert.assertEquals(0, i);
    }

    @Test
    public void getIndex_offsetNoOverflow()
    {
        final int i = RoundRobin.getIndex(5, 2, 2);
        Assert.assertEquals(4, i);
    }

    @Test
    public void getIndex_offsetOverflow()
    {
        final int i = RoundRobin.getIndex(5, 2, 4);
        Assert.assertEquals(1, i);
    }

    @Test
    public void getIndex_offsetMultipleOverflow()
    {
        final int i = RoundRobin.getIndex(5, 2, 9);
        Assert.assertEquals(1, i);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndex_sizeSubZero()
    {
        RoundRobin.getIndex(-1, 0, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndex_sizeZero()
    {
        RoundRobin.getIndex(0, 0, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndex_startIndexSubZero()
    {
        RoundRobin.getIndex(5, -1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndex_startIndexTooLarge()
    {
        RoundRobin.getIndex(5, 5, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIndex_offsetNegative()
    {
        RoundRobin.getIndex(5, 5, 1);
    }
}
