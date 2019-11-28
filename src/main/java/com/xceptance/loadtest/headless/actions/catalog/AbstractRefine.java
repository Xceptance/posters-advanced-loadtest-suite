package com.xceptance.loadtest.headless.actions.catalog;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;

/**
 * Abstract base class for refine actions.
 */
public abstract class AbstractRefine<T> extends AjaxAction<T>
{
    /**
     * The element that contains the actual refinement URL.
     */
    protected HtmlElement refinementLink;

    /**
     * Check that we can do this and should do it. You can already collect data here
     * that you save as state, because this won't be called twice by the framework.
     */
    @Override
    public void precheck()
    {
        if (!ProductListingPage.instance.is())
        {
            Assert.fail("Expected to be on product listing page");
        }

        if (!ProductListingPage.instance.productCountSufficientForRefine())
        {
            Assert.fail("Not enough products to execute product listing page action");
        }
    }

    @Override
    protected void doExecute() throws Exception
    {
        final String url = refinementLink.getAttribute("href");

        // Fire XHR call
        final WebResponse response = new HttpRequest().XHR()
                        .url(url)
                        .param("page", ProductListingPage.instance.productGrid.getFooter().asserted("Failed to retrieve product grid footer").single().getAttribute("data-page-number"))
                        .param("selectedUrl", Page.makeFullyQualifiedUrl(url))
                        .assertContent("Nothing came back from HttpRequest.", true, HttpRequest.NOT_BLANK)
                        .assertStatusCode(200)
                        .fire();

        // Map two elements of the response in HTML content into our page
        // Some sites might not have the banner element, in case simply disable the replacement
        Page.mapHtml().html(response.getContentAsString())
                        .byCSS(".search-banner", "html body .page .search-banner")
                        .byCSS(".search-results", "html body .page .search-results")
                        .map();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Validate that we end up on PLP
        ProductListingPage.instance.validate();
    }
}