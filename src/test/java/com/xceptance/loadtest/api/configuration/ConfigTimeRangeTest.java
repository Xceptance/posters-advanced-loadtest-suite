package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;

public class ConfigTimeRangeTest
{
    public static final String KEY = "ConfigTimeRange";

    private void testInput(final String input, final int expectedMin, final int expectedMax)
    {
        final ConfigTimeRange range = ConfigTimeRange.build(KEY, input);
        Assert.assertEquals(expectedMin, range.min);
        Assert.assertEquals(expectedMax, range.max);
    }

    @Test
    public void rangeParser()
    {
        /*
         * input, expectedMin, expectedMax
         */

        // simple input
        testInput("1-100", 1, 100);
        testInput("1s-1m", 1, 60);
        testInput("1s-1s", 1, 1);

        // complex time
        testInput("1h2m3s-1h2m3s", 3723, 3723);
        testInput("1h 2m 3s - 1h 2m 3s", 3723, 3723);
        testInput("1 h 2 m 3 s - 1 h 2 m 3 s", 3723, 3723);

        // white spaces
        testInput(" 1s-1s", 1, 1);
        testInput("1s-1s ", 1, 1);
        testInput("1s- 1s", 1, 1);
        testInput("1s -1s", 1, 1);
        testInput("1s - 1s", 1, 1);
        testInput("1s - 1s ", 1, 1);
        testInput(" 1s - 1s ", 1, 1);
        testInput("   1s   -   1s  ", 1, 1);

        /*
         * no upper limit
         */
        // start at zero
        testInput("0s-", 0, Integer.MAX_VALUE - 1);
        // start at 1
        testInput("1s-", 1, Integer.MAX_VALUE);
        testInput("1s- ", 1, Integer.MAX_VALUE);
        testInput("1s -", 1, Integer.MAX_VALUE);
        testInput("1s - ", 1, Integer.MAX_VALUE);
        // exceed upper bound (implicitly fall back to supported MAX)
        testInput("10s-" + Integer.MAX_VALUE + "h", 10, Integer.MAX_VALUE);

        // no lower limit
        testInput("-0s", 0, 0);
        testInput("-1s", 0, 1);
        testInput("- 1s", 0, 1);
        testInput(" -1s", 0, 1);
        testInput(" - 1s", 0, 1);

        // null
        testInput(null, 0, 0);
    }

    @Test
    public void testNoDash()
    {
        ConfigTimeRange.build(KEY, "1s");
    }

    @Test
    public void testNoDashNoDigit()
    {
        try
        {
            // no dash, no digit
            ConfigTimeRange.build(KEY, "s");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unknown format of time period"));
        }
    }

    @Test
    public void testTooManyDashes1()
    {
        try
        {
            // too many dashes
            ConfigTimeRange.build(KEY, "-1s-1s");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unknown format of time period"));
        }
    }

    @Test
    public void testTooManyDashes2()
    {
        try
        {
            // too many dashes
            ConfigTimeRange.build(KEY, "1s-1s-");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unknown format of time period"));
        }
    }

    @Test
    public void testTooManyDashes3()
    {
        try
        {
            // too many dashes
            ConfigTimeRange.build(KEY, "-1s-1s-");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unknown format of time period"));
        }
    }

    @Test(expected = NumberFormatException.class)
    public void testNoDigit()
    {
        // no digit
        ConfigTimeRange.build(KEY, "s-m");
    }

    @Test
    public void testDashOnly()
    {
        try
        {
            // dash only
            ConfigTimeRange.build(KEY, "-");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Value is no valid time range pattern:"));
        }
    }

    @Test
    public void testEmpty()
    {
        try
        {
            // empty
            ConfigTimeRange.build(KEY, "");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Value is no valid time range pattern:"));
        }
    }

    @Test
    public void testWhitespaceOnly1()
    {
        try
        {
            // whitespace only
            ConfigTimeRange.build(KEY, " ");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Value is no valid time range pattern:"));
        }
    }

    @Test
    public void testWhitespaceOnly2()
    {
        try
        {
            // whitespace only
            ConfigTimeRange.build(KEY, "  ");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Value is no valid time range pattern:"));
        }
    }

    @Test
    public void testOrder()
    {
        try
        {
            // MIN higher than MAX
            ConfigTimeRange.build(KEY, "10s-1s");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Time range MAX must be greater than or equals to MIN:"));
        }
    }

