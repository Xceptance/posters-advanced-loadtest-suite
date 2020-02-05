package com.xceptance.loadtest.api.util;

import org.junit.Assert;

import com.xceptance.loadtest.api.events.EventLogger;

/**
 * Utilities for building and handling data types.
 *
 * @author Xceptance Software Technologies
 */
public class DataUtils
{
    // In general this should be enabled. ONLY disabled for unit testing.
    public static boolean useIntCache = true;

    /**
     * LRU Cache for typical numbers
     */
    private final static ConcurrentLRUCache<String, Integer> intCache = new ConcurrentLRUCache<>(201);

    static
    {
        for (int i = 0; i < 50; i++)
        {
            intCache.put(String.valueOf(i), i);
            intCache.put(String.valueOf(i) + ".0", i);
            intCache.put(String.valueOf(i) + ",0", i);
        }
    }

    /**
     * Will remove digit group separator occurrences from the given input string. Assumes that there
     * are no digital places.
     *
     * Only deals with digit group separators ',' and '.'.
     *
     * @param input
     *            The input string.
     * @return The processed string.
     */
    private static String removeDigitGroupSeparators(final String input)
    {
        final String output = input.trim();

        // Replace one occurrence only, otherwise we will generate an incorrect result
        if (output.contains(","))
        {
            return output.replace(",", "");
        }
        else
        {
            return output.replace(".", "");
        }
    }

    /**
     * Removes digit group separator occurrences as well as decimal places from the given input
     * string.
     *
     * Expects the digit group separator character to be either ',' or '.'.
     *
     * @param input
     *            The input string.
     * @param digitGroupSeparator
     *            The character used as digit group separator, either ',' or '.'.
     * @return The processed string.
     */
    private static String removeDigitGroupSeparatorAndDecimalPlaces(final String input, final char digitGroupSeparator)
    {
        // Make sure we only deal with known characters for digit group separation
        Assert.assertTrue("Expected digit group separator to be either ',' or '.'", digitGroupSeparator == ',' || digitGroupSeparator == '.');

        // Which decimal separator do we have (based on provided digit group separator)
        final char decimalSeparator = (digitGroupSeparator == ',') ? '.' : ',';

        // Trim the string, remove any digit group separators and copy the string
        String output = input.trim().replace(String.valueOf(digitGroupSeparator), "");

        // Find potential decimal separator in our string..
        final int decimalSeparatorPosition = output.indexOf(decimalSeparator);
        if (decimalSeparatorPosition > 0)
        {
            // ..and ignore everything including and after the decimal separator (= remove decimal
            // places)
            output = output.substring(0, decimalSeparatorPosition);
        }

        return output;
    }

    /**
     * Converts the given string to an integer.
     *
     * Assumes that the represented number does NOT contain decimal places.
     *
     * Will remove digit group separators (either ',' or '.') from the string.
     *
     * Will throw a number format exception in case the input is not a parsable integer. In case you
     * need a variant that does not throw an exception refer to {@link #toIntSafe(String)}.
     *
     * @param input
     *            The input string.
     * @return The resulting integer value.
     * @throws NumberFormatException
     *             if the string is null or does not contain a parsable integer.
     */
    public static int toInt(final String input)
    {
        if (input == null)
        {
            throw new NumberFormatException("String does not contain parsable integer");
        }

        if (useIntCache)
        {
            // See if we know that already
            final Integer integer = intCache.get(input);
            if (integer != null)
            {
                return integer;
            }
        }

        final Integer result = Integer.parseInt(removeDigitGroupSeparators(input));
        intCache.put(input, result);

        return result;
    }

    /**
     * Converts the given string to an integer.
     *
     * Assumes that the represented number does NOT contain decimal places.
     *
     * Will remove digit group separators (either ',' or '.') from the string.
     *
     * Will NOT throw an exception in case the input is null or is not a parsable integer, but
     * return null instead. In case you need a variant that does throw an exception refer to
     * {@link #toInt(String)}.
     *
     * @param input
     *            The input string.
     * @return The resulting integer value or null in case the string is not parsable.
     */
    public static Integer toIntSafe(final String input)
    {
        if (input != null)
        {
            if (useIntCache)
            {
                // See if we know that already
                final Integer integer = intCache.get(input);
                if (integer != null)
                {
                    return integer;
                }
            }

            // Try to convert the string
            try
            {
                final Integer result = Integer.parseInt(removeDigitGroupSeparators(input));
                intCache.put(input, result);

                return result;
            }
            catch (final NumberFormatException e)
            {
                EventLogger.DEFAULT.warn("NumberFormatException",
                                "Failed to parse integer in given string '" + input + "' or the given string is larger than Integer min/max value.");
            }
        }
        else
        {
            EventLogger.DEFAULT.warn("NumberFormatException", "Failed to parse NULL string to int.");
        }

        return null;
    }

    /**
     * Converts the given string to an integer.
     *
     * Will remove digit group separators as well as decimal places from the string. Expects digit
     * group separator and decimal separator to either be ',' or '.'.
     *
     * Will throw a number format exception in case the input is not a parsable integer. In case you
     * need a variant that does not throw an exception refer to {@link #toIntSafe(String, char)}.
     *
     * @param input
     *            The input string.
     * @return The resulting integer value.
     * @throws NumberFormatException
     *             if the string is null or does not contain a parsable integer.
     */
    public static int toInt(final String input, final char digitGroupSeparator)
    {
        if (input == null)
        {
            throw new NumberFormatException("String does not contain parsable integer");
        }

        if (useIntCache)
        {
            // See if we know that already
            final Integer integer = intCache.get(input);
            if (integer != null)
            {
                return integer;
            }
        }

        final Integer result = Integer.parseInt(removeDigitGroupSeparatorAndDecimalPlaces(input, digitGroupSeparator));
        intCache.put(input, result);

        return result;
    }

    /**
     * Converts the given string to an integer.
     *
     * Will remove digit group separators as well as decimal places from the string. Expects digit
     * group separator and decimal separator to either be ',' or '.'.
     *
     * Will NOT throw a number format exception in case the input is null or is not a parsable
     * integer, but return null instead. In case you need a variant that does throw an exception
     * refer to {@link #toInt(String, char)}.
     *
     * @param input
     *            The input string.
     * @return The resulting integer value or null in case the string is not parsable.
     */
    public static Integer toIntSafe(final String input, final char digitGroupSeparator)
    {
        if (input != null)
        {
            if (useIntCache)
            {
                // See if we know that already
                final Integer integer = intCache.get(input);
                if (integer != null)
                {
                    return integer;
                }
            }

            // Try to remove digit group and decimal places
            String processed;
            try
            {
                processed = removeDigitGroupSeparatorAndDecimalPlaces(input, digitGroupSeparator);
            }
            catch (final AssertionError ae)
            {
                EventLogger.DEFAULT.warn("NumberFormatException",
                                "Failed when processing digit group separator and decimal places on string '" + input + "' and decimal separator '" + digitGroupSeparator + "'.");
                return null;
            }

            // Try to convert the string
            try
            {
                final Integer result = Integer.parseInt(processed);
                intCache.put(input, result);

                return result;
            }
            catch (final NumberFormatException e)
            {
                EventLogger.DEFAULT.warn("NumberFormatException", "Failed to parse integer in given string '" + input + "'.");
            }
        }
        else
        {
            EventLogger.DEFAULT.warn("NumberFormatException", "Failed to parse NULL string to int.");
        }

        return null;
    }
}