package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.xlt.api.util.XltProperties;

public class DataUtilsTest
{
    /**
     * Create a Context for this Test
     */
    @BeforeClass
    public static void init()
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("com.xceptance.xlt.http.filter.include", "^http://localhost");

        Context.createContext(properties, DataUtilsTest.class.getSimpleName(), DataUtilsTest.class.getName(), SiteSupplier.randomSite().get());

        // Disable the cache
        DataUtils.useIntCache = false;
    }

    @Test
    public void testToIntSafe_simple()
    {
        // Valid input - simple numbers (no separators at all)
        Assert.assertEquals(0, DataUtils.toIntSafe("0").intValue());
        Assert.assertEquals(5, DataUtils.toIntSafe("5").intValue());
        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toIntSafe(Integer.toString(Integer.MAX_VALUE)).intValue());
        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toIntSafe(Integer.toString(Integer.MIN_VALUE)).intValue());
    }

    /** Valid input - digit group separators only, auto detection */
    @Test
    public void testToIntSafe_groupSeparatorAutoDetection()
    {
        // large number
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1,000,000").intValue());
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1.000.000").intValue());

        // odd zero
        Assert.assertEquals(0, DataUtils.toIntSafe("000,000").intValue());
        Assert.assertEquals(0, DataUtils.toIntSafe("000.000").intValue());
    }

    @Test
    public void testToIntSafe_groupSeparatorGiven()
    {
        // no separator in string
        Assert.assertEquals(0, DataUtils.toIntSafe("0", ',').intValue());
        Assert.assertEquals(0, DataUtils.toIntSafe("0", '.').intValue());

        // common large number
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1,000,000", ',').intValue());
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1.000.000", '.').intValue());

        // odd zero
        Assert.assertEquals(0, DataUtils.toIntSafe("000,000", ',').intValue());
        Assert.assertEquals(0, DataUtils.toIntSafe("000.000", '.').intValue());
    }

    @Test
    public void testToIntSafe_groupSeparatorGiven_and_decimalSeparator()
    {
        // small
        Assert.assertEquals(1, DataUtils.toIntSafe("1,99", '.').intValue());
        Assert.assertEquals(1, DataUtils.toIntSafe("1.99", ',').intValue());

        // large
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1.000.000,99", '.').intValue());
        Assert.assertEquals(1_000_000, DataUtils.toIntSafe("1,000,000.99", ',').intValue());

        // zero
        Assert.assertEquals(0, DataUtils.toIntSafe("000.000,99", '.').intValue());
        Assert.assertEquals(0, DataUtils.toIntSafe("000,000.99", ',').intValue());

        // MAX
        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toIntSafe(Integer.MAX_VALUE + ".000", ',').intValue());
        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toIntSafe(Integer.MAX_VALUE + ",000", '.').intValue());

        // MIN
        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toIntSafe(Integer.MIN_VALUE + ",000", '.').intValue());
        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toIntSafe(Integer.MIN_VALUE + ".000", ',').intValue());
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringBlank_groupSeparatorAutoDetection()
    {
        // blank - auto detection
        Assert.assertNull(DataUtils.toIntSafe(null));
        Assert.assertNull(DataUtils.toIntSafe(""));
        Assert.assertNull(DataUtils.toIntSafe(" "));
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringBlank_groupSeparatorGiven()
    {
        // blank - manually given valid group separator
        Assert.assertNull(DataUtils.toIntSafe(null, ','));
        Assert.assertNull(DataUtils.toIntSafe(null, '.'));
        Assert.assertNull(DataUtils.toIntSafe("", ','));
        Assert.assertNull(DataUtils.toIntSafe("", '.'));
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringBlank_groupSeparatorInvalid()
    {
        // blank - invalid group separator
        Assert.assertNull(DataUtils.toIntSafe(null, ' '));
        Assert.assertNull(DataUtils.toIntSafe(null, 'a'));
        Assert.assertNull(DataUtils.toIntSafe(null, '/'));
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringNotANumber_groupSeparatorAutoDetection()
    {
        // NaN - auto detection
        Assert.assertNull(DataUtils.toIntSafe("abc"));
        Assert.assertNull(DataUtils.toIntSafe("1234abc567"));
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringNotANumber_groupSeparatorGiven()
    {
        // NaN - manually given valid group separator
        Assert.assertNull(DataUtils.toIntSafe("abc", ','));
        Assert.assertNull(DataUtils.toIntSafe("abc", '.'));
        Assert.assertNull(DataUtils.toIntSafe("1234abc567", ','));
        Assert.assertNull(DataUtils.toIntSafe("1234abc567", '.'));
    }

    @Test
    public void testToIntSafe_invalidInput_unexpectedNumberOfDecimalPlaces()
    {
        // unexpected number of decimal places
        Assert.assertNull(DataUtils.toIntSafe("1,000.000"));
        Assert.assertNull(DataUtils.toIntSafe("1.000,000"));
    }

    @Test
    public void testToIntSafe_invalidInput_numberStringValid_groupSeparatorInvalid()
    {
        // valid number, invalidGroupSeparator
        Assert.assertNull(DataUtils.toIntSafe("1.000.000,99", ' '));
        Assert.assertNull(DataUtils.toIntSafe("1,000,000.99", 'a'));
        Assert.assertNull(DataUtils.toIntSafe("1,000,000.99", '/'));
    }

    @Test
    public void testToInt_simple()
    {
        Assert.assertEquals(0, DataUtils.toInt("0"));
        Assert.assertEquals(5, DataUtils.toInt("5"));
        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toInt("" + Integer.MAX_VALUE));
        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toInt("" + Integer.MIN_VALUE));
    }

    @Test
    public void testToInt_groupSeparatorAutoDetection()
    {
        Assert.assertEquals(1000000, DataUtils.toInt("1.000.000"));
        Assert.assertEquals(1000000, DataUtils.toInt("1,000,000"));
        Assert.assertEquals(0, DataUtils.toInt("000.000"));
        Assert.assertEquals(0, DataUtils.toInt("000,000"));
    }

    @Test
    public void testToInt_groupSeparatorGiven()
    {
        // Valid input with digit group separator provided
        Assert.assertEquals(0, DataUtils.toInt("0", ','));
        Assert.assertEquals(0, DataUtils.toInt("0", '.'));

        Assert.assertEquals(1000000, DataUtils.toInt("1.000.000", '.'));
        Assert.assertEquals(1000000, DataUtils.toInt("1,000,000", ','));

        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toInt(Integer.MAX_VALUE + ",000", '.'));
        Assert.assertEquals(Integer.MAX_VALUE, DataUtils.toInt(Integer.MAX_VALUE + ".000", ','));

        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toInt(Integer.MIN_VALUE + ",000", '.'));
        Assert.assertEquals(Integer.MIN_VALUE, DataUtils.toInt(Integer.MIN_VALUE + ".000", ','));
    }

    @Test
    public void testToInt_groupSeparatorGiven_and_decimalSeparator()
    {
        // Valid input with digit group separator provided
        Assert.assertEquals(0, DataUtils.toInt("0.99", ','));
        Assert.assertEquals(0, DataUtils.toInt("0,99", '.'));

        Assert.assertEquals(1000000, DataUtils.toInt("1.000.000,99", '.'));
        Assert.assertEquals(1000000, DataUtils.toInt("1,000,000.99", ','));

        Assert.assertEquals(0, DataUtils.toInt("000.000,99", '.'));
        Assert.assertEquals(0, DataUtils.toInt("000,000.99", ','));
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_Null()
    {
        DataUtils.toInt(null);
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_Empty()
    {
        DataUtils.toInt("");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_Spaces()
    {
        DataUtils.toInt(" ");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_onlyNonDigitCharacter()
    {
        DataUtils.toInt("abc");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_containsNonDigitCharacter()
    {
        DataUtils.toInt("1234abc567");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_unexpectedNumberOfDecimalPlaces_1()
    {
        DataUtils.toInt("1,000.000");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_unexpectedNumberOfDecimalPlaces_2()
    {
        DataUtils.toInt("1.000,000");
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_null_digitGroupSeparator_1()
    {
        DataUtils.toInt(null, ',');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_null_DigitGroupSeparator_2()
    {
        DataUtils.toInt(null, '.');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_empty_DigitGroupSeparator_1()
    {
        DataUtils.toInt("", ',');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_empty_DigitGroupSeparator_2()
    {
        DataUtils.toInt("", '.');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_nonDigitCharactersOnly_digitGroupSeparator_1()
    {
        DataUtils.toInt("abc", ',');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_nonDigitCharactersOnly_digitGroupSeparator_2()
    {
        DataUtils.toInt("abc", '.');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_containsDigitCharactersOnly_digitGroupSeparator_1()
    {
        DataUtils.toInt("1234abc567", ',');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_containsDigitCharactersOnly_digitGroupSeparator_2()
    {
        DataUtils.toInt("1234abc567", '.');
    }

    @Test(expected = AssertionError.class)
    public void testToInt_digitGroupSeparator_space()
    {
        DataUtils.toInt("1.000.000,99", ' ');
    }

    @Test(expected = AssertionError.class)
    public void testToInt_digitGroupSeparator_letter()
    {
        DataUtils.toInt("1,000,000.99", 'a');
    }

    @Test(expected = AssertionError.class)
    public void testToInt_digitGroupSeparator_slash()
    {
        DataUtils.toInt("1,000,000.99", '/');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_null_digitGroupSeparator_space()
    {
        DataUtils.toInt(null, ' ');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_null_digitGroupSeparator_letter()
    {
        DataUtils.toInt(null, 'a');
    }

    @Test(expected = NumberFormatException.class)
    public void testToInt_null_digitGroupSeparator_slash()
    {
        DataUtils.toInt(null, '/');
    }
}
