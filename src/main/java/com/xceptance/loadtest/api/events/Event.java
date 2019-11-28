package com.xceptance.loadtest.api.events;

import com.xceptance.loadtest.api.configuration.LTProperties;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.engine.Session;

public class Event
{
    static Event init(final String propertyName)
    {
        // Property lookup and decision
        final LTProperties properties = Context.configuration().properties;
        final String defaultLogLevel = properties.getProperty("events.logging.default.level", "debug");
        final String logLevel = properties.getProperty("events.logging." + propertyName + ".level", defaultLogLevel).toLowerCase();

        final Event event;
        switch (logLevel) {
            case "error":
                event = new ERROR();
                break;

            case "warn":
                event = new WARN();
                break;

            case "info":
                event = new INFO();
                break;

            case "debug":
                event = new DEBUG();
                break;

            default:
                throw new IllegalArgumentException("Log level '" + logLevel + "' not supported.");
        }

        return event;
    }

    public void error(final String name, final String msg)
    {
        return;
    }

    public void warn(final String name, final String msg)
    {
        return;
    }

    public void info(final String name, final String msg)
    {
        return;
    }

    public void debug(final String name, final String msg)
    {
        return;
    }

    private static class DEBUG extends INFO
    {
        @Override
        public void debug(final String name, final String msg)
        {
            Session.logEvent(name, msg);
        }
    }

    private static class INFO extends WARN
    {
        @Override
        public void info(final String name, final String msg)
        {
            Session.logEvent(name, msg);
        }
        //
        // @Override
        // public void debug(final String name, final String msg)
        // {
        // Session.logEvent(name, "");
        // }
    }

    private static class WARN extends ERROR
    {
        @Override
        public void warn(final String name, final String msg)
        {
            Session.logEvent(name, msg);
        }
    }

    private static class ERROR extends Event
    {
        @Override
        public void error(final String name, final String msg)
        {
            Session.logEvent(name, msg);
        }
    }
}
