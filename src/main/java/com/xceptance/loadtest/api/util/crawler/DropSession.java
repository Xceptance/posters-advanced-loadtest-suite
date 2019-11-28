package com.xceptance.loadtest.api.util.crawler;

import com.xceptance.loadtest.api.util.Context;

/**
 * Session Dropper used for crawler.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class DropSession
{
    /** Indicator to drop a session never */
    static final int DROP_NEVER = -1;

    /** Indicator to drop a session never */
    static final int DROP_ALWAYS = 0;

    /** Drop value unit */
    public enum DropUnit
    {
        SECONDS,
        PAGES
    }

    /** When to drop the session */
    private int threshold;

    /** Threshold unit */
    private DropUnit unit;

    /** Number of pages since last session drop. */
    private int pageCount;

    /** Timestamp of last session drop. */
    private long lastDropTime;

    /**
     * Create new session dropper
     */
    DropSession()
    {
        reset();
    }

    /**
     * Define session drop threshold value.
     *
     * @param threshold
     * @return threshold unit query
     */
    public DropSession every(final int threshold, final DropUnit unit)
    {
        this.unit = unit;

        // Check for valid value.
        if (threshold <= 0)
        {
            throw new IllegalArgumentException("Threshold must be greater than zero");
        }

        switch (unit) {
            case SECONDS:
                this.threshold = threshold * 1000;
                break;
            case PAGES:
                this.threshold = threshold;
        }

        return this;
    }

    /**
     * Drop session always.
     *
     * @return resulting crawler configuration
     */
    public DropSession always()
    {
        this.threshold = DROP_ALWAYS;
        return this;
    }

    /**
     * Drop session never.
     *
     * @return resulting crawler configuration
     */
    public DropSession never()
    {
        this.threshold = DROP_NEVER;
        return this;
    }

    /**
     * Update session dropper. That might trigger a session drop if configured
     * threshold is reached.
     */
    public void update()
    {
        switch (threshold)
        {
            case DROP_ALWAYS:
                dropSession();
                break;

            case DROP_NEVER:
                // Do nothing.
                break;

            default:
                // Decide if threshold unit is page or time
                switch (unit)
                {
                    case PAGES:
                        // Drop session if current page count has reached
                        // threshold page count.
                        if (++pageCount >= threshold)
                        {
                            dropSession();
                        }
                        break;
                    case SECONDS:
                        // Drop session if time since last session drop has
                        // reached or exceeded threshold time.
                        if (now() - lastDropTime >= threshold)
                        {
                            dropSession();
                        }
                        break;
                }
        }
    }

    /**
     * Drop the browser session.
     */
    private void dropSession()
    {
        dropCookies();
        reset();
    }

    /**
     * Set page counter to zero and last drop time to current time.
     */
    private void reset()
    {
        lastDropTime = now();
        pageCount = 0;
    }

    /**
     * Drop all cookies.
     */
    private void dropCookies()
    {
        Context.getPage().getWebClient().getCookieManager().clearCookies();
    }

    /**
     * Current timestamp.
     *
     * @return current timestamp
     */
    private long now()
    {
        return System.currentTimeMillis();
    }
}
