package com.xceptance.loadtest.posters.actions.crawler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.SimpleURL;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.validators.Validator;

/**
 * Loads a given URL and applies provided validations.
 *
 * @author Xceptance Software Technologies
 */
public class SimpleURL_XPathAsserted extends SimpleURL
{
    private final String xpath;

    private final String text;

    /**
     * Creates an SimpleUrlObject.
     *
     * @param url The URL to load.
     * @param xpath An XPath which will be evaluated after the page was loaded.
     * @param text The expected text content of the element represented by XPath.
     */
    public SimpleURL_XPathAsserted(final String url, final String xpath, final String text)
    {
        super(url);
        this.xpath = xpath;
        this.text = text;
        setTimerName(super.getClass().getSimpleName());
    }

    /**
     * Creates an SimpleUrlObject.
     *
     * @param action The previous action.
     * @param url The URL to load.
     * @param xpath An XPath which will be evaluated after the page was loaded.
     * @param text The expected text content of the element represented by XPath.
     */
    public SimpleURL_XPathAsserted(final com.xceptance.xlt.api.actions.AbstractHtmlPageAction action, final String url, final String xpath,
        final String text)
    {
        super(action, url);
        this.xpath = xpath;
        this.text = text;
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Validate the page load.
        Validator.validatePageSource();

        // Make sure the given XPath validation or text are not empty
        if (StringUtils.isBlank(xpath) || text == null)
        {
            return;
        }

        // Validate the given XPath
        final List<HtmlElement> elements = Page.find().byXPath(xpath).all();
        Assert.assertFalse("xpath not found '" + xpath + "'", elements.isEmpty());
        
        // Validate the given text
        Assert.assertTrue("Text does not match", RegExUtils.isMatching(elements.get(0).asText().trim(), text.trim()));
    }
}