package com.xceptance.loadtest.posters.actions.catalog;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Executes a paging operation.
 */
public class Paging extends AjaxAction<Paging>
{
	private String categoryType;
	
	private String categoryId;
	
	private String newPage;
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
    	// Extract category type from URL
    	categoryType = RegExUtils.getFirstMatch(Context.getPage().getUrl().toString(), "\\/posters\\/([^/]*)", 1);
    	Assert.assertTrue("Expected category type to be contained in URL", !StringUtils.isBlank(categoryType) && categoryType.toLowerCase().contains("category"));
    
    	// Get paging script
    	String pagingScript = AjaxUtils.getScript("getProductOfTopCategory", Page.find().byId("main").asserted("Failed to find main container").single());
    	Assert.assertTrue("Expected paging script", !StringUtils.isBlank(pagingScript));
    	
    	// Extract categoryId
    	categoryId = RegExUtils.getFirstMatch(pagingScript, "categoryId\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected category id to be contained in paging script", !StringUtils.isBlank(categoryId));
    	
    	// Extract current page
    	String currentPageString = RegExUtils.getFirstMatch(pagingScript, "currentPage\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected current page to be contained in paging script", !StringUtils.isBlank(currentPageString));
    	
    	int currentPage;
    	try
    	{
    		currentPage = Integer.parseInt(currentPageString);
    	}
    	catch(NumberFormatException nfe)
    	{
    		Assert.fail("Failed to interpret current page number");
    		return;
    	}
    	
    	// Extract number of total pages
    	String totalPagesString = RegExUtils.getFirstMatch(pagingScript, "totalPages\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected number of total pages to be contained in paging script", !StringUtils.isBlank(totalPagesString));
    	
    	int totalPages;
    	try
    	{
    		totalPages = Integer.parseInt(totalPagesString);
    	}
    	catch(NumberFormatException nfe)
    	{
    		Assert.fail("Failed to interpret total page number");
    		return;
    	}
    	
    	// Randomly select the new page number ignoring current page number
    	int newPageNumber = XltRandom.nextInt(totalPages);
    	newPage = String.format("%d", newPageNumber >= currentPage ? newPageNumber + 1 : newPageNumber);
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
    	new HttpRequest()
    		.XHR()
    		.POST()
    		.url("/posters/" + resource)
    		.param("categoryId", categoryId)
    		.param("page", newPage)
    		.assertJSONObject("Expected current page and product information to be contained in paging response", true, json -> { return json.has("currentPage") && json.has("products"); })
    		.assertJSONObject("Expected non-empty product information to be contained in paging response", true, json -> json.getJSONArray("products").length() > 0)
    		.fire();
    	
    	// TODO Render or otherwise update the PLP with products from paging response
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
    	// Nothing to validate
    }
}