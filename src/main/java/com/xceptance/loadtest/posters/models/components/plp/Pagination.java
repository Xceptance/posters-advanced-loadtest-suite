package com.xceptance.loadtest.posters.models.components.plp;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.posters.util.PageState;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Pagination (paging) component.
 * 
 * @author Xceptance Software Technologies
 */
public class Pagination implements Component
{
	public static final Pagination instance = new Pagination();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("pagination-bottom");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public int getCurrentPage()
    {
    	return getCurrentPage(PageState.retrieveProducts(), getPagingScript());
    }
    
    private int getCurrentPage(JSONObject productsJson, String pagingScript)
    {
    	// Get or extract current page
    	Integer currentPage = (productsJson != null) ? productsJson.optInt("currentPage") : extractFromScript(pagingScript, "currentPage", "current page");
    	
    	Assert.assertTrue("Failed to retrieve current page", currentPage != null);

    	return currentPage;
    }
    
    public int getTotalPages()
    {
    	return getTotalPages(PageState.retrieveProducts(), getPagingScript());
    }

    private int getTotalPages(JSONObject productsJson, String pagingScript)
    {
    	// Get or extract total pages    	
    	Integer totalPages = (productsJson != null) ? productsJson.optInt("totalPages") : extractFromScript(pagingScript, "totalPages", "total pages");

    	Assert.assertTrue("Failed to retrieve total pages", totalPages != null);
    	
    	return totalPages;
    }

    public int getNewPageRandomly()
    {
    	// Get paging script
    	String pagingScript = getPagingScript();
    	
    	// Get products JSON
    	JSONObject productsJson = PageState.retrieveProducts();
    	
    	// Get or extract current page
    	int currentPage = getCurrentPage(productsJson, pagingScript);

    	// Get or extract total pages    	
    	int totalPages = getTotalPages(productsJson, pagingScript);
    	
    	// Make sure there is more than one page
    	Assert.assertTrue("Expected more than one page to randomly select new page", totalPages > 1);   	
    	
    	// Randomly select the new page number, excluding the current page number
    	int newPage = XltRandom.nextInt(1, totalPages - 1);
    	newPage = (newPage >= currentPage) ? (newPage + 1) : newPage;

    	return newPage;
    }
    
    private String getPagingScript()
    {
    	String pagingScript = AjaxUtils.getScript("getProductOfTopCategory", Page.find().byId("main").asserted("Failed to find main container").single());

    	Assert.assertTrue("Expected paging script", !StringUtils.isBlank(pagingScript));
    	
    	return pagingScript;
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
}