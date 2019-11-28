package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigBooleanTest1
{
    public static final String KEY = "ConfigBoolean";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1a", "true");
        XltProperties.getInstance().setProperty(KEY + "1b", "false");

        final ConfigBoolean1 c = ConfigurationBuilder.buildDefault(ConfigBoolean1.class);
        Assert.assertTrue(c.foo1a);
        Assert.assertFalse(c.foo1b);
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigBoolean2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigBoolean3 c = ConfigurationBuilder.buildDefault(ConfigBoolean3.class);
        Assert.assertFalse(c.foo);
    }

    @Test
    public final void fallbackA()
    {
        final ConfigBoolean4a c = ConfigurationBuilder.buildDefault(ConfigBoolean4a.class);
        Assert.assertTrue(c.foo);
    }

    @Test
    public final void fallbackB()
    {
        final ConfigBoolean4b c = ConfigurationBuilder.buildDefault(ConfigBoolean4b.class);
        Assert.assertFalse(c.foo);
    }

    @Test
    public final void NoBoolean1()
    {
        XltProperties.getInstance().setProperty(KEY + "5", "uasdh");

        try
        {
            ConfigurationBuilder.buildDefault(ConfigBoolean5.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("not an boolean"));
        }
    }

    @Test
    public final void NoBooleanFallback2()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigBoolean5.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("not an boolean"));
        }
    }
}

class ConfigBoolean1
{
    @Property(key = ConfigBooleanTest1.KEY + "1a")
    public boolean foo1a;

    @Property(key = ConfigBooleanTest1.KEY + "1b")
    public boolean foo1b;
}

class ConfigBoolean2
{
    @Property(key = ConfigBooleanTest1.KEY + "2")
    public boolean foo;
}

class ConfigBoolean3
{
    @Property(key = ConfigBooleanTest1.KEY + "3", required = false)
    public boolean foo;
}

class ConfigBoolean4a
{
    @Property(key = ConfigBooleanTest1.KEY + "4a", fallback = "true")
    public boolean foo;
}

class ConfigBoolean4b
{
    @Property(key = ConfigBooleanTest1.KEY + "4b", fallback = "false")
    public boolean foo;
}

class ConfigBoolean5
{
    @Property(key = ConfigBooleanTest1.KEY + "5", fallback = "asdf")
    public boolean foo;
}