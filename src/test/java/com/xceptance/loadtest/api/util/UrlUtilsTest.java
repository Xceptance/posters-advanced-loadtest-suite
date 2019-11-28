package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.Test;

public class UrlUtilsTest
{
    @Test
    public void testURLSpaceEncoding()
    {
        // all in one method, if it is broken, it is broken!
        Assert.assertEquals(
                        "http:///fo.com",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com"));
        Assert.assertEquals(
                        "http:///fo.com?",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com?"));
        Assert.assertEquals(
                        "http:///fo.com?foo=bar",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com?foo=bar"));
        Assert.assertEquals(
                        "http:///fo.com?foo=b%20r",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com?foo=b+r"));
        Assert.assertEquals(
                        "http:///fo.com?foo=b%20r",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com?foo=b%20r"));
        Assert.assertEquals(
                        "http:///fo.com?foo=%20%20r",
                        UrlUtils.convertUrlSpaceEncodingToPercent20("http:///fo.com?foo=++r"));

    }
}
