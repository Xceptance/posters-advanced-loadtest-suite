package com.xceptance.loadtest.api.util.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Queues;
import com.xceptance.xlt.api.util.XltRandom;

public class RandomAccessCache<T>
{
    // provides limited size + expiration
    private final Cache<T, Integer> cache;

    // knows active indexes + allows random access
    private final List<Integer> linkedIndexes;

    // knows free indexes
    private final Queue<Integer> freeIndexes;

    // knows elements + fixed size
    private final Object[] elements;

    RandomAccessCache(final CacheBuilder<Object, Object> cacheBuilder)
    {
        cache = cacheBuilder.removalListener(new RemovalListener<T, Integer>()
        {
            @Override
            public void onRemoval(final RemovalNotification<T, Integer> notification)
            {
                synchronized (linkedIndexes)
                {
                    linkedIndexes.remove(notification.getValue());
                }
                freeIndexes.add(notification.getValue());
            }
        }).build();

        final int size = getCacheSize(cache);

        elements = new Object[size];

        new ArrayList<>((int) Math.ceil(size * 1.4));

        freeIndexes = Queues.newConcurrentLinkedQueue();
        for (int i = 0; i < size; i++)
        {
            freeIndexes.add(i);
        }

        linkedIndexes = new LinkedList<>();
    }

    public void put(final T element)
    {
        // get a free slot
        final Integer freeIndex = freeIndexes.poll();
        if (freeIndex != null)
        {
            synchronized (linkedIndexes)
            {
                // add the URL at free position
                elements[freeIndex] = element;

                // create the link
                linkedIndexes.add(freeIndex);
            }

            // activate timer and link URL
            cache.put(element, freeIndex);
        }
        // else: drop url silently
    }

    public void putAll(final Collection<T> elements)
    {
        if (elements != null)
        {
            elements.forEach(element -> put(element));
        }
    }

    /**
     *
     * @return random cached element or <code>null</code> if no element is present
     */
    @SuppressWarnings("unchecked")
    public T getRandom()
    {
        synchronized (linkedIndexes)
        {
            if (!linkedIndexes.isEmpty())
            {
                return (T) elements[linkedIndexes.get(XltRandom.nextInt(linkedIndexes.size()))];
            }
        }

        return null;
    }

    private int getCacheSize(final Cache<?, ?> cache)
    {
        final long cacheSize = cache.size();
        if (cacheSize > Integer.MAX_VALUE)
        {
            throw new IllegalStateException("Cache size must not exceed " + Integer.MAX_VALUE);
        }
        return (int) cacheSize;
    }
}