    @Test
    public void testUnknownTimeFormat()
    {
        try
        {
            // MIN higher than MAX
            ConfigTimeRange.build(KEY, "10x-100y");
            Assert.fail("Expected Exception");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unknown format of time period"));
        }
    }

    @Test
    public final void happyPath_Range()
    {
        XltProperties.getInstance().setProperty(KEY + "1.range", "1-10");

        final ConfigTimeRange1 c = ConfigurationBuilder.buildDefault(ConfigTimeRange1.class);
        Assert.assertEquals(1, c.foo.min);
        Assert.assertEquals(10, c.foo.max);
        Assert.assertTrue(c.foo.value >= 1 && c.foo.value <= 10);
    }

    @Test
    public final void autocomplete_off()
    {
        XltProperties.getInstance().setProperty(KEY + "2.r", "1111-2222");

        final ConfigTimeRange2 c = ConfigurationBuilder.buildDefault(ConfigTimeRange2.class);
        Assert.assertEquals(1111, c.foo.min);
        Assert.assertEquals(2222, c.foo.max);
        Assert.assertTrue(c.foo.value >= 1111 && c.foo.value <= 2222);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutable_true_by_default()
    {
        XltProperties.getInstance().setProperty(KEY + "3.range", "1111-2222");
        final ConfigTimeRange3 c = ConfigurationBuilder.buildDefault(ConfigTimeRange3.class);
        c.foo.random();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutable_true()
    {
        XltProperties.getInstance().setProperty(KEY + "4.range", "1111-2222");
        final ConfigTimeRange4 c = ConfigurationBuilder.buildDefault(ConfigTimeRange4.class);
        c.foo.random();
    }

    public void immutable_false()
    {
        XltProperties.getInstance().setProperty(KEY + "5.range", "1111-2222");
        final ConfigTimeRange5 c = ConfigurationBuilder.buildDefault(ConfigTimeRange5.class);
        final int r = c.foo.random();
        Assert.assertTrue(r <= 2222 && r >= 1111);
    }

    public void fallback()
    {
        final ConfigTimeRange6 c = ConfigurationBuilder.buildDefault(ConfigTimeRange6.class);
        Assert.assertEquals(999, c.foo.min);
        Assert.assertEquals(1000, c.foo.max);
        Assert.assertTrue(c.foo.value >= 999 && c.foo.value <= 1000);
    }

    public void fallback_no_automcomplete()
    {
        final ConfigTimeRange7 c = ConfigurationBuilder.buildDefault(ConfigTimeRange7.class);
        Assert.assertEquals(9999, c.foo.min);
        Assert.assertEquals(10000, c.foo.max);
        Assert.assertTrue(c.foo.value >= 9999 && c.foo.value <= 10000);
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigTimeRange8.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigTimeRange9 c = ConfigurationBuilder.buildDefault(ConfigTimeRange9.class);
        Assert.assertEquals(0, c.foo.min);
        Assert.assertEquals(0, c.foo.max);
        Assert.assertEquals(0, c.foo.value);
    }
}

class ConfigTimeRange1
{
    @Property(key = ConfigTimeRangeTest.KEY + "1")
    public ConfigTimeRange foo;
}

class ConfigTimeRange2
{
    @Property(key = ConfigTimeRangeTest.KEY + "2.r", autocomplete = false)
    public ConfigTimeRange foo;
}

class ConfigTimeRange3
{
    @Property(key = ConfigTimeRangeTest.KEY + "3")
    public ConfigTimeRange foo;
}

class ConfigTimeRange4
{
    @Property(key = ConfigTimeRangeTest.KEY + "4", immutable = true)
    public ConfigTimeRange foo;
}

class ConfigTimeRange5
{
    @Property(key = ConfigTimeRangeTest.KEY + "5", immutable = false)
    public ConfigTimeRange foo;
}

class ConfigTimeRange6
{
    @Property(key = ConfigTimeRangeTest.KEY + "6", fallback = "999-1000")
    public ConfigTimeRange foo;
}

class ConfigTimeRange7
{
    @Property(key = ConfigTimeRangeTest.KEY + "7.foobar", autocomplete = false, fallback = "9999-10000")
    public ConfigTimeRange foo;
}

class ConfigTimeRange8
{
    @Property(key = ConfigTimeRangeTest.KEY + "8", required = true)
    public ConfigTimeRange foo;
}

class ConfigTimeRange9
{
    @Property(key = ConfigTimeRangeTest.KEY + "9", required = false)
    public ConfigTimeRange foo;
}