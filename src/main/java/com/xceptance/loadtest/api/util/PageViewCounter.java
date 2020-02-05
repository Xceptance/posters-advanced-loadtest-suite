package com.xceptance.loadtest.api.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import com.xceptance.loadtest.api.actions.NonPageView;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.xlt.api.engine.CustomValue;
import com.xceptance.xlt.api.engine.Session;

/**
 * Helper to count the page views of a user.
 * 
 * @autor Xceptance Software Technologies
 */
public class PageViewCounter
{
    /**
     * Maps users (identified by their thread group) to the corresponding page view
     * counter
     */
    private static final Map<ThreadGroup, LongAdder> COUNTERS = new ConcurrentHashMap<>(100);

    /**
     * Increases the current user's page view counter by 1.<br>
     * {@link NonPageView}s will be skipped.
     *
     * @param action
     *            the action to count
     */
    public static void count(final PageAction<?> action)
    {
        // only take page views into account
        if (action instanceof NonPageView)
        {
            return;
        }

        // increase the user's page view counter by 1
        getPageViewCount(getUserId()).increment();
    }

    /**
     * Writes the current user's page view counter to the logs and purges the
     * counter afterwards.
     */
    public static void publishAndDestroy()
    {
        // get counter
        final Session currentSession = Session.getCurrent();
        final ThreadGroup userId = getUserId();
        final String testCaseName = currentSession.getUserName();
        final int pageViewCount = getPageViewCount(userId).intValue();

        // log counter
        final CustomValue customValue = new CustomValue();
        customValue.setName(testCaseName);
        customValue.setValue(pageViewCount);
        currentSession.getDataManager().logDataRecord(customValue);

        // destroy counter
        COUNTERS.remove(userId);
    }

    /**
     * Get the current user's thread group, which is taken as key in the counter
     * map.
     *
     * @return the current user's thread group
     */
    private static ThreadGroup getUserId()
    {
        return Thread.currentThread().getThreadGroup();
    }

    /**
     * Get the current user's page view counter.
     *
     * @param key
     *            the current user's thread group
     * @return the current user's page view counter
     */
    private static LongAdder getPageViewCount(final ThreadGroup key)
    {
        return COUNTERS.computeIfAbsent(key, k -> new LongAdder());
    }
}