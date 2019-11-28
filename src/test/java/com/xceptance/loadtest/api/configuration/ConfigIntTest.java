package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigIntTest
{
    public static final String KEY = "ConfigInt";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1", "1");

        final ConfigInt1 c = ConfigurationBuilder.buildDefault(ConfigInt1.class);
        Assert.assertEquals(1, c.foo);
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigInt2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigInt3 c = ConfigurationBuilder.buildDefault(ConfigInt3.class);
        Assert.assertEquals(0, c.foo);
    }

    @Test
    public final void fallback()
    {
        final ConfigInt4 c = ConfigurationBuilder.buildDefault(ConfigInt4.class);
        Assert.assertEquals(42, c.foo);
    }

    @Test
    public final void NoInt1()
    {
        XltProperties.getInstance().setProperty(KEY + "5", "uasdh");

        try
        {
            ConfigurationBuilder.buildDefault(ConfigInt5.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("not an integer"));
        }
    }

    @Test
    public final void NoIntFallback2()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigInt5.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("not an integer"));
        }
    }
}

class ConfigInt1
{
    @Property(key = ConfigIntTest.KEY + "1")
    public int foo;
}

class ConfigInt2
{
    @Property(key = ConfigIntTest.KEY + "2")
    public int foo;
}

class ConfigInt3
{
    @Property(key = ConfigIntTest.KEY + "3", required = false)
    public int foo;
}

class ConfigInt4
{
    @Property(key = ConfigIntTest.KEY + "4", fallback = "42")
    public int foo;
}

class ConfigInt5
{
    @Property(key = ConfigIntTest.KEY + "5", fallback = "asdf")
    public int foo;
}