package com.xceptance.loadtest.api.util;

import org.junit.Assert;
import org.junit.Test;

public class AjaxUtilsTest
{
    /*
     *
     *
     * Parse parameters to LIST
     *
     *
     */

    /**
     * Fully qualified URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToList_fullyQualifiedUrl() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("https://url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("https://url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("https://url?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("https://url/one/two/three/four?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, trailing slash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("https://url/one/two/three/four/?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, insecure protocol
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("http://url?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("https://url?foo=&bar=").toString());
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("https://url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("https://url?foo=").toString());
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("https://url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("[foo=a, bar=]", AjaxUtils.parseUrlParamsToList("https://url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("https://url?").toString());

        // no query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("https://url").toString());
    }

    /**
     * Absolute URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToList_absolutePath() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("/url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("/url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("/url?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("/url/one/two/three/four?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path, trailing slash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("/url/one/two/three/four/?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("/url?foo=&bar=").toString());
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("/url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("/url?foo=").toString());
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("/url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("[foo=a, bar=]", AjaxUtils.parseUrlParamsToList("/url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("/url?").toString());

        // no query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("/url").toString());
    }

    /**
     * Relative URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToList_relativePath() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("url/url?foo=bar&x=y").toString());
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("url/url#foo=bar&x=y").toString());
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("url/url?foo=bar&x=y#a=b").toString());
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("url?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("url/url?foo=&bar=").toString());
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("url/url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("url/url?foo=").toString());
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("url/url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("[foo=a, bar=]", AjaxUtils.parseUrlParamsToList("url/url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("url/url?").toString());
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("url?").toString());

        // no query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("url/url").toString());
    }

    /**
     * relative URLs (no protocol, no host)
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToList_queryOnly() throws Exception
    {
        // full query
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("?foo=bar&x=y").toString());

        // full query and hash
        Assert.assertEquals("[foo=bar, x=y]", AjaxUtils.parseUrlParamsToList("?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("?foo=&bar=").toString());
        Assert.assertEquals("[foo=, bar=]", AjaxUtils.parseUrlParamsToList("?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("?foo=").toString());
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("[foo=a, bar=]", AjaxUtils.parseUrlParamsToList("?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("?").toString());
    }

    @Test
    public void testParseUrlParamsToList_duplicateNameAndValue() throws Exception
    {
        // name and value pair is contained twice
        Assert.assertEquals("[foo=bar, foo=bar]", AjaxUtils.parseUrlParamsToList("?foo=bar&foo=bar").toString());
    }

    @Test
    public void testParseUrlParamsToList_duplicateName() throws Exception
    {
        // same key but different value
        Assert.assertEquals("[foo=a, foo=b]", AjaxUtils.parseUrlParamsToList("?foo=a&foo=b").toString());
    }

    @Test
    public void testParseUrlParamsToList_hashOnly() throws Exception
    {
        Assert.assertEquals("[a=b]", AjaxUtils.parseUrlParamsToList("#a=b").toString());
    }

    @Test
    public void testParseUrlParamsToList_null() throws Exception
    {
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList(null).toString());
    }

    @Test
    public void testParseUrlParamsToList_empty() throws Exception
    {
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("").toString());
    }

    @Test
    public void testParseUrlParamsToList_blank() throws Exception
    {
        Assert.assertEquals("[]", AjaxUtils.parseUrlParamsToList("   ").toString());
    }

    @Test
    public void testParseUrlParamsToList_UNCLEAR_handledAsValue() throws Exception
    {
        Assert.assertEquals("[foo=]", AjaxUtils.parseUrlParamsToList("foo").toString());
    }

    /*
     *
     *
     * Parse parameters to MAP
     *
     *
     */

