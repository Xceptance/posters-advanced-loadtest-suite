package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;

public class ConfigProbabilityTest
{
    public static final String KEY = "ConfigProbability";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1.probability", "50");

        final ConfigProbability1 c = ConfigurationBuilder.buildDefault(ConfigProbability1.class);
        Assert.assertEquals(50, c.foo.raw);

        // never changes
        final boolean b = c.foo.value;
        for (int i = 0; i < 1000; i++)
        {
            Assert.assertEquals(b, c.foo.value);
        }
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigProbability2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigProbability3 c = ConfigurationBuilder.buildDefault(ConfigProbability3.class);
        Assert.assertEquals(0, c.foo.raw);
        Assert.assertFalse(c.foo.value);
    }

    @Test
    public final void fallback()
    {
        final ConfigProbability4 c = ConfigurationBuilder.buildDefault(ConfigProbability4.class);
        Assert.assertEquals(42, c.foo.raw);
    }

    @Test
    public final void NoInt1()
    {
        XltProperties.getInstance().setProperty(KEY + "5.probability", "foo");

        try
        {
            ConfigurationBuilder.buildDefault(ConfigProbability5.class);
        }
        catch (final RuntimeException e)
        {
            Assert.assertTrue(e.getMessage().contains("NumberFormatException - For input string: \"foo\""));
        }
    }

    @Test
    public final void NoIntFallback2()
    {
        XltProperties.getInstance().removeProperty(KEY + "5.probability");

        try
        {
            ConfigurationBuilder.buildDefault(ConfigProbability5.class);
        }
        catch (final RuntimeException e)
        {
            Assert.assertTrue(e.getMessage().contains("NumberFormatException - For input string: \"asdf\""));
        }
    }

    @Test
    public final void noAutoComplete()
    {
        XltProperties.getInstance().setProperty(KEY + "6.p", "50");

        final ConfigProbability6 c = ConfigurationBuilder.buildDefault(ConfigProbability6.class);
        Assert.assertEquals(50, c.foo.raw);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void immutable_default()
    {
        XltProperties.getInstance().setProperty(KEY + "6.p", "50");

        final ConfigProbability6 c = ConfigurationBuilder.buildDefault(ConfigProbability6.class);
        c.foo.random();
    }

    @Test
    public final void immutable_off()
    {
        XltProperties.getInstance().setProperty(KEY + "7.probability", "49");

        final ConfigProbability7 c = ConfigurationBuilder.buildDefault(ConfigProbability7.class);
        Assert.assertEquals(49, c.foo.raw);
        boolean b = false;

        for (int i = 0; i < 1000; i++)
        {
            b = b || c.foo.random();
        }

        // someone at least one true should be happened
        Assert.assertTrue(b);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void immutable_on()
    {
        XltProperties.getInstance().setProperty(KEY + "8.probability", "49");

        final ConfigProbability8 c = ConfigurationBuilder.buildDefault(ConfigProbability8.class);
        Assert.assertEquals(49, c.foo.raw);
        c.foo.random();
    }
}

class ConfigProbability1
{
    @Property(key = ConfigProbabilityTest.KEY + "1")
    public ConfigProbability foo;
}

class ConfigProbability2
{
    @Property(key = ConfigProbabilityTest.KEY + "2")
    public ConfigProbability foo;
}

class ConfigProbability3
{
    @Property(key = ConfigProbabilityTest.KEY + "3", required = false)
    public ConfigProbability foo;
}

class ConfigProbability4
{
    @Property(key = ConfigProbabilityTest.KEY + "4", fallback = "42")
    public ConfigProbability foo;
}

class ConfigProbability5
{
    @Property(key = ConfigProbabilityTest.KEY + "5", fallback = "asdf")
    public ConfigProbability foo;
}

class ConfigProbability6
{
    @Property(key = ConfigProbabilityTest.KEY + "6.p", autocomplete = false)
    public ConfigProbability foo;
}

class ConfigProbability7
{
    @Property(key = ConfigProbabilityTest.KEY + "7", immutable = false)
    public ConfigProbability foo;
}

class ConfigProbability8
{
    @Property(key = ConfigProbabilityTest.KEY + "8", immutable = true)
    public ConfigProbability foo;
}