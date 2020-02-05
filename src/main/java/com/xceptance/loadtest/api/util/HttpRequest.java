package com.xceptance.loadtest.api.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.DefaultPageCreator;
import com.gargoylesoftware.htmlunit.DefaultPageCreator.PageType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.validators.Validator;

/**
 * <h1>HttpRequest configuration</h1>
 * <p>
 * This class provides methods to configure and fire an HttpRequest. To verify the result you can
 * specify status code or simple content checks. Furthermore you have the possibility to parse the
 * response content into an HTML element.
 * </p>
 * <p>
 * By default the HttpRequest is build for method GET and checks for a status code 200.
 * </p>
 * Defaults:
 * <table border=1>
 * <tr>
 * <td>Method</td>
 * <td>GET</td>
 * </tr>
 * <tr>
 * <td>Expected Status Code</td>
 * <td>200</td>
 * </tr>
 * </table>
 * 
 * @autor Xceptance Software Technologies
 */
public class HttpRequest
{
    /** Default status code fail message */
    private static final String DEFAULT_STATUS_CODE_FAIL_MSG = "Unexpected HTTP status code.";

    /** Default expected response code: 200 */
    private static final int DEFAULT_EXPECTED_STATUS_CODE = 200;

    /**
     * Update modes
     */
    private enum UpdateMode
    {
        REPLACE_CONTENT, REPLACE_ELEMENT, APPEND, NONE
    }

    /** Basic URI */
    private URIBuilder uriBuilder;

    /** HTTP method */
    private HttpMethod method = HttpMethod.GET;

    /** POST parameters. Mutual exclusive to {@link #requestBody} */
    private List<NameValuePair> postParams;

    /** The request body. Mutual exclusive to {@link #postParams} */
    private String requestBody;

    /** Additional headers */
    private Map<String, String> headers;

    /** Headers to exclude */
    private Set<String> delHeaders;

    /** Expected status code. */
    private int expectedStatusCode = DEFAULT_EXPECTED_STATUS_CODE;

    /** Status code fail message */
    private String expectedStatusCodeFailMessage = DEFAULT_STATUS_CODE_FAIL_MSG;

    /** Desired update mode. {@value UpdateMode#NONE} by default */
    private UpdateMode updateMode = UpdateMode.NONE;

    /** HtmlElement to be updated */
    private HtmlElement container;

    /** Is the request cachable or not? XHRs are not cached by default. */
    private boolean isCached = false;

    /** Response content, saved as member to reduce repeated generation. */
    private String responseBody;

    /** Assertions of String content. */
    private List<XHRResultAssertion<?, String>> contentAssertions;

    /** Assertions of JSONObject, assumes that response content is a JSONObject */
    private List<XHRResultAssertion<?, JSONObject>> jsonObjectAssertions;

    /** Assertions of JSONArray, assumes that response content is a JSONArray */
    private List<XHRResultAssertion<?, JSONArray>> jsonArrayAssertions;

    /**
     * Configures URL parameter space character encoding. If true, spaces are encoded as '%20',
     * otherwise they will be '+'.
     */
    private boolean spacesEncodedAsPercent20 = true;

    private class XHRResultAssertion<E, C>
    {
        private final String failMessage;

        private final E expectedValue;

        private final Function<C, E> function;

        public XHRResultAssertion(final String failMessage, final E expectedValue, final Function<C, E> function)
        {
            this.failMessage = failMessage;
            this.expectedValue = expectedValue;
            this.function = function;
        }

        protected void apply(final C input)
        {
            Assert.assertEquals(failMessage, expectedValue, function.apply(input));
        }
    }

    /**
     * Set the base URL. In case of a relative URL, it will be completed by the information taken from
     * the current page's URL.
     *
     * @param url
     *            a valid URI in string form
     * @return HttpRequest configuration
     * @throws URISyntaxException
     *             if the input is not a valid URI
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     *             if the given URL is null, empty, or consists of whitespaces only.
     */
    public HttpRequest url(final String url) throws URISyntaxException, MalformedURLException
    {
        return url(url, Context.getPage());
    }

