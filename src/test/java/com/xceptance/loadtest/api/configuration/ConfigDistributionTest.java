package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigDistributionTest
{
    public static final String KEY = "ConfigDistribution";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1.distribution", "1/1 2/1 3/2");

        final ConfigDistribution1 c = ConfigurationBuilder.buildDefault(ConfigDistribution1.class);
        Assert.assertEquals(1, c.foo.raw[0]);
        Assert.assertEquals(2, c.foo.raw[1]);
        Assert.assertEquals(3, c.foo.raw[2]);
        Assert.assertEquals(3, c.foo.raw[3]);

        Assert.assertTrue(c.foo.value > 0 && c.foo.value < 4);

        // yeah, not really great, but good enough
        final int last = c.foo.random();
        boolean wasRandom = false;
        for (int i = 0; i < 1000; i++)
        {
            if (last != c.foo.random())
            {
                wasRandom = true;
                break;
            }
        }
        Assert.assertTrue(wasRandom);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void happyPathImmutable()
    {
        XltProperties.getInstance().setProperty(KEY + "1a.distribution", "1/1 2/1");

        final ConfigDistribution1a c = ConfigurationBuilder.buildDefault(ConfigDistribution1a.class);
        c.foo.random();
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigDistribution2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigDistribution3 c = ConfigurationBuilder.buildDefault(ConfigDistribution3.class);
        Assert.assertEquals(0, c.foo.raw[0]);
        Assert.assertTrue(c.foo.raw.length == 1);
    }

    @Test
    public final void fallback()
    {
        final ConfigDistribution4 c = ConfigurationBuilder.buildDefault(ConfigDistribution4.class);
        Assert.assertEquals(3, c.foo.raw.length);

        Assert.assertEquals(3, c.foo.raw[0]);
        Assert.assertEquals(4, c.foo.raw[1]);
        Assert.assertEquals(5, c.foo.raw[2]);

    }

    @Test
    public final void fallbackNoNeeded()
    {
        XltProperties.getInstance().setProperty(KEY + "4a.distribution", "1/1 2/1 3/1");

        final ConfigDistribution4a c = ConfigurationBuilder.buildDefault(ConfigDistribution4a.class);
        Assert.assertEquals(3, c.foo.raw.length);

        Assert.assertEquals(1, c.foo.raw[0]);
        Assert.assertEquals(2, c.foo.raw[1]);
        Assert.assertEquals(3, c.foo.raw[2]);

    }

    @Test
    public final void empty()
    {
        final ConfigDistribution5 c = ConfigurationBuilder.buildDefault(ConfigDistribution5.class);
        Assert.assertTrue(c.foo.raw.length == 1);
        Assert.assertTrue(c.foo.raw[0] == 0);
    }

    @Test
    public final void oneDifferentDelimiter()
    {
        final ConfigDistribution6 c = ConfigurationBuilder.buildDefault(ConfigDistribution6.class);
        Assert.assertEquals(4, c.foo.raw.length);
        Assert.assertEquals(1, c.foo.raw[0]);
        Assert.assertEquals(2, c.foo.raw[1]);
        Assert.assertEquals(2, c.foo.raw[2]);
        Assert.assertEquals(3, c.foo.raw[3]);

    }

    @Test
    public final void threeDifferentDelimiter()
    {
        final ConfigDistribution7 c = ConfigurationBuilder.buildDefault(ConfigDistribution7.class);
        Assert.assertEquals(4, c.foo.raw.length);
        Assert.assertEquals(1, c.foo.raw[0]);
        Assert.assertEquals(2, c.foo.raw[1]);
        Assert.assertEquals(2, c.foo.raw[2]);
        Assert.assertEquals(3, c.foo.raw[3]);
    }

    @Test
    public final void autocompleteOff()
    {
        XltProperties.getInstance().setProperty(KEY + "8.foo", "1/1 2/2 3/1");

        final ConfigDistribution8 c = ConfigurationBuilder.buildDefault(ConfigDistribution8.class);
        Assert.assertEquals(4, c.foo.raw.length);
        Assert.assertEquals(1, c.foo.raw[0]);
        Assert.assertEquals(2, c.foo.raw[1]);
        Assert.assertEquals(2, c.foo.raw[2]);
        Assert.assertEquals(3, c.foo.raw[3]);
    }

}

class ConfigDistribution1
{
    @Property(key = ConfigDistributionTest.KEY + "1", immutable = false)
    public ConfigDistribution foo;
}

class ConfigDistribution1a
{
    @Property(key = ConfigDistributionTest.KEY + "1a")
    public ConfigDistribution foo;
}

class ConfigDistribution2
{
    @Property(key = ConfigDistributionTest.KEY + "2")
    public ConfigDistribution foo;
}

class ConfigDistribution3
{
    @Property(key = ConfigDistributionTest.KEY + "3", required = false)
    public ConfigDistribution foo;
}

class ConfigDistribution4
{
    @Property(key = ConfigDistributionTest.KEY + "4", fallback = "3/1 4/1 5/1")
    public ConfigDistribution foo;
}

class ConfigDistribution4a
{
    @Property(key = ConfigDistributionTest.KEY + "4a", fallback = "30/1 40/1 50/1")
    public ConfigDistribution foo;
}

class ConfigDistribution5
{
    @Property(key = ConfigDistributionTest.KEY + "5", fallback = "")
    public ConfigDistribution foo;
}

class ConfigDistribution6
{
    @Property(key = ConfigDistributionTest.KEY + "6", fallback = "1/1, 2/2, 3/1", delimiters = ",")
    public ConfigDistribution foo;
}

class ConfigDistribution7
{
    @Property(key = ConfigDistributionTest.KEY + "7", fallback = "1/1; 2/2,3/1", delimiters = ",;-")
    public ConfigDistribution foo;
}

class ConfigDistribution8
{
    @Property(key = ConfigDistributionTest.KEY + "8.foo", autocomplete = false)
    public ConfigDistribution foo;
}