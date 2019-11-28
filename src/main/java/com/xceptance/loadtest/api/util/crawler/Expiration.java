package com.xceptance.loadtest.api.util.crawler;

import java.util.concurrent.TimeUnit;

import com.xceptance.loadtest.api.util.Context;

/**
 * The expire interface that has to be implemented by the dropper algorithm of
 * choice.
 *
 * @author rschwietzke
 *
 */
public abstract class Expiration
{
    /**
     * Expires the current state
     */
    protected void expire()
    {
        Context.getPage().getWebClient().getCookieManager().clearCookies();
    }

    /**
     * Checks if the current state requires expiration
     *
     * @return true if expiration was performed, false otherwise
     */
    public abstract boolean expireIfReached();

    public static TimeExpiration every(final long value, final TimeUnit timeUnit)
    {
        final TimeExpiration te = new TimeExpiration();
        te.value = timeUnit.toMillis(value);

        return te;
    }

    public static PageExpiration every(final int pages)
    {
        final PageExpiration pe = new PageExpiration();
        pe.everyPages = pages;

        return pe;
    }

    public static PageExpiration always()
    {
        final PageExpiration pe = new PageExpiration();
        pe.everyPages = 1;

        return pe;
    }

    public static PageExpiration never()
    {
        final PageExpiration pe = new PageExpiration();
        pe.everyPages = -1;

        return pe;
    }
}

class TimeExpiration extends Expiration
{
    // this is the expiration value in msec
    long value = 0;

    // last expiration
    private long lastExpiration = System.currentTimeMillis();

    @Override
    public boolean expireIfReached()
    {
        // never expire
        if (value < 0)
        {
            return false;
        }

        final long now = System.currentTimeMillis();

        if (lastExpiration + value > now)
        {
            expire();
            lastExpiration = now;

            return true;
        }
        else
        {
            return false;
        }
    }
}

class PageExpiration extends Expiration
{
    // expire every pages
    int everyPages = 1;

    // the counter since last asked
    int counter = 0;

    @Override
    public boolean expireIfReached()
    {
        // never expire!
        if (this.everyPages < 0)
        {
            return false;
        }

        // we got asked
        counter++;

        if (counter == everyPages)
        {
            expire();
            counter = 0;

            return true;
        }
        else
        {
            return false;
        }
    }
}