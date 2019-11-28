package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.ConfigRange.Range;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigRangeTest
{
    public static final String KEY = "ConfigRange";

    @Test
    public void rangeParser()
    {
        Assert.assertEquals(1, Range.parse("1-100").min);
        Assert.assertEquals(100, Range.parse("1-100").max);

        Assert.assertEquals(1111, Range.parse("1111-10000").min);
        Assert.assertEquals(10000, Range.parse("1111-10000").max);

        Assert.assertEquals(2, Range.parse(" 2 - 6 ").min);
        Assert.assertEquals(6, Range.parse(" 2 - 6 ").max);

        Assert.assertEquals(2, Range.parse(" 2-6 ").min);
        Assert.assertEquals(6, Range.parse(" 2-6 ").max);

        Assert.assertEquals(2, Range.parse("2 - 6").min);
        Assert.assertEquals(6, Range.parse("2 - 6").max);

        Assert.assertEquals(2, Range.parse("2 -6").min);
        Assert.assertEquals(6, Range.parse("2 -6").max);

        Assert.assertEquals(2, Range.parse("2-  6").min);
        Assert.assertEquals(6, Range.parse("2-  6").max);

        Assert.assertEquals(-2, Range.parse("-2-6").min);
        Assert.assertEquals(6, Range.parse("-2-6").max);

        Assert.assertEquals(-6, Range.parse("-2--6").min);
        Assert.assertEquals(-2, Range.parse("-2--6").max);


        Assert.assertEquals(-6, Range.parse("-6--2").min);
        Assert.assertEquals(-2, Range.parse("-6--2").max);

        Assert.assertEquals(-6, Range.parse("-2- -6").min);
        Assert.assertEquals(-2, Range.parse("-2- -6").max);

        Assert.assertEquals(-6, Range.parse("-2 - -6").min);
        Assert.assertEquals(-2, Range.parse("-2 - -6").max);

        Assert.assertEquals(Integer.MIN_VALUE, Range.parse("-1000").min);
        Assert.assertEquals(1000, Range.parse("-1000").max);

        Assert.assertEquals(Integer.MIN_VALUE, Range.parse("- 1000").min);
        Assert.assertEquals(1000, Range.parse("- 1000").max);

        Assert.assertEquals(Integer.MIN_VALUE, Range.parse("--1000").min);
        Assert.assertEquals(-1000, Range.parse("--1000").max);

        Assert.assertEquals(Integer.MIN_VALUE, Range.parse("- -1000").min);
        Assert.assertEquals(-1000, Range.parse("- -1000").max);

        Assert.assertEquals(1000, Range.parse("1000-").min);
        Assert.assertEquals(Integer.MAX_VALUE, Range.parse("1000-").max);

        Assert.assertEquals(1000, Range.parse("1000 -").min);
        Assert.assertEquals(Integer.MAX_VALUE, Range.parse("1000 -").max);

        Assert.assertEquals(-1000, Range.parse("-1000-").min);
        Assert.assertEquals(Integer.MAX_VALUE, Range.parse("-1000-").max);

        Assert.assertEquals(-1000, Range.parse("-1000 -").min);
        Assert.assertEquals(Integer.MAX_VALUE, Range.parse("-1000 -").max);
    }

    @Test
    public final void insideInt()
    {
        final Range range = Range.parse("1-100");
        Assert.assertTrue(range.inside(50));
        Assert.assertTrue(range.inside(1));
        Assert.assertTrue(range.inside(100));

        Assert.assertFalse(range.inside(0));
        Assert.assertFalse(range.inside(101));

        Assert.assertTrue(range.insideExclusive(50));
        Assert.assertTrue(range.insideExclusive(2));
        Assert.assertTrue(range.insideExclusive(99));

        Assert.assertFalse(range.insideExclusive(1));
        Assert.assertFalse(range.insideExclusive(100));
        Assert.assertFalse(range.insideExclusive(0));
        Assert.assertFalse(range.insideExclusive(101));
    }

    @Test
    public final void insideDouble()
    {
        final Range range = Range.parse("1-100");
        Assert.assertTrue(range.inside(50.12));
        Assert.assertTrue(range.inside(1.0d));
        Assert.assertTrue(range.inside(100.0d));

        Assert.assertFalse(range.inside(0.9999));
        Assert.assertFalse(range.inside(100.0001));

        Assert.assertTrue(range.insideExclusive(50.01));
        Assert.assertTrue(range.insideExclusive(1.01));
        Assert.assertTrue(range.insideExclusive(99.99));

        Assert.assertFalse(range.insideExclusive(1.00d));
        Assert.assertFalse(range.insideExclusive(100.00d));
        Assert.assertFalse(range.insideExclusive(0.999));
        Assert.assertFalse(range.insideExclusive(100.0001));
    }

    @Test
    public final void dontOverlap()
    {
        // test overlapping
        final Range range1 = Range.parse("1-100");
        final Range range2 = Range.parse("500-600");

        Assert.assertFalse(range1.overlap(range2));
        Assert.assertFalse(range2.overlap(range1));
    }

    @Test
    public final void overlapFully()
    {
        // test overlapping
        final Range range1 = Range.parse("1-100");
        final Range range2 = Range.parse("5-10");

        Assert.assertTrue(range1.overlap(range2));
        Assert.assertTrue(range2.overlap(range1));
    }

    @Test
    public final void partialOverlap()
    {
        {
            // test overlapping
            final Range range1 = Range.parse("1-100");
            final Range range2 = Range.parse("90-200");

            Assert.assertTrue(range1.overlap(range2));
            Assert.assertTrue(range2.overlap(range1));
        }

        // test overlapping
        {
            final Range range1 = Range.parse("100-200");
            final Range range2 = Range.parse("90-110");

            Assert.assertTrue(range1.overlap(range2));
            Assert.assertTrue(range2.overlap(range1));
        }
    }

    @Test
    public final void happyPath_Range()
    {
        XltProperties.getInstance().setProperty(KEY + "1.range", "1-10");

        final ConfigRange1 c = ConfigurationBuilder.buildDefault(ConfigRange1.class);
        Assert.assertEquals(1, c.foo.min);
        Assert.assertEquals(10, c.foo.max);
        Assert.assertTrue(c.foo.value >= 1 && c.foo.value <= 10);
    }

    @Test
    public final void autocomplete_off()
    {
        XltProperties.getInstance().setProperty(KEY + "2.r", "1111-2222");

        final ConfigRange2 c = ConfigurationBuilder.buildDefault(ConfigRange2.class);
        Assert.assertEquals(1111, c.foo.min);
        Assert.assertEquals(2222, c.foo.max);
        Assert.assertTrue(c.foo.value >= 1111 && c.foo.value <= 2222);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutable_true_by_default()
    {
        XltProperties.getInstance().setProperty(KEY + "3.range", "1111-2222");
        final ConfigRange3 c = ConfigurationBuilder.buildDefault(ConfigRange3.class);
        c.foo.random();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutable_true()
    {
        XltProperties.getInstance().setProperty(KEY + "4.range", "1111-2222");
        final ConfigRange4 c = ConfigurationBuilder.buildDefault(ConfigRange4.class);
        c.foo.random();
    }

    public void immutable_false()
    {
        XltProperties.getInstance().setProperty(KEY + "5.range", "1111-2222");
        final ConfigRange5 c = ConfigurationBuilder.buildDefault(ConfigRange5.class);
        final int r = c.foo.random();
        Assert.assertTrue(r <= 2222 && r >= 1111);
    }

    public void fallback()
    {
        final ConfigRange6 c = ConfigurationBuilder.buildDefault(ConfigRange6.class);
        Assert.assertEquals(999, c.foo.min);
        Assert.assertEquals(1000, c.foo.max);
        Assert.assertTrue(c.foo.value >= 999 && c.foo.value <= 1000);
    }

    public void fallback_no_automcomplete()
    {
        final ConfigRange7 c = ConfigurationBuilder.buildDefault(ConfigRange7.class);
        Assert.assertEquals(9999, c.foo.min);
        Assert.assertEquals(10000, c.foo.max);
        Assert.assertTrue(c.foo.value >= 9999 && c.foo.value <= 10000);
    }

    @Test
    public final void complainEmpty()
    {
        try
        {
            ConfigurationBuilder.buildDefault(ConfigRange8.class);
        }
        catch (final AssertionError e)
        {
            Assert.assertTrue(e.getMessage().contains("No value provided"));
        }
    }

    @Test
    public final void noComplainEmpty()
    {
        final ConfigRange9 c = ConfigurationBuilder.buildDefault(ConfigRange9.class);
        Assert.assertEquals(0, c.foo.min);
        Assert.assertEquals(0, c.foo.max);
        Assert.assertEquals(0, c.foo.value);
    }
}

class ConfigRange1
{
    @Property(key = ConfigRangeTest.KEY + "1")
    public ConfigRange foo;
}

class ConfigRange2
{
    @Property(key = ConfigRangeTest.KEY + "2.r", autocomplete = false)
    public ConfigRange foo;
}

class ConfigRange3
{
    @Property(key = ConfigRangeTest.KEY + "3")
    public ConfigRange foo;
}

class ConfigRange4
{
    @Property(key = ConfigRangeTest.KEY + "4", immutable = true)
    public ConfigRange foo;
}

class ConfigRange5
{
    @Property(key = ConfigRangeTest.KEY + "5", immutable = false)
    public ConfigRange foo;
}

class ConfigRange6
{
    @Property(key = ConfigRangeTest.KEY + "6", fallback = "999-1000")
    public ConfigRange foo;
}

class ConfigRange7
{
    @Property(key = ConfigRangeTest.KEY + "7.foobar", autocomplete = false, fallback = "9999-10000")
    public ConfigRange foo;
}

class ConfigRange8
{
    @Property(key = ConfigRangeTest.KEY + "8", required = true)
    public ConfigRange foo;
}

class ConfigRange9
{
    @Property(key = ConfigRangeTest.KEY + "9", required = false)
    public ConfigRange foo;
}