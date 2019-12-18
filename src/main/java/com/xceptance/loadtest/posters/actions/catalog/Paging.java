package com.xceptance.loadtest.posters.actions.catalog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.util.PageState;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Executes a paging operation.
 */
public class Paging extends AjaxAction<Paging>
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
    	// TODO Decide if parts of this logic should move to the components
    	
    	// Extract category type from URL
    	categoryType = RegExUtils.getFirstMatch(Context.getPage().getUrl().toString(), "\\/posters\\/([^/]*)", 1);
    	Assert.assertTrue("Expected category type to be contained in URL", !StringUtils.isBlank(categoryType) && categoryType.toLowerCase().contains("category"));
    
    	// Get paging script
    	String pagingScript = AjaxUtils.getScript("getProductOfTopCategory", Page.find().byId("main").asserted("Failed to find main container").single());
    	Assert.assertTrue("Expected paging script", !StringUtils.isBlank(pagingScript));
    	
    	// Extract categoryId from paging script
    	categoryId = RegExUtils.getFirstMatch(pagingScript, "categoryId\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected category id to be contained in paging script", !StringUtils.isBlank(categoryId));
    	
    	// Retrieve products state from current page in case it is available (i.e. we have executed pagination before)
    	JSONObject productsState = PageState.retrieveProducts();
    	
    	// Get or extract current page
    	currentPage = (productsState != null) ? productsState.optInt("currentPage") : extractFromScript(pagingScript, "currentPage", "current page");
    	Assert.assertTrue("Failed to retrieve current page", currentPage != null);
    	
    	// Get or extract total pages
    	Integer totalPages = (productsState != null) ? productsState.optInt("totalPages") : extractFromScript(pagingScript, "totalPages", "total pages");
    	Assert.assertTrue("Failed to retrieve total pages", totalPages != null);
    	
    	// Make sure there is more than one page
    	Assert.assertTrue("Expected more than one page to execute paging", totalPages > 1);   	

    	// Randomly select the new page number, excluding the current page number
    	newPage = XltRandom.nextInt(1, totalPages - 1);
    	newPage = (newPage >= currentPage) ? (newPage + 1) : newPage;
    }
    
    private Integer extractFromScript(String pagingScript, String identifier, String readableName)
    {
    	String currentPageString = RegExUtils.getFirstMatch(pagingScript, identifier + "\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected " + readableName + " to be contained in paging script", !StringUtils.isBlank(currentPageString));
    	
    	try
    	{
    		return Integer.parseInt(currentPageString);
    	}
    	catch(NumberFormatException nfe)
    	{
    		Assert.fail("Failed to interpret " + readableName + " number");
    		return null;
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
    	// Decide request URL depending on current categoryType
    	String resource = categoryType.equals("category") ? "getProductOfSubCategory" : "getProductOfTopCategory";
    	
    	// Send paging request for new products
    	WebResponse response = new HttpRequest()
    		.XHR()
    		.POST()
    		.url("/posters/" + resource)
    		.param("categoryId", categoryId)
    		.param("page", String.format("%d", newPage))
    		.assertJSONObject("Expected current page and product information to be contained in paging response", true, json -> { return json.has("currentPage") && json.has("totalPages"); })
    		.assertJSONObject("Expected non-empty product information to be contained in paging response", true, json -> { return json.has("products") && (json.getJSONArray("products").length() > 0); } )
    		.fire();

    	// Collect product state in the HTML page for later consumption
    	PageState.embedProducts(response.getContentAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
    	// Validate that Paging resulted in the new current page that was requested
    	Integer newCurrentPage = PageState.retrieveProducts().optInt("currentPage");
    	Assert.assertTrue("Expected different page after paging operation", (newCurrentPage != null) && (currentPage != newCurrentPage) && (newPage == newCurrentPage));
    }
}