    /**
     * Set the base URL
     *
     * @param url
     *            a valid URI in string form
     * @param page
     *            in case of a relative URL, it will be completed by the information taken from the
     *            given page's URL.
     * @return HttpRequest configuration
     * @throws URISyntaxException
     *             if the input is not a valid URI
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     *             if the given URL is null, empty, or consists of whitespaces only.
     */
    public HttpRequest url(final String url, final HtmlPage page) throws URISyntaxException, MalformedURLException
    {
        Args.notBlank(url, "url");
        return url(new URIBuilder(com.gargoylesoftware.htmlunit.util.UrlUtils.encodeUrl(page.getFullyQualifiedUrl(url), false, StandardCharsets.UTF_8).toURI()));
    }

    /**
     * Set the base url
     *
     * @param uriBuilder
     *            an instance of {@link URIBuilder}, with protocol and host set
     * @return HttpRequest configuration
     * @throws IllegalArgumentException
     *             if the given URI Builder is null
     */
    public HttpRequest url(final URIBuilder uriBuilder)
    {
        Args.notNull(uriBuilder, "uriBuilder");

        this.uriBuilder = uriBuilder;
        return this;
    }

    public HttpRequest XHR()
    {
        return header("X-Requested-With", "XMLHttpRequest");
    }

    /**
     * Set request method GET
     *
     * @return HttpRequest configuration
     */
    public HttpRequest GET()
    {
        this.method = HttpMethod.GET;
        return this;
    }

    /**
     * Set request method POST
     *
     * @return HttpRequest configuration
     */
    public HttpRequest POST()
    {
        this.method = HttpMethod.POST;
        return this;
    }

    /**
     * Set request method PUT
     *
     * @return HttpRequest configuration
     */
    public HttpRequest PUT()
    {
        this.method = HttpMethod.PUT;
        return this;
    }

    /**
     * Set request method DELETE
     *
     * @return HttpRequest configuration
     */
    public HttpRequest DELETE()
    {
        this.method = HttpMethod.DELETE;
        return this;
    }

    /**
     * Set request method OPTIONS
     *
     * @return HttpRequest configuration
     */
    public HttpRequest OPTIONS()
    {
        this.method = HttpMethod.OPTIONS;
        return this;
    }

    /**
     * Add an URL parameter
     *
     * @param name
     *            the parameter's name
     * @param value
     *            the parameter's value
     * @return HttpRequest configuration
     */
    public HttpRequest param(final String name, String value)
    {
        Args.notBlank(name, "Parameter name");
        value = valueNullToBlank(value, name);

        uriBuilder.addParameter(name, value);
        return this;
    }

    /**
     * Add URL parameters
     *
     * @param params
     *            the URL parameters given as name-value pairs
     * @return HttpRequest configuration
     */
    public HttpRequest params(final List<NameValuePair> params)
    {
        params.forEach(p -> param(p.getName(), p.getValue()));
        return this;
    }

    /**
     * Removes an URL parameter.
     *
     * @param name
     *            the parameter's name
     * @return HttpRequest configuration
     */
    public HttpRequest removeParam(final String name)
    {
        Args.notBlank(name, "Parameter name");

        // Access parameters and remove parameter with given name
        final List<org.apache.http.NameValuePair> params = uriBuilder.getQueryParams();
        params.removeIf(p -> p.getName().equals(name));

        // Update URI
        uriBuilder.removeQuery();
        if (params.size() > 0)
        {
            uriBuilder.addParameters(params);
        }

        return this;
    }

    /**
     * Replaces existing param or adds it if it does not exist
     *
     * @param param
     *            a param to replace or add
     * @return this modified instance of HttpRequest
     */
    public HttpRequest replaceOrAddParam(final NameValuePair param)
    {
        final List<NameValuePair> params = new ArrayList<>();
        params.add(param);
        return replaceOrAddParams(params);
    }

    /**
     * Replaces all existing parameters or adds it if it does not exist yet
     *
     * @param params
     *            list of params to replace or add
     * @return this modified instance of HttpRequest
     */
    public HttpRequest replaceOrAddParams(final List<NameValuePair> params)
    {
        Args.notNull(params, "Parameters");

        // Access parameters and remove parameter with given name (add it later again)
        final List<org.apache.http.NameValuePair> currentParams = uriBuilder.getQueryParams();
        params.forEach(
                        param ->
                        {
                            currentParams.removeIf(p -> p.getName().equals(param.getName()));
                            currentParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
                        });

        // Update URI
        uriBuilder.removeQuery();
        if (params.size() > 0)
        {
            uriBuilder.addParameters(currentParams);
        }

        return this;
    }

