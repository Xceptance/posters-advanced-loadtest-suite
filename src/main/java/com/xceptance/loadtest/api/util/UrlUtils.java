package com.xceptance.loadtest.api.util;

import com.xceptance.common.util.RegExUtils;

/**
 * URL helper utilities.
 * 
 * @autor Xceptance Software Technologies
 */
public class UrlUtils
{
    /**
     * Converts URL query parameter space characters (' ' or '+') encoding to '%20'
     *
     * @param uri
     *            The URL with the space parameters to convert
     *
     * @returns The converted URL only containing '%20' encoded space characters in the parameter part
     */
    public static String convertUrlSpaceEncodingToPercent20(final String uri)
    {
        // Find position of parameter start ('?')
        final int pos = uri.indexOf("?");
        if (pos < 0)
        {
            // No parameters, nothing to fix, so we are done
            return uri;
        }

        // URL part before the '?'
        final String path = uri.substring(0, pos);

        // URL query parameter part after the '?' with the resulting string keeping the '?'
        final String query = uri.substring(pos);

        // Replace space characters ' ' or '+' by '%20'
        return path + RegExUtils.replaceAll(query, "\\s|\\+", "%20");
    }

    /**
     * Converts URL query parameter space characters (' ' or '%20') encoding to '+'
     *
     * @param uri
     *            The URL with the space parameters to convert
     *
     * @returns The converted URL only containing '+' encoded space characters in the parameter part
     */
    public static String convertUrlSpaceEncodingToPlus(final String uri)
    {
        // Find position of parameter start ('?')
        final int pos = uri.indexOf("?");
        if (pos < 0)
        {
            // No parameters, nothing to fix, so we are done
            return uri;
        }

        // URL part before the '?'
        final String path = uri.substring(0, pos);

        // URL query parameter part after the '?' with the resulting string keeping the '?'
        final String query = uri.substring(pos);

        // Replace space characters ' ' or '%20' by '+'
        return path + RegExUtils.replaceAll(query, "\\s|%20", "+");
    }
}