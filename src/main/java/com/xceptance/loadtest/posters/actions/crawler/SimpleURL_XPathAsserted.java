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
 * This is a simple test class for pulling urls.
 *
 * @author Rene Schwietzke
 * @version
 */
public class SimpleURL_XPathAsserted extends SimpleURL
{
    private final String xpath;

    private final String text;

    /**
     * Creates an SimpleUrlObject.
     *
     * @param url
     *            the URL which is beeing loaded
     * @param xpath
     *            an xpath wich will be evaluated after the page was loaded
     * @param text
     *            the expected text content of the element represented by xpath
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
     * @param action
     *            the previous action
     * @param url
     *            the URL which is beeing loaded
     * @param xpath
     *            an xpath wich will be evaluated after the page was loaded
     * @param text
     *            the expected text content of the element represented by xpath
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
        // validate response code
        Validator.validatePageSource();

        // check the special path
        if (StringUtils.isBlank(xpath) || text == null)
        {
            return;
        }

        // ok, do it
        final List<HtmlElement> elements = Page.find().byXPath(xpath).all();
        Assert.assertFalse("xpath not found '" + xpath + "'", elements.isEmpty());

        Assert.assertTrue("Text does not match", RegExUtils.isMatching(elements.get(0).asText().trim(), text.trim()));
    }
}