    /**
     * Fully qualified URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToMap_fullyQualifiedUrl() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("https://url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("https://url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("https://url?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("https://url/one/two/three/four?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, trailing slash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("https://url/one/two/three/four/?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, insecure protocol
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("http://url?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("https://url?foo=&bar=").toString());
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("https://url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("https://url?foo=").toString());
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("https://url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("{foo=a, bar=}", AjaxUtils.parseUrlParamsToMap("https://url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("https://url?").toString());

        // no query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("https://url").toString());
    }

    /**
     * Absolute URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToMap_absolutePath() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("/url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("/url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("/url?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("/url/one/two/three/four?foo=bar&x=y#a=b").toString());

        // URL with parameters and hash, long path, trailing slash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("/url/one/two/three/four/?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("/url?foo=&bar=").toString());
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("/url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("/url?foo=").toString());
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("/url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("{foo=a, bar=}", AjaxUtils.parseUrlParamsToMap("/url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("/url?").toString());

        // no query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("/url").toString());
    }

    /**
     * Relative URLs
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToMap_relativePath() throws Exception
    {
        // URL with parameters
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("url/url?foo=bar&x=y").toString());
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("url?foo=bar&x=y").toString());

        // URL with hash (no parameters)
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("url/url#foo=bar&x=y").toString());
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("url#foo=bar&x=y").toString());

        // URL with parameters and hash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("url/url?foo=bar&x=y#a=b").toString());
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("url?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("url/url?foo=&bar=").toString());
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("url/url?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("url/url?foo=").toString());
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("url/url?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("{foo=a, bar=}", AjaxUtils.parseUrlParamsToMap("url/url?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("url/url?").toString());
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("url?").toString());

        // no query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("url/url").toString());
    }

    /**
     * relative URLs (no protocol, no host)
     *
     * @throws Exception
     */
    @Test
    public void testParseUrlParamsToMap_queryOnly() throws Exception
    {
        // full query
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("?foo=bar&x=y").toString());

        // full query and hash
        Assert.assertEquals("{foo=bar, x=y}", AjaxUtils.parseUrlParamsToMap("?foo=bar&x=y#a=b").toString());

        // parameter without value, multiple parameters
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("?foo=&bar=").toString());
        Assert.assertEquals("{foo=, bar=}", AjaxUtils.parseUrlParamsToMap("?foo&bar").toString());
        // parameter without value, single parameters
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("?foo=").toString());
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("?foo").toString());
        // mixed parameters with/without value
        Assert.assertEquals("{foo=a, bar=}", AjaxUtils.parseUrlParamsToMap("?foo=a&bar=").toString());

        // empty query
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("?").toString());
    }

    @Test
    public void testParseUrlParamsToMap_duplicateNameAndValue() throws Exception
    {
        // name and value pair is contained twice
        Assert.assertEquals("{foo=bar}", AjaxUtils.parseUrlParamsToMap("?foo=bar&foo=bar").toString());
    }

    @Test
    public void testParseUrlParamsToMap_duplicateName() throws Exception
    {
        // same key but different value
        Assert.assertEquals("{foo=b}", AjaxUtils.parseUrlParamsToMap("?foo=a&foo=b").toString());
    }

    @Test
    public void testParseUrlParamsToMap_hashOnly() throws Exception
    {
        Assert.assertEquals("{a=b}", AjaxUtils.parseUrlParamsToMap("#a=b").toString());
    }

    @Test
    public void testParseUrlParamsToMap_null() throws Exception
    {
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap(null).toString());
    }

    @Test
    public void testParseUrlParamsToMap_empty() throws Exception
    {
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("").toString());
    }

    @Test
    public void testParseUrlParamsToMap_blank() throws Exception
    {
        Assert.assertEquals("{}", AjaxUtils.parseUrlParamsToMap("   ").toString());
    }

    @Test
    public void testParseUrlParamsToMap_UNCLEAR_handledAsValue() throws Exception
    {
        Assert.assertEquals("{foo=}", AjaxUtils.parseUrlParamsToMap("foo").toString());
    }
}