    /**
     * Remove all URL parameters. This effects the URL parameters as well as the parameters of the given
     * URL.
     *
     * @return HttpRequest configuration
     */
    public HttpRequest removeParams()
    {
        uriBuilder.removeQuery();
        return this;
    }

    /**
     * Add POST parameter. This implies that the request body gets dropped.
     *
     * @param name
     *            the POST parameter's name
     * @param value
     *            the parameter's value
     * @return HttpRequest configuration
     */
    public HttpRequest postParam(final String name, String value)
    {
        Args.notBlank(name, "Post parameter name");
        value = valueNullToBlank(value, name);

        if (postParams == null)
        {
            postParams = new ArrayList<>();
        }

        postParams.add(new NameValuePair(name, value));
        requestBody = null;

        return this;
    }

    /**
     * Add POST parameters. This implies that the request body gets dropped.
     *
     * @param postParams
     *            the parameters given as name-value pairs
     * @return HttpRequest configuration
     */
    public HttpRequest postParams(final List<NameValuePair> postParams)
    {
        noNullValue(postParams, "Post parameters");

        postParams.forEach(p -> postParam(p.getName(), p.getValue()));

        return this;
    }

    /**
     * Remove a POST parameter
     *
     * @param name
     *            the parameter's name
     * @return HttpRequest configuration
     */
    public HttpRequest removePostParam(final String name)
    {
        Args.notBlank(name, "Post parameter name");

        if (postParams != null)
        {
            postParams.removeIf(p -> p.getName().equals(name));
        }

        return this;
    }

    /**
     * Remove all POST parameters.
     *
     * @return HttpRequest configuration
     */
    public HttpRequest removePostParams()
    {
        postParams = null;
        requestBody = null;
        return this;
    }

    /**
     * Set the request body. This implies that the POST parameters get dropped.
     *
     * @param requestBody
     *            the request body
     * @return HttpRequest configuration
     */
    public HttpRequest requestBody(final String requestBody)
    {
        this.requestBody = requestBody;
        postParams = null;

        return this;
    }

    /**
     * Remove the request body.
     *
     * @return HttpRequest configuration
     */
    public HttpRequest removeRequestBody()
    {
        removePostParams();
        return this;
    }

    /**
     * Set the expected status code.<br>
     * Response code {@value #DEFAULT_EXPECTED_STATUS_CODE} doesn't need to be configured explicitly
     * (it's the default). <br>
     * To disable status code check set it to <code>0</code> or below.
     *
     * @param expectedStatusCode
     *            expected status code
     * @return
     */
    public HttpRequest assertStatusCode(final int expectedStatusCode)
    {
        return assertStatusCode(expectedStatusCode, DEFAULT_STATUS_CODE_FAIL_MSG);
    }

    /**
     * Set the expected status code and custom assertion fail message. Response code
     * {@value #DEFAULT_EXPECTED_STATUS_CODE} doesn't need to be configured explicitly (it's the
     * default). Default fail message is: {@value #DEFAULT_STATUS_CODE_FAIL_MSG}
     *
     * @param expectedStatusCode
     *            expected status code
     * @param failMessage
     * @return
     */
    public HttpRequest assertStatusCode(final int expectedStatusCode, final String failMessage)
    {
        this.expectedStatusCode = expectedStatusCode;
        this.expectedStatusCodeFailMessage = failMessage;
        return this;
    }

    /**
     * Add additional header
     *
     * @param name
     *            header name
     * @param value
     *            header value
     * @return HttpRequest configuration
     */
    public HttpRequest header(final String name, String value)
    {
        // Check parameters
        Args.notBlank(name, "Header name");
        value = valueNullToBlank(value, name);

        // Update headers
        getAdditionalHeadersInternal().put(name, value);
        getDelHeadersInternal().remove(name);

        return this;
    }

