package com.xceptance.loadtest.api.util;

import java.text.MessageFormat;

public class Timer
{
    private final long ms;
    private final long ns;

    private long stoppedMs;
    private long stoppedNs;

    private boolean stopped = false;

    private Timer()
    {
        ms = System.currentTimeMillis();
        ns = System.nanoTime();
    }

    public static Timer start()
    {
        return new Timer();
    }

    public void stop(final String msg)
    {
        stop();

        System.out.println(MessageFormat.format(msg, runtimeMillis()));
    }

    public void stop()
    {
        stoppedMs = System.currentTimeMillis();
        stoppedNs = System.nanoTime();

        if (stopped)
        {
            throw new IllegalArgumentException("Stopped called twice");
        }

        stopped = true;
    }

    public long runtimeMillis()
    {
        return stoppedMs - ms;
    }

    public long runtimeNanos()
    {
        return stoppedNs - ns;
    }
}
