package com.xceptance.loadtest.api.util.crawler;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public enum LinkCache
{
    URLS;

    private final RandomAccessCache<String> urls = RandomAccessCacheBuilder.newBuilder()
                                                                           .maximumSize(1000)
                                                                           .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                           .build();

    public static void put(final String url)
    {
        URLS.urls.put(url);
    }

    public static void putAll(final Collection<String> urls)
    {
        URLS.urls.putAll(urls);
    }

    /**
     *
     * @return random link or <code>null</code> if no element is present
     */
    public static String getRandom()
    {
        return URLS.urls.getRandom();
    }
}