    /**
     * Add additional headers
     *
     * @param additionalHeaders
     *            given as name-value pairs
     * @return HttpRequest configuration
     */
    public HttpRequest headers(final Map<String, String> additionalHeaders)
    {
        // Check map
        Args.notNull(additionalHeaders, "Additional headers");

        final Set<String> keys = additionalHeaders.keySet();

        // Check keys
        Args.notEmpty(keys, "Header names");

        // Update headers
        additionalHeaders.forEach((name, value) -> header(name, value));
        getDelHeadersInternal().removeAll(keys);

        return this;
    }

    /**
     * Exclude specific header from HttpRequest
     *
     * @param headerName
     *            header name
     * @return HttpRequest configuration
     */
    public HttpRequest removeHeader(final String headerName)
    {
        Args.notNull(headerName, "Header name");

        // Update headers
        getAdditionalHeadersInternal().remove(headerName);
        getDelHeadersInternal().add(headerName);

        return this;
    }

    /**
     * Exclude specific headers from HttpRequest
     *
     * @param headerNames
     *            header names
     * @return HttpRequest configuration
     */
    public HttpRequest removeHeaders(final Set<String> headerNames)
    {
        noNullValue(headerNames, "Header names");

        // Update headers
        headerNames.forEach(headerName -> getAdditionalHeadersInternal().remove(headerName));
        getDelHeadersInternal().addAll(headerNames);

        return this;
    }

    /**
     * Appends the response body to the given element.
     *
     * @param parent
     *            container the response body gets parsed into
     * @return HttpRequest configuration
     */
    public HttpRequest appendTo(final HtmlElement parent)
    {
        Args.notNull(parent, "parent");

        updateMode = UpdateMode.APPEND;
        container = parent;

        return this;
    }

    /**
     * Replace the content of the given element with the response body.
     *
     * @param parent
     *            container the response body gets parsed into (previous children will be removed)
     * @return HttpRequest configuration
     */
    public HttpRequest replaceContentOf(final HtmlElement parent)
    {
        Args.notNull(parent, "parent");

        updateMode = UpdateMode.REPLACE_CONTENT;
        container = parent;

        return this;
    }

    /**
     * Replace the given element with the response body.
     *
     * @param element
     *            element to replace
     * @return HttpRequest configuration
     */
    public HttpRequest replace(final HtmlElement element)
    {
        Args.notNull(element, "parent");

        updateMode = UpdateMode.REPLACE_ELEMENT;
        container = element;

        return this;
    }

