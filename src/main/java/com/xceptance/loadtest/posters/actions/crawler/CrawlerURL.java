package com.xceptance.loadtest.posters.actions.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.actions.SimpleURL;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.XHTMLValidator;

/**
 * Opens a given URL with some Crawler specific validations. 
 *
 * @author Xceptance Software Technologies
 */
public class CrawlerURL extends SimpleURL
{
    private Map<Predicate<WebResponse>, String> webResponseAssertions;

    private boolean validateBasics = false;

    /**
     * Instantiates the class.
     *
     * @param url The URL which is being loaded
     * @param statusCodePattern The expected status code pattern
     */
    public CrawlerURL(final String url, final String statusCodePattern)
    {
        this(url);

        expectStatusCode(statusCodePattern);
    }

    /**
     * Instantiates the class.
     *
     * @param url The URL which is being loaded
     */
    public CrawlerURL(final String url)
    {
        super(url);
    }

    /**
     * Configure the action to validate content length, HTML end tag, and  XHTML structure.
     * 
     * @return Configured instance
     * @see {@link ContentLengthValidator}
     * @see {@link HtmlEndTagValidator}
     * @see {@link XHTMLValidator}
     */
    public CrawlerURL assertBasics()
    {
        validateBasics = true;
        return this;
    }

    /**
     * Add an assertion on the WebResponse.
     *
     * @param failMessage The message in case of assertion fails
     * @param webResponseAssertion The assertion, given as Java function
     * @return Configured instance
     */
    public CrawlerURL assertWebResponse(final String failMessage, final Predicate<WebResponse> webResponseAssertion)
    {
        if (webResponseAssertions == null)
        {
            webResponseAssertions = new HashMap<>();
        }
        webResponseAssertions.put(webResponseAssertion, failMessage);
        return this;
    }

    @Override
    protected void postValidate() throws Exception
    {
        final HtmlPage page = Context.getPage();

        // validate content length, HTML end tag, and XHTML structure
        if (validateBasics)
        {
            ContentLengthValidator.getInstance().validate(page);
            HtmlEndTagValidator.getInstance().validate(page);
            XHTMLValidator.getInstance().validate(page);
        }

        if (webResponseAssertions != null)
        {
            final WebResponse webResponse = page.getWebResponse();
            webResponseAssertions.forEach((predicate, msg) -> Assert.assertTrue("WebResponse assertion failed: " + msg, predicate.test(webResponse)));
        }
    }
}