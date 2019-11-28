package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigListTest
{
    public static final String KEY = "ConfigList";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1.list", "1 2");

        final ConfigList1 c = ConfigurationBuilder.buildDefault(ConfigList1.class);
        Assert.assertEquals("1", c.foo.list.get(0));
        Assert.assertEquals("2", c.foo.list.get(1));

        Assert.assertTrue(c.foo.value.equals("1") || c.foo.value.equals("2"));

        // yeah, not really great, but good enough
        final String last = c.foo.random();
        boolean wasRandom = false;
        for (int i = 0; i < 1000; i++)
        {
            if (!last.equals(c.foo.random()))
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
        XltProperties.getInstance().setProperty(KEY + "1.list", "1 2");

        final ConfigList1a c = ConfigurationBuilder.buildDefault(ConfigList1a.class);
        c.foo.random();
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigList2.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigList3 c = ConfigurationBuilder.buildDefault(ConfigList3.class);
        Assert.assertTrue(c.foo.list.isEmpty());
    }

    @Test
    public final void fallback()
    {
        final ConfigList4 c = ConfigurationBuilder.buildDefault(ConfigList4.class);
        Assert.assertEquals("42", c.foo.list.get(0));
        Assert.assertEquals("43", c.foo.list.get(1));
        Assert.assertEquals("44", c.foo.list.get(2));
    }

    @Test
    public final void empty()
    {
        final ConfigList5 c = ConfigurationBuilder.buildDefault(ConfigList5.class);
        Assert.assertTrue(c.foo.list.isEmpty());
    }

    @Test
    public final void oneDifferentDelimiter()
    {
        final ConfigList6 c = ConfigurationBuilder.buildDefault(ConfigList6.class);
        Assert.assertEquals("12", c.foo.list.get(0));
        Assert.assertEquals(" 13", c.foo.list.get(1));
        Assert.assertEquals(" 14", c.foo.list.get(2));
    }

    @Test
    public final void threeDifferentDelimiter()
    {
        final ConfigList7 c = ConfigurationBuilder.buildDefault(ConfigList7.class);
        Assert.assertEquals("12", c.foo.list.get(0));
        Assert.assertEquals("13", c.foo.list.get(1));
        Assert.assertEquals("14", c.foo.list.get(2));
        Assert.assertEquals("15", c.foo.list.get(3));
    }

    @Test
    public final void autocompleteOff()
    {
        XltProperties.getInstance().setProperty(KEY + "8.list", "1 2");
        XltProperties.getInstance().setProperty(KEY + "8", "3 4 5");

        final ConfigList8 c = ConfigurationBuilder.buildDefault(ConfigList8.class);
        Assert.assertEquals("3", c.foo.list.get(0));
        Assert.assertEquals("4", c.foo.list.get(1));
        Assert.assertEquals("5", c.foo.list.get(2));
    }

}

class ConfigList1
{
    @Property(key = ConfigListTest.KEY + "1", immutable = false)
    public ConfigList foo;
}

class ConfigList1a
{
    @Property(key = ConfigListTest.KEY + "1")
    public ConfigList foo;
}

class ConfigList2
{
    @Property(key = ConfigListTest.KEY + "2")
    public ConfigList foo;
}

class ConfigList3
{
    @Property(key = ConfigListTest.KEY + "3", required = false)
    public ConfigList foo;
}

class ConfigList4
{
    @Property(key = ConfigListTest.KEY + "4", fallback = "42  43 44")
    public ConfigList foo;
}

class ConfigList5
{
    @Property(key = ConfigListTest.KEY + "5", fallback = "")
    public ConfigList foo;
}

class ConfigList6
{
    @Property(key = ConfigListTest.KEY + "6", fallback = "12, 13, 14", delimiters = ",")
    public ConfigList foo;
}

class ConfigList7
{
    @Property(key = ConfigListTest.KEY + "7", fallback = "12;13-14,15", delimiters = ",;-")
    public ConfigList foo;
}

class ConfigList8
{
    @Property(key = ConfigListTest.KEY + "8", autocomplete = false)
    public ConfigList foo;
}