    /**
     * Expect a JSON array as response content. The method takes a value that is expected, and a
     * function that describes how to get the current value from the response body.
     * <p>
     * * Assuming response content is:<br>
     * <code>["foo"]</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>foo</strong>", json -> json.getString(1))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>xyz</strong>", json -> json.getString(1))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertJSONArray(final E expectedValue, final Function<JSONArray, E> function)
    {
        assertJSONArray(null, expectedValue, function);
        return this;
    }

    /**
     * Expect a JSON array as response content. The method takes a value that is expected, and a
     * function that describes how to get the current value from the response body. If the assertion
     * fails, the given failMessage will be part of the thrown AssertionError.
     * <p>
     * * Assuming response content is:<br>
     * <code>["foo"]</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>foo</strong>", json -> json.getString(1))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>xyz</strong>", json -> json.getString(1))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param failMessage
     *            if assertion fails, then fail with this message
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertJSONArray(final String failMessage, final E expectedValue, final Function<JSONArray, E> function)
    {
        Args.notNull(function, "function");

        if (jsonArrayAssertions == null)
        {
            jsonArrayAssertions = new ArrayList<>();
        }

        jsonArrayAssertions.add(new XHRResultAssertion<>(failMessage, expectedValue, function));
        return this;
    }

    /**
     * Expect a JSON object as response content. The method takes a value that is expected, and a
     * function that describes how to get the current value from the JSON contained in the response
     * body.
     * <p>
     * * Assuming response content is:<br>
     * <code>{"bar":"foo"}</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>foo</strong>", json -> json.getString("bar"))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>xyz</strong>", json -> json.getString("bar"))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertJSONObject(final E expectedValue, final Function<JSONObject, E> function)
    {
        assertJSONObject(null, expectedValue, function);
        return this;
    }

    /**
     * Expect a JSON object as response content. The method takes a value that is expected, and a
     * function that describes how to get the current value from the JSON contained in the response
     * body. If the assertion fails, the given failMessage will be part of the thrown AssertionError.
     * <p>
     * * Assuming response content is:<br>
     * <code>{"bar":"foo"}</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>foo</strong>", json -> json.getString("bar"))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>xyz</strong>", json -> json.getString("bar"))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param failMessage
     *            if assertion fails, then fail with this message
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertJSONObject(final String failMessage, final E expectedValue, final Function<JSONObject, E> function)
    {
        Args.notNull(function, "function");

        if (jsonObjectAssertions == null)
        {
            jsonObjectAssertions = new ArrayList<>();
        }

        jsonObjectAssertions.add(new XHRResultAssertion<>(failMessage, expectedValue, function));
        return this;
    }

    /**
     * Allows to run assertions on the response content string. The method takes a value that is
     * expected, and a function that describes how to get the current value from the response body.
     * <p>
     * * Assuming response content is:<br>
     * <code>12345 bar:foo; 12345</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("<strong>foo</strong>", content -> RegexUtils.getFirstMatch(content, "bar:(.+?);", 1))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("<strong>xyz</strong>", content -> RegexUtils.getFirstMatch(content, "bar:(.+?);", 1))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertContent(final E expectedValue, final Function<String, E> function)
    {
        return assertContent(null, expectedValue, function);
    }

    /**
     * Allows to run assertions on the response content string. The method takes a value that is
     * expected, and a function that describes how to get the current value from the response body. If
     * the assertion fails, the given failMessage will be part of the thrown AssertionError.
     * <p>
     * * Assuming response content is:<br>
     * <code>12345 bar:foo; 12345</code>
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>foo</strong>", content -> RegexUtils.getFirstMatch(content, "bar:(.+?);", 1))</code>
     * <br>
     * succeeds.
     * </p>
     * <p>
     * Example: <br>
     * <code>assertContent("Expected value for 'bar' not found.", "<strong>xyz</strong>", content -> RegexUtils.getFirstMatch(content, "bar:(.+?);", 1))</code>
     * <br>
     * fails with AssertionError.
     * </p>
     *
     * @param failMessage
     *            if assertion fails, then fail with this message
     * @param expectedValue
     *            expected value
     * @param function
     *            lambda function that returns current value for comparison with expected value
     * @return HttpRequest configuration
     */
    public <E> HttpRequest assertContent(final String failMessage, final E expectedValue, final Function<String, E> function)
    {
        Args.notNull(function, "function");

        if (contentAssertions == null)
        {
            contentAssertions = new ArrayList<>();
        }

        contentAssertions.add(new XHRResultAssertion<>(failMessage, expectedValue, function));
        return this;
    }

    /**
     * Set the caching mode. By default HttpRequest responses are not cached.
     *
     * @param isCached
     *            <code>true</code> if the HttpRequest response should be cached
     * @return HttpRequest configuration
     */
    public HttpRequest cached(final boolean isCached)
    {
        this.isCached = isCached;
        return this;
    }

    /**
     * Encodes space characters contained in URL parameters as '%20' character.
     */
    public void encodeUrlParameterSpaceCharactersAsPercent20()
    {
        spacesEncodedAsPercent20 = true;
    }

    /**
     * Encodes space characters contained in URL parameters as '+' character.
     */
    public void encodeUrlParameterSpaceCharactersAsPlus()
    {
        spacesEncodedAsPercent20 = false;
    }

    /**
     * Get the URL string
     *
     * @return URL string
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public String getUrl() throws URISyntaxException, MalformedURLException
    {
        // Normalize query parameters: If the query was not modified it will not be encoded (taken as it
        // is). But if a parameter was added or
        // removed, the query will be encoded always. The following lines normalize the parameter encoding.
        final List<org.apache.http.NameValuePair> queryParams = uriBuilder.getQueryParams();
        if (queryParams.isEmpty())
        {
            // If there's no parameter at all, remove the trailing question mark (if any)
            uriBuilder.removeQuery();
        }
        else
        {
            // Force parameter encoding
            uriBuilder.setParameters(queryParams);
        }

        // Encode spaces the configured way
        if (spacesEncodedAsPercent20)
        {
            return UrlUtils.convertUrlSpaceEncodingToPercent20(uriBuilder.build().toString());
        }
        else
        {
            return UrlUtils.convertUrlSpaceEncodingToPlus(uriBuilder.build().toString());
        }
    }

    /**
     * Get the HTTP method
     *
     * @return HTTP method
     */
    protected HttpMethod getMethod()
    {
        return method;
    }

