package com.xceptance.loadtest.posters.actions.catalog;

import com.xceptance.loadtest.api.actions.HtmlUnitAjaxAction;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.posters.util.PageState;

import org.junit.Assert;

/**
 * Executes a paging operation.
 * 
 * @author Xceptance Software Technologies
 */
public class Paging extends HtmlUnitAjaxAction<Paging>
{
    private String categoryType;

    private String categoryId;

    private Integer currentPage;

    private Integer newPage;

    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
        // Extract category type from URL
        categoryType = ProductListingPage.instance.getCategoryType();

        // Extract categoryId from paging script
        categoryId = ProductListingPage.instance.getCategoryId();

        // Get or extract current page
        currentPage = ProductListingPage.instance.pagination.getCurrentPage();

        // Randomly select the new page number, excluding the current page number
        newPage = ProductListingPage.instance.pagination.getNewPageRandomly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Decide request URL depending on current categoryType
        final String resource = "category".equals(categoryType) ? "getProductOfSubCategory" : "getProductOfTopCategory";

        // Send paging request for new products
        final HttpRequest xhr = new HttpRequest()
                .XHR()
                .POST()
                .url("/en-US/" + resource)
                .param("categoryId", categoryId)
                .param("page", String.format("%d", newPage))
                .assertJSONObject("Expected current page and product information to be contained in paging response", true, json -> (json.has("currentPage") && json.has("totalPages")))
                .assertJSONObject("Expected non-empty product information to be contained in paging response", true, json -> (json.has("products") && (json.getJSONArray("products").length() > 0)) )
                .send();

        // Collect product state in the HTML page for later consumption
        PageState.embedProducts(xhr.getResponseContentAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Validate that Paging resulted in the new current page that was requested
        final Integer newCurrentPage = PageState.retrieveProducts().optInt("currentPage");
        Assert.assertTrue("Expected different page after paging operation", (newCurrentPage != null) && (currentPage != newCurrentPage) && (newPage == newCurrentPage));
    }
}