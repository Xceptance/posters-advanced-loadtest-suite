package com.xceptance.loadtest.api.util.crawler;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

public class RandomAccessCacheBuilder
{
    final CacheBuilder<Object, Object> cb;

    private RandomAccessCacheBuilder()
    {
        cb = CacheBuilder.newBuilder();
    }

    public static RandomAccessCacheBuilder newBuilder()
    {
        return new RandomAccessCacheBuilder();
    }

    public RandomAccessCacheBuilder maximumSize(final int size)
    {
        cb.maximumSize(size);
        return this;
    }

    public RandomAccessCacheBuilder expireAfterWrite(final int duration, final TimeUnit unit)
    {
        cb.expireAfterAccess(duration, unit);
        return this;
    }

    public <T> RandomAccessCache<T> build()
    {
        return new RandomAccessCache<T>(cb);
    }
}