    /**
     * Get the POST parameters. Please notice that the POST parameters is mutual exclusive to the
     * request body.
     *
     * @return POST parameters
     */
    protected List<NameValuePair> getPostParams()
    {
        return postParams;
    }

    /**
     * Get the request body. Please notice that the request body is mutual exclusive to the POST
     * parameters.
     *
     * @return request body
     */
    protected String getRequestBody()
    {
        return requestBody;
    }

    /**
     * Get the additional headers
     *
     * @return additional headers
     */
    protected Map<String, String> getAdditionalHeaders()
    {
        return getAdditionalHeadersInternal();
    }

    /**
     * Get the header exclusions
     *
     * @return header exclusions
     */
    protected Set<String> getHeaderExclusions()
    {
        return getDelHeadersInternal();
    }

    protected int getExpectedStatusCode()
    {
        return expectedStatusCode;
    }

    protected String getExpectedStatusCodeFailMessage()
    {
        return expectedStatusCodeFailMessage;
    }

    protected HtmlElement getUpdateContainer()
    {
        return container;
    }

    /**
     * Get caching mode.
     *
     * @return <code>true</code> if HttpRequest response will be cached, <code>false</code> otherwise
     */
    protected boolean isCached()
    {
        return isCached;
    }

    protected List<XHRResultAssertion<?, String>> getContentAssertions()
    {
        return contentAssertions;
    }

    protected List<XHRResultAssertion<?, JSONObject>> getJSONObjectAssertions()
    {
        return jsonObjectAssertions;
    }

    /**
     * get
     *
     * @return
     */
    protected List<XHRResultAssertion<?, JSONArray>> getJSONArrayAssertions()
    {
        return jsonArrayAssertions;
    }

    /**
     * Performs an HttpRequest call for the configured HttpRequest based on the current page.
     *
     * @return received response
     * @throws URISyntaxException
     * @throws IOException
     *             if an IO problem occurs
     * @throws SAXException
     *             if an SAX problem occurs
     * @throws MalformedURLException
     *             if an error occurred when creating a URL object
     */
    public WebResponse fire() throws Exception
    {
        return fireFrom(Context.getPage());
    }

    /**
     * Performs an HttpRequest call for the configured HttpRequest based on given page.
     *
     * @param page
     *            the current page
     * @return received response
     * @throws URISyntaxException
     * @throws IOException
     *             if an IO problem occurs
     * @throws SAXException
     *             if an SAX problem occurs
     * @throws MalformedURLException
     *             if an error occurred when creating a URL object
     */
    public WebResponse fireFrom(final HtmlPage page) throws URISyntaxException, IOException, SAXException, MalformedURLException
    {
        // Build the request
        final WebRequest request = buildRequest(page);

        // Perform the call
        final WebResponse response = page.getWebClient().loadWebResponse(request);

        // Process assertions
        processAssertions(response);

        // Update the page
        updatePage(response);

        return response;
    }

