package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigStringTest
{
    public static final String KEY = "ConfigString";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1", "test");

        final Config1 c = ConfigurationBuilder.buildDefault(Config1.class);
        Assert.assertEquals("test", c.foo);
        c.foo = "bar";
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(Config2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final Config3 c = ConfigurationBuilder.buildDefault(Config3.class);
        Assert.assertNull(c.foo);
    }

    @Test
    public final void fallback()
    {
        final Config4 c = ConfigurationBuilder.buildDefault(Config4.class);
        Assert.assertEquals("back", c.foo);
    }
}

class Config1
{
    @Property(key = ConfigStringTest.KEY + "1")
    public String foo;
}

class Config2
{
    @Property(key = ConfigStringTest.KEY + "2")
    public String foo;
}

class Config3
{
    @Property(key = ConfigStringTest.KEY + "3", required = false)
    public String foo;
}

class Config4
{
    @Property(key = ConfigStringTest.KEY + "4", fallback = "back")
    public String foo;
}
