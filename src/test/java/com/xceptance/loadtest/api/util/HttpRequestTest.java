package com.xceptance.loadtest.api.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.xlt.api.util.XltProperties;

import util.TestUtils;

public class HttpRequestTest
{
    private static final String TEST_URL = "https://xlt.xceptance.com/test";

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
        properties.setProperty("general.ocapi.clientId", "foobar");
        properties.setProperty("com.xceptance.xlt.http.filter.include", "^http://localhost");

        Context.createContext(properties, HttpRequestTest.class.getSimpleName(), HttpRequestTest.class.getName(), SiteSupplier.randomSite().get());
    }

    /*
     * PARAMS
     */

    @Test
    public void initialParams() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());
        Assert.assertEquals(TEST_URL, httpRequest.getUrl());
    }

    @Test
    public void param() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add a parameter
        httpRequest.param("foo", "a");

        Assert.assertEquals(TEST_URL + "?foo=a", httpRequest.getUrl());
    }

    @Test
    public void param_twice() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add two parameters
        httpRequest.param("foo", "a");
        httpRequest.param("bar", "b");

        Assert.assertEquals(TEST_URL + "?foo=a&bar=b", httpRequest.getUrl());
    }

    @Test
    public void param_sameKey() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add parameter several times but with different values
        httpRequest.param("foo", "a");
        httpRequest.param("foo", "b");
        httpRequest.param("foo", "c");

        Assert.assertEquals(TEST_URL + "?foo=a&foo=b&foo=c", httpRequest.getUrl());
    }

    @Test
    public void params() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // have already a parameter
        httpRequest.param("foo", "a");

        // add more parameters
        final List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("bar", "b"));
        params.add(new NameValuePair("baz", "c"));
        httpRequest.params(params);

        Assert.assertEquals(TEST_URL + "?foo=a&bar=b&baz=c", httpRequest.getUrl());
    }

    @Test
    public void removeParam() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add parameter
        httpRequest.param("foo", "a");

        // remove parameter
        httpRequest.removeParam("foo");

        Assert.assertEquals(TEST_URL, httpRequest.getUrl());
    }

    @Test
    public void removeParam_fromMany() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add 3 different parameters
        httpRequest.param("foo", "a");
        httpRequest.param("bar", "b");
        httpRequest.param("baz", "c");

        // remove one parameter
        httpRequest.removeParam("bar");

        Assert.assertEquals(TEST_URL + "?foo=a&baz=c", httpRequest.getUrl());
    }

    @Test
    public void removeParam_sameKey() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add one parameter three times
        httpRequest.param("foo", "a");
        httpRequest.param("foo", "b");
        httpRequest.param("foo", "c");

        // add a different parameter
        httpRequest.param("bar", "b");

        // remove first parameter
        httpRequest.removeParam("foo");

        Assert.assertEquals(TEST_URL + "?bar=b", httpRequest.getUrl());
    }

    @Test
    public void removeParams() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        // add some parameters
        httpRequest.param("foo", "a");
        httpRequest.param("foo", "b");
        httpRequest.param("foo", "c");

        // remove parameters
        httpRequest.removeParams();

        // no parameter must be left
        Assert.assertEquals(TEST_URL, httpRequest.getUrl());
    }

    @Test
    public void url() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage());

        Assert.assertEquals(TEST_URL, httpRequest.getUrl());
    }

    /*
     * HttpRequest METHOD
     */

    /** Check if the HttpRequest method type is set to the correct initial value. */
    @Test
    public void initialTestMethod()
    {
        assertMethod(new HttpRequest(), HttpMethod.GET);
    }

    /**
     * Set the method type of an HttpRequest object. Check if the correct type was set.
     */
    @Test
    public void testMethod_success()
    {
        // Check all method types
        assertMethod(new HttpRequest().GET(), HttpMethod.GET);
        assertMethod(new HttpRequest().POST(), HttpMethod.POST);
        assertMethod(new HttpRequest().PUT(), HttpMethod.PUT);
        assertMethod(new HttpRequest().DELETE(), HttpMethod.DELETE);
    }

    /**
     * Overwrite method
     */
    @Test
    public void testMethod_overwrite()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Set method, then overwrite it
        httpRequest.GET();
        httpRequest.POST();

        // Method must be POST
        assertMethod(httpRequest, HttpMethod.POST);
    }

    /*
     * Whitespace encoding
     */

    @Test
    public void whitespace_initial_Plus_To_Percent20() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL + "?q=foo+bar", TestUtils.getFakePage());

        // any initial white space encoding ('+') results in '%20' encoding
        final String query = StringUtils.substringAfter(httpRequest.getUrl(), "?");
        Assert.assertEquals(query, "q=foo%20bar");
    }

    @Test
    public void whitespace_initial_Space_To_Percent20() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL + "?q=foo bar", TestUtils.getFakePage());

        // any initial white space encoding (' ') results in '%20' encoding
        final String query = StringUtils.substringAfter(httpRequest.getUrl(), "?");
        Assert.assertEquals(query, "q=foo%20bar");
    }

    @Test
    public void whitespace_initial_Percent20_To_Percent20() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL + "?q=foo%20bar", TestUtils.getFakePage());

        // any initial white space encoding ('%20') results in '%20' encoding
        final String query = StringUtils.substringAfter(httpRequest.getUrl(), "?");
        Assert.assertEquals(query, "q=foo%20bar");
    }

    @Test
    public void whitespace_added_Param_With_Whitespace_To_Percent20() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(TEST_URL + "?q=foo%20bar", TestUtils.getFakePage());
        httpRequest.param("x", "y z");

        // additional parameter's whitespaces (' ') are encoded as '%20' by default
        final String query = StringUtils.substringAfter(httpRequest.getUrl(), "?");
        Assert.assertEquals(query, "q=foo%20bar&x=y%20z");
    }

    /*
     * POST PARAMS
     */

    /** Test the initial amount of post parameters */
    @Test
    public void initialPostParams()
    {
        final HttpRequest httpRequest = new HttpRequest();
        Assert.assertNull(httpRequest.getPostParams());
    }

    /** Add a single post parameter to an HttpRequest object */
    @Test
    public void postParam()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add a post parameter
        httpRequest.postParam("foo", "a");

        // The post parameter has to be in the list exactly once
        Assert.assertEquals("foo", httpRequest.getPostParams().get(0).getName());
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());

        // There must not be any other parameters
        Assert.assertEquals(1, httpRequest.getPostParams().size());
    }

    /** Add two different post parameters */
    public void postParam_twice()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add two parameters
        httpRequest.postParam("foo", "a");
        httpRequest.postParam("bar", "b");

        // Both parameters have to be in the list once
        Assert.assertEquals("foo", httpRequest.getPostParams().get(0).getName());
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());

        Assert.assertEquals("bar", httpRequest.getPostParams().get(1).getName());
        Assert.assertEquals("b", httpRequest.getPostParams().get(1).getValue());

        // There must not be any other parameter
        Assert.assertEquals(2, httpRequest.getPostParams().size());
    }

    /** Add multiple post parameters with the same key */
    @Test
    public void postParam_sameKey()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add parameter several times but with different values
        httpRequest.postParam("foo", "a");
        httpRequest.postParam("foo", "b");
        httpRequest.postParam("foo", "c");

        // The parameter has to be in the list three times
        Assert.assertEquals(3, getKeyCount(httpRequest.getPostParams(), "foo"));

        // All 3 values have to be present
        // (because it's a list the order is preserved)
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());
        Assert.assertEquals("b", httpRequest.getPostParams().get(1).getValue());
        Assert.assertEquals("c", httpRequest.getPostParams().get(2).getValue());

        // There must not be any other parameter
        Assert.assertEquals(3, httpRequest.getPostParams().size());
    }

    /** Add multiple post parameters at once */
    @Test
    public void postParams()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add more parameters
        final List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new NameValuePair("foo", "a"));
        postParams.add(new NameValuePair("bar", "b"));
        httpRequest.postParams(postParams);

        // All three parameters have to be in the list
        Assert.assertEquals("foo", httpRequest.getPostParams().get(0).getName());
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());

        Assert.assertEquals("bar", httpRequest.getPostParams().get(1).getName());
        Assert.assertEquals("b", httpRequest.getPostParams().get(1).getValue());

        // There must not be any other parameters
        Assert.assertEquals(2, httpRequest.getPostParams().size());
    }

    @Test
    public void postParams_combined()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Already have a parameter
        httpRequest.postParam("foo", "a");

        // Add more parameters
        final List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new NameValuePair("bar", "b"));
        postParams.add(new NameValuePair("baz", "c"));
        httpRequest.postParams(postParams);

        // All three parameters have to be in the list
        Assert.assertEquals("foo", httpRequest.getPostParams().get(0).getName());
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());

        Assert.assertEquals("bar", httpRequest.getPostParams().get(1).getName());
        Assert.assertEquals("b", httpRequest.getPostParams().get(1).getValue());

        Assert.assertEquals("baz", httpRequest.getPostParams().get(2).getName());
        Assert.assertEquals("c", httpRequest.getPostParams().get(2).getValue());

        // There must not be any other parameters
        Assert.assertEquals(3, httpRequest.getPostParams().size());
    }

    /** Remove a single post parameter */
    @Test
    public void removePostParam()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add postParameter
        httpRequest.postParam("foo", "a");

        // Remove postParameter
        httpRequest.removePostParam("foo");

        // The postParameter must not be in the list anymore
        Assert.assertEquals(0, getKeyCount(httpRequest.getPostParams(), "foo"));
        // There must not be any other postParameters
        Assert.assertEquals(0, httpRequest.getPostParams().size());
    }

    /** Add multiple post parameters and remove one */
    @Test
    public void removePostParam_many()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add 3 different postParameters
        httpRequest.postParam("foo", "a");
        httpRequest.postParam("bar", "b");
        httpRequest.postParam("baz", "c");

        // Remove one postParameter
        httpRequest.removePostParam("bar");

        // The non-removed post params still have to be in the list
        Assert.assertEquals("foo", httpRequest.getPostParams().get(0).getName());
        Assert.assertEquals("a", httpRequest.getPostParams().get(0).getValue());

        Assert.assertEquals("baz", httpRequest.getPostParams().get(1).getName());
        Assert.assertEquals("c", httpRequest.getPostParams().get(1).getValue());

        // The removed postParameter must not be in the list anymore
        Assert.assertEquals(0, getKeyCount(httpRequest.getPostParams(), "bar"));

        // There must not be any other postParameters
        Assert.assertEquals(2, httpRequest.getPostParams().size());
    }

    /** Add amd remove multiple post parameters with the same key */
    @Test
    public void removePostParam_multiple()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add 3 different postParameters
        httpRequest.postParam("foo", "a");
        httpRequest.postParam("foo", "b");
        httpRequest.postParam("foo", "c");

        // Remove one postParameter
        httpRequest.removePostParam("foo");

        // The removed postParameter must not be in the list anymore
        Assert.assertEquals(0, getKeyCount(httpRequest.getPostParams(), "foo"));
        // The two other postParameters must still be left
        Assert.assertEquals(0, httpRequest.getPostParams().size());
    }

    /** Remove all post parameters */
    @Test
    public void removePostParams()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add some postParameters
        httpRequest.postParam("foo", "a");
        httpRequest.postParam("foo", "b");
        httpRequest.postParam("bar", "c");

        // Remove postParameters
        httpRequest.removePostParams();

        // Post params must be null
        Assert.assertNull(httpRequest.getPostParams());
    }

    /*
     * REQUEST BODY
     */
    @Test
    public void requestBody_initialValue()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Request body must be null initially
        Assert.assertNull(httpRequest.getRequestBody());
    }

    @Test
    public void requestBody()
    {
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestBody("foo");

        Assert.assertEquals("foo", httpRequest.getRequestBody());
    }

    @Test
    public void requestBody_overwrite()
    {
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestBody("foo");
        httpRequest.requestBody("bar");

        Assert.assertEquals("bar", httpRequest.getRequestBody());
        // Post params must be null after setting request body
        Assert.assertEquals(null, httpRequest.getPostParams());
    }

    @Test
    public void removeRequestBody()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Set request body and remove it
        httpRequest.requestBody("foo");
        httpRequest.removeRequestBody();

        // Request body and post params must be null
        Assert.assertEquals(null, httpRequest.getRequestBody());
        Assert.assertEquals(null, httpRequest.getPostParams());
    }

    @Test
    public void removeRequestBody_noneSet()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove request body without setting it
        httpRequest.removeRequestBody();

        // Request body and post params must be null
        Assert.assertEquals(null, httpRequest.getRequestBody());
        Assert.assertEquals(null, httpRequest.getPostParams());
    }

    /*
     * HEADER
     */
    @Test
    public void header_initialValue()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Header list must be empty initially
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());
        // Excluded list must be empty initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void header()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add a single header
        httpRequest.header("foo", "a");

        // Added header must be in the header list
        Assert.assertEquals("a", httpRequest.getAdditionalHeaders().get("foo"));

        // There must not be any other headers in the list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be null initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void header_addMultiple()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add multiple headers
        httpRequest.header("foo", "a");
        httpRequest.header("bar", "b");
        httpRequest.header("baz", "c");

        // Added headers must be in the header list
        Assert.assertEquals("a", httpRequest.getAdditionalHeaders().get("foo"));
        Assert.assertEquals("b", httpRequest.getAdditionalHeaders().get("bar"));
        Assert.assertEquals("c", httpRequest.getAdditionalHeaders().get("baz"));

        // There must not be any other headers in the list
        Assert.assertEquals(3, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be empty initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void header_overwrite()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Set header and overwrite it
        httpRequest.header("foo", "a");
        httpRequest.header("foo", "b");

        // Added header must have the valued set last
        Assert.assertEquals("b", httpRequest.getAdditionalHeaders().get("foo"));

        // There must not be any other headers in the list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be null initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void headers()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Create map of headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("foo", "a");
        headers.put("bar", "b");
        headers.put("baz", "c");

        // Add headers
        httpRequest.headers(headers);

        // Added headers must be in the header list
        Assert.assertEquals("a", httpRequest.getAdditionalHeaders().get("foo"));
        Assert.assertEquals("b", httpRequest.getAdditionalHeaders().get("bar"));
        Assert.assertEquals("c", httpRequest.getAdditionalHeaders().get("baz"));

        // There must not be any other headers in the list
        Assert.assertEquals(3, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be null initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void removeHeader()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove header
        httpRequest.removeHeader("foo");

        // Header list must be null initially
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed header must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));

        // There must not be any other excluded headers in the list
        Assert.assertEquals(1, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void removeHeader_multiple()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove multiple headers
        httpRequest.removeHeader("foo");
        httpRequest.removeHeader("bar");
        httpRequest.removeHeader("baz");

        // Header list must be null initially
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed headers must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("bar"));
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("baz"));

        // There must not be any other excluded headers in the list
        Assert.assertEquals(3, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void removeHeader_twice()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove the same header twice
        httpRequest.removeHeader("foo");
        httpRequest.removeHeader("foo");

        // Header list must be null initially
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed header must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));

        // There must not be any other excluded headers in the list
        // The removed must be in the list once
        Assert.assertEquals(1, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void removeHeader_addAndRemove()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add header
        httpRequest.header("foo", "a");

        // Added header must be in the header list
        Assert.assertEquals("a", httpRequest.getAdditionalHeaders().get("foo"));

        // There must not be any other headers in the list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be null initially
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());

        // Remove header
        httpRequest.removeHeader("foo");

        // Header list must be null again
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed header must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));

        // There must not be any other excluded headers in the list
        Assert.assertEquals(1, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void removeHeader_removeThenAdd()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove header
        httpRequest.removeHeader("foo");

        // Header list must be null
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed header must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));

        // There must not be any other excluded headers in the list
        Assert.assertEquals(1, httpRequest.getHeaderExclusions().size());

        // Add header
        httpRequest.header("foo", "a");

        // Added header must be in the header list
        Assert.assertEquals("a", httpRequest.getAdditionalHeaders().get("foo"));

        // There must not be any other headers in the header list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());

        // Excluded list must be null again
        Assert.assertTrue(httpRequest.getHeaderExclusions().isEmpty());
    }

    @Test
    public void removeHeaders()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Remove headers
        final HashSet<String> headers = new HashSet<>();
        headers.add("foo");
        headers.add("bar");
        httpRequest.removeHeaders(headers);

        // Header list must be null initially
        Assert.assertTrue(httpRequest.getAdditionalHeaders().isEmpty());

        // Removed headers must be in the excluded list
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("bar"));

        // There must not be any other excluded headers in the list
        Assert.assertEquals(2, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void removeHeaders_combineAddAndRemove()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Add foo and bar
        httpRequest.header("foo", "a");
        httpRequest.header("bar", "b");

        // Remove foo and baz
        final HashSet<String> headers = new HashSet<>();
        headers.add("foo");
        headers.add("baz");
        httpRequest.removeHeaders(headers);

        // foo must have been removed
        Assert.assertFalse(httpRequest.getAdditionalHeaders().containsKey("foo"));
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("foo"));

        // bar must still be in the header
        Assert.assertEquals("b", httpRequest.getAdditionalHeaders().get("bar"));
        Assert.assertFalse(httpRequest.getHeaderExclusions().contains("bar"));

        // baz must be on the excluded list
        Assert.assertFalse(httpRequest.getAdditionalHeaders().containsKey("baz"));
        Assert.assertTrue(httpRequest.getHeaderExclusions().contains("baz"));

        // There must be 1 element in the header list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());
        // There must be 2 elements in the excluded list
        Assert.assertEquals(2, httpRequest.getHeaderExclusions().size());
    }

    @Test
    public void headerXHR()
    {
        final HttpRequest httpRequest = new HttpRequest().XHR();

        Assert.assertEquals("XMLHttpRequest", httpRequest.getAdditionalHeaders().get("X-Requested-With"));
        // There must not be any other headers in the list
        Assert.assertEquals(1, httpRequest.getAdditionalHeaders().size());
    }

    /*
     * APPEND TO
     */

    @Test
    public void appendTo()
    {
        // final HttpRequest xhr = new HttpRequest();

        // TODO Requires HTML Element
        // final HtmlElement element = new HtmlElement("foo", null, null);
        // element.getQualifiedName();
        //
        // xhr.appendTo(element);
        //
        // xhr.getUpdateContainer();
    }

    /*
     * REPLACE
     */
    // TODO Requires HTML Element

    /*
     * ASSERT STATUS CODE
     */
    @Test
    public void assertStatusCode_init() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.assertStatusCode(200);

        Assert.assertEquals(200, httpRequest.getExpectedStatusCode());
        // Error message must be set to standard value
        Assert.assertEquals(TestUtils.getFieldValue(httpRequest, "DEFAULT_STATUS_CODE_FAIL_MSG"), httpRequest.getExpectedStatusCodeFailMessage());
    }

    @Test
    public void assertStatusCode_initWithFailMessage()
    {
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.assertStatusCode(200, "foo");

        Assert.assertEquals(200, httpRequest.getExpectedStatusCode());
        Assert.assertEquals("foo", httpRequest.getExpectedStatusCodeFailMessage());
    }

    @Test
    public void assertStatusCode_overwrite()
    {
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.assertStatusCode(200, "foo");
        httpRequest.assertStatusCode(302, "bar");

        Assert.assertEquals(302, httpRequest.getExpectedStatusCode());
        Assert.assertEquals("bar", httpRequest.getExpectedStatusCodeFailMessage());
    }

    @Test
    public void assertStatusCode_success() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("['foo']", 200);

        // Add status code assertion
        final String message = "foobar";
        httpRequest.assertStatusCode(200, message);

        // Process assertions. There should be no Assertion Error
        processAssertions(httpRequest, response);
    }

    @Test
    public void assertStatusCode_fail() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("['foo']", 302);

        // Expected error message
        final String message = "foobar";
        final String expectedMessage = message + " expected:<200> but was:<302>";

        // Add status code assertion
        httpRequest.assertStatusCode(200, message);

        try
        {
            // Process assertions. Assertion error should be thrown
            processAssertions(httpRequest, response);
            // If there was no assertion error, the test should fail
            Assert.fail("Expected assertion error.");
        }
        catch (final InvocationTargetException e)
        {
            // Check if there was an assertion error
            Assert.assertTrue(e.getTargetException() instanceof AssertionError);

            // Check if the assertion error came with the expected error message
            final String errorMessage = e.getTargetException().getMessage();
            Assert.assertEquals(expectedMessage, errorMessage);
        }
    }

    /*
     * ASSERT JSON ARRAY
     */
    @Test
    public void assertJSONArray_initialValue()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // JSON assertions must be null
        Assert.assertNull(httpRequest.getJSONArrayAssertions());
    }

    @Test
    public void assertJSONArray_success() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponce object
        final WebResponse response = mockWebReponse("['foo']", 200);

        // Assertion error message
        final String message = "foobar";

        // Add JSON array assertion
        httpRequest.assertJSONArray(message, "foo", json -> json.get(0));

        // Process assertions. There should not be an Assertion error
        processAssertions(httpRequest, response);
    }

    @Test
    public void assertJSONArray_fail() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("['bar']", 200);

        // Expected error message
        final String message = "foobar";
        final String expectedMessage = message + " expected:<[foo]> but was:<[bar]>";

        // Add JSON array assertion
        httpRequest.assertJSONArray(message, "foo", json -> json.get(0));

        try
        {
            // Process assertions. Assertion error should be thrown
            processAssertions(httpRequest, response);
            // If there was no assertion error, the test should fail
            Assert.fail("Expected assertion error.");
        }
        catch (final InvocationTargetException e)
        {
            // Check if there was an assertion error
            Assert.assertTrue(e.getTargetException() instanceof AssertionError);

            // Check if the assertion error came with the expected error message
            final String errorMessage = e.getTargetException().getMessage();
            Assert.assertEquals(expectedMessage, errorMessage);
        }
    }

    @Test
    public void assertJSONArray_multiple() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("['foo','bar','baz']", 200);

        // Add multiple JSON assertions
        httpRequest.assertJSONArray("foo", json -> json.get(0));
        httpRequest.assertJSONArray("bar", json -> json.get(1));
        httpRequest.assertJSONArray("baz", json -> json.get(2));

        // Process assertions. There should be no exceptions
        processAssertions(httpRequest, response);
    }

    /*
     * ASSERT JSON OBJECT
     */
    @Test
    public void assertJSONObject_initialValue()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // JSON assertions must be null
        Assert.assertNull(httpRequest.getJSONObjectAssertions());
    }

    @Test
    public void assertJSONObject_success() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponce object
        final WebResponse response = mockWebReponse("{'value':'foo'}", 200);

        // Add JSON object assertion
        final String message = "foobar";
        httpRequest.assertJSONObject(message, "foo", json -> json.get("value"));

        // Process assertions. There should not be an Assertion error
        processAssertions(httpRequest, response);
    }

    @Test
    public void assertJSONObject_fail() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("{'value':'bar'}", 200);

        // Expected error message
        final String message = "foobar";
        final String expectedMessage = message + " expected:<[foo]> but was:<[bar]>";

        // Add JSON object assertion
        httpRequest.assertJSONObject(message, "foo", json -> json.get("value"));

        try
        {
            // Process assertions. Assertion error should be thrown
            processAssertions(httpRequest, response);
            // If there was no assertion error, the test should fail
            Assert.fail("Expected assertion error.");
        }
        catch (final InvocationTargetException e)
        {
            // Check if there was an assertion error
            Assert.assertTrue(e.getTargetException() instanceof AssertionError);

            // Check if the assertion error came with the expected error message
            final String errorMessage = e.getTargetException().getMessage();
            Assert.assertEquals(expectedMessage, errorMessage);
        }
    }

    @Test
    public void assertJSONObject_multiple() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("{'value1':'foo','value2':'bar','value3':'baz'}", 200);

        // Add multiple JSON assertions
        httpRequest.assertJSONObject("foo", json -> json.get("value1"));
        httpRequest.assertJSONObject("bar", json -> json.get("value2"));
        httpRequest.assertJSONObject("baz", json -> json.get("value3"));

        // Process assertions. There should be no exceptions
        processAssertions(httpRequest, response);
    }

    /*
     * ASSERT CONTENT
     */
    @Test
    public void assertContent_initialValue()
    {
        final HttpRequest httpRequest = new HttpRequest();

        // JSON assertions must be null
        Assert.assertNull(httpRequest.getContentAssertions());
    }

    @Test
    public void assertContent_success() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponce object
        final WebResponse response = mockWebReponse("foo", 200);

        // Add content assertion
        final String message = "foobar";
        httpRequest.assertContent(message, "foo", content -> content);

        // Process assertions. There should not be an Assertion error
        processAssertions(httpRequest, response);
    }

    @Test
    public void assertContent_fail() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("bar", 200);

        // Expected error message
        final String message = "foobar";
        final String expectedMessage = message + " expected:<[foo]> but was:<[bar]>";

        // Add content assertion
        httpRequest.assertContent(message, "foo", content -> content);

        try
        {
            // Process assertions. Assertion error should be thrown
            processAssertions(httpRequest, response);
            // If there was no assertion error, the test should fail
            Assert.fail("Expected assertion error.");
        }
        catch (final InvocationTargetException e)
        {
            // Check if there was an assertion error
            Assert.assertTrue(e.getTargetException() instanceof AssertionError);

            // Check if the assertion error came with the expected error message
            final String errorMessage = e.getTargetException().getMessage();
            Assert.assertEquals(expectedMessage, errorMessage);
        }
    }

    @Test
    public void assertContent_multiple() throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest();

        // Mock WebResponse object
        final WebResponse response = mockWebReponse("foobarbaz", 200);

        // Add multiple content assertions
        httpRequest.assertContent(true, content -> content.contains("foo"));
        httpRequest.assertContent(true, content -> content.contains("bar"));
        httpRequest.assertContent(true, content -> content.contains("baz"));

        // Process assertions. There should be no exceptions
        processAssertions(httpRequest, response);
    }

    /*
     * IS CACHED
     */

    @Test
    public void isCached()
    {
        // Set isCached to 'true'
        final HttpRequest xhrTrue = new HttpRequest().cached(true);
        Assert.assertTrue(xhrTrue.isCached());

        // Set is Cached to 'false'
        final HttpRequest xhrFalse = new HttpRequest().cached(false);
        Assert.assertFalse(xhrFalse.isCached());
    }

    /**
     * Use URL with trailing question mark as input and expect the URL without the question mark as
     * result.
     */
    @Test
    public void noParametersButTrailingQuestionMark() throws Exception
    {
        final String url = new HttpRequest().XHR().url(TEST_URL, TestUtils.getFakePage()).getUrl();

        Assert.assertEquals("Trailing question mark should be cut off", TEST_URL, url);
    }

    /*
     * HELPER
     */

    /** Get how many times the key was found in the parameter list. */
    private int getKeyCount(final List<NameValuePair> params, final String key)
    {
        int i = 0;
        for (final NameValuePair param : params)
        {
            if (param.getName().equals(key))
            {
                i++;
            }
        }
        return i;
    }

    /**
     * Compare the method type of an HttpRequest object with the expected method type.
     *
     * @param httpRequest
     *            HttpRequest with given method
     * @param expected
     *            Expected method
     */
    private void assertMethod(final HttpRequest httpRequest, final HttpMethod expected)
    {
        Assert.assertEquals(expected, httpRequest.getMethod());
    }

    private WebResponse mockWebReponse(final String responseBody, final int statusCode)
    {
        final WebResponse response = Mockito.mock(WebResponse.class);
        Mockito.when(response.getContentAsString()).thenReturn(responseBody);
        Mockito.when(response.getStatusCode()).thenReturn(statusCode);

        return response;
    }

    private void processAssertions(final HttpRequest httpRequest, final WebResponse response) throws Exception, AssertionError
    {
        // Get method
        final Method declaredMethod = HttpRequest.class.getDeclaredMethod("processAssertions", WebResponse.class);
        declaredMethod.setAccessible(true);

        declaredMethod.invoke(httpRequest, response);
    }
}