    private WebRequest buildRequest(final HtmlPage page) throws MalformedURLException, URISyntaxException
    {
        // Often an URL string is (relative or absolute) not full qualified (for example '/foo/bar.html').
        // So a full qualified URL is build first.
        final URL pageURL = page.getFullyQualifiedUrl(getUrl());

        // Create the basic request
        final HttpMethod method = getMethod();
        final WebRequest request = new WebRequest(pageURL, method);

        // Set charset
        final Charset charset = getContentCharset(page);
        request.setCharset(charset);

        // Add either POST parameters or request body
        if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method))
        {
            final String requestBody = getRequestBody();
            if (requestBody != null)
            {
                request.setRequestBody(requestBody);
            }
            else
            {
                final List<NameValuePair> parameters = getPostParams();
                if (parameters != null)
                {
                    request.setRequestParameters(parameters);
                }
            }
        }
        else
        {
            Assert.assertTrue("Post parameter(s) or request body is allowed for POST|PUT|PATCH requests only. Current method is " + method,
                            getRequestBody() == null && (getPostParams() == null || getPostParams().isEmpty()));
        }

        // Cache
        if (!isCached())
        {
            // If HttpRequest caching is disabled (DEFAULT) set the internal HttpRequest flag.
            request.setXHR();
        }

        // Compression
        if (Context.get().configuration.applyHeaderGzip)
        {
            request.getAdditionalHeaders().put("Accept-Encoding", "gzip, deflate");
        }

        // Add standard HttpRequest headers
        request.getAdditionalHeaders().put("Referer", page.getUrl().toExternalForm());

        // Add additional configured headers
        final Map<String, String> aditionalHeaders = getAdditionalHeaders();
        if (aditionalHeaders != null)
        {
            request.getAdditionalHeaders().putAll(aditionalHeaders);
        }

        // Remove unwanted headers
        final Set<String> headerExclusions = getHeaderExclusions();
        if (headerExclusions != null)
        {
            headerExclusions.forEach(headerName -> request.getAdditionalHeaders().remove(headerName));
        }

        return request;
    }

    /**
     * Applies the given assertions.
     *
     * @param response
     *            the HttpRequest's response
     */
    protected void processAssertions(final WebResponse response)
    {
        // Status code
        if (getExpectedStatusCode() > 0)
        {
            Assert.assertEquals(getExpectedStatusCodeFailMessage(), getExpectedStatusCode(), response.getStatusCode());
        }

        // Content
        if (getContentAssertions() != null)
        {
            getContentAssertions().forEach(assertion -> assertion.apply(getResponseBody(response)));
        }

        // Json object
        if (getJSONObjectAssertions() != null && !getJSONObjectAssertions().isEmpty())
        {
            final JSONObject json = buildJSONObject(getResponseBody(response));
            getJSONObjectAssertions().forEach(assertion -> assertion.apply(json));
        }

        // Json array
        if (getJSONArrayAssertions() != null && !getJSONArrayAssertions().isEmpty())
        {
            final JSONArray json = buildJSONArray(getResponseBody(response));
            getJSONArrayAssertions().forEach(assertion -> assertion.apply(json));
        }
    }

    /**
     * Builds a JSON object from given string.
     *
     * @param responseContent
     *            string representation of a JSON object
     * @return JSON object
     * @throws AssertionError
     *             if response cannot get parsed to a JSON object
     */
    private JSONObject buildJSONObject(final String responseContent) throws AssertionError
    {
        try
        {
            return new JSONObject(responseContent);
        }
        catch (final JSONException e)
        {
            fail(responseContent, "object");
        }

        throw new IllegalStateException("If we get here we have a problem!");
    }

    /**
     * Builds a JSON array from given string.
     *
     * @param responseContent
     *            string representation of a JSON array
     * @return JSON array
     * @throws AssertionError
     *             if response cannot get parsed to a JSON array
     */
    private JSONArray buildJSONArray(final String responseContent) throws AssertionError
    {
        try
        {
            return new JSONArray(responseContent);
        }
        catch (final JSONException e)
        {
            fail(responseContent, "array");
        }

        throw new IllegalStateException("If we get here we have a problem!");
    }

    /**
     * Tries to find out why parsing the given string to JSON of given type failed and throw an
     * assertion error with appropriate message.
     *
     * @param responseContent
     *            string to evaluate
     * @param jsonType
     *            expected JSON type
     * @throws AssertionError
     */
    private void fail(final String responseContent, final String jsonType) throws AssertionError
    {
        Assert.assertFalse("Response is empty.", RegExUtils.replaceAll(responseContent, "\\s+", "").isEmpty());
        Assert.fail("The response doesn't contain a JSON " + jsonType + ".");
    }

    /**
     * Update the current page with response content if desired.
     *
     * @param response
     * @throws SAXException
     *             if response cannot get parsed as HTML
     * @throws IOException
     *             if response cannot get parsed as HTML
     */
    private void updatePage(final WebResponse response) throws SAXException, IOException
    {
        // Update page if necessary
        if (!UpdateMode.NONE.equals(updateMode))
        {
            if (UpdateMode.REPLACE_CONTENT.equals(updateMode))
            {
                DOMUtils.replaceContent(container, getResponseBody(response));
            }
            else if (UpdateMode.REPLACE_ELEMENT.equals(updateMode))
            {
                DOMUtils.replaceElement(container, getResponseBody(response));
            }
            else if (UpdateMode.APPEND.equals(updateMode))
            {
                // Parse the new content into the container
                DOMUtils.appendElement(container, getResponseBody(response));
            }
        }
    }

    /**
     * Returns the (cached) response Body, so it has to be created only once.
     *
     * @param response
     *            the HttpRequest's response
     * @return response body
     */
    protected String getResponseBody(final WebResponse response)
    {
        if (responseBody == null)
        {
            responseBody = response.getContentAsString();

            // if we are not able to retrieve a body content, we might want to check what's going on
            if (responseBody == null)
            {
                Validator.dumpResponseContentAndFail(response, "Bad XHR response");
            }
        }
        return responseBody;
    }

    /**
     * Determine content charset of given page.
     *
     * @param page
     * @return
     */
    private static Charset getContentCharset(final HtmlPage page)
    {
        final WebResponse r = page.getWebResponse();
        Charset charset = r.getContentCharsetOrNull();

        // No content charset given explicitly? Decide based on content type.
        if (charset == null)
        {
            // In case content type is XML, we can use charset UTF-8
            final String contentType = r.getContentType();
            if (null != contentType && PageType.XML == DefaultPageCreator.determinePageType(contentType))
            {
                charset = StandardCharsets.UTF_8;
            }
            else
            {
                // It's not XML, so let's use the request's charset.
                charset = r.getWebRequest().getCharset();

                // If this is also not given, use UTF-8 as default.
                if (charset == null)
                {
                    charset = StandardCharsets.UTF_8;
                }
            }
        }

        return charset;
    }

    /**
     * Prepares a form submission HttpRequest based on the given forms data (action attribute, input and
     * select elements)
     *
     * @param form
     *            the form to submit
     * @return the pre-configured HttpRequest element
     */
    public static HttpRequest submitForm(final HtmlForm form) throws Exception
    {
        final HttpRequest httpRequest = new HttpRequest().XHR().url(form.getActionAttribute());
        final List<NameValuePair> params = AjaxUtils.serialize(form);

        final String method = form.getMethodAttribute();
        if (HttpMethod.POST.toString().equalsIgnoreCase(method))
        {
            httpRequest.POST().postParams(params);
        }
        else if (HttpMethod.GET.toString().equalsIgnoreCase(method))
        {
            httpRequest.GET().params(params);
        }
        else
        {
            throw new UnsupportedOperationException("Form method " + method.toString() + " not supported.");
        }

        return httpRequest;
    }

    private Map<String, String> getAdditionalHeadersInternal()
    {
        if (headers == null)
        {
            headers = new HashMap<>();
        }

        return headers;
    }

    private Set<String> getDelHeadersInternal()
    {
        if (delHeaders == null)
        {
            delHeaders = new HashSet<>();
        }

        return delHeaders;
    }

    /**
     * Returns non-null value. In case the given value is <code>null</code> it is converted to an empty
     * String.
     *
     * @param value
     *            value to check
     * @param key
     *            corresponding key
     * @return non-null value
     */
    private static String valueNullToBlank(final String value, final String key)
    {
        if (value == null)
        {
            EventLogger.DEFAULT.debug("Parameter value 'null' was converted into empty string for key", key);
            return StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * Checks if collection is <code>null</code> or a collection element is <code>null</code>.
     *
     * @param collection
     *            collection to check
     * @param parameterName
     *            name of the collection
     * @throws IllegalArgumentException
     *             if collection is <code>null</code> or a collection element is <code>null</code>
     */
    private <T> void noNullValue(final Collection<T> collection, final String parameterName)
    {
        Args.notEmpty(collection, parameterName);

        for (final T t : collection)
        {
            if (t == null)
            {
                throw new IllegalArgumentException("'" + parameterName + "' must not contain null value.");
            }
        }
    }

    /**
     * Checks if a given string is not blank.<br>
     * There's no null-check included!
     */
    public static Function<String, Boolean> NOT_BLANK = s -> s.trim().length() > 0;
}