package com.xceptance.loadtest.posters.models.components.plp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.utils.PageState;

/**
 * Product grid component.
 *
 * @author Xceptance Software Technologies
 */
public class ProductGrid implements Component
{
	public static final ProductGrid instance = new ProductGrid();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("productOverview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public List<String> getFilteredProductUrls()
    {
    	// Prepare a list of all discarded URLs
    	List<String> discardedUrls = Context.configuration().filterProductUrls.unweightedList().stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());

    	// Use either the embedded product state (if available) or existing product tiles
    	if(PageState.hasProducts())
    	{
    		return getProductUrlsFromPageStateAndApplyFilter(discardedUrls);
    	}
    	else
    	{
    		return getProductUrlsFromExistingTilesAndApplyFilter(discardedUrls);
    	}
   	}
    
    private List<String> getProductUrlsFromPageStateAndApplyFilter(List<String> discardedUrls)
    {
    	// Get product information embedded in current page
    	JSONObject productState = PageState.retrieveProducts();
    	Assert.assertTrue("Expected products array in product state", productState.has("products"));
    	
    	JSONArray products = productState.getJSONArray("products");
    	Assert.assertTrue("Expected at least one product in products array", products.length() > 0);
    	
    	// Generate list of product links from product details
    	List<String> productLinks = new ArrayList<>();
    	for(int i=0; i<products.length(); i++)
    	{
    		JSONObject product = products.getJSONObject(i);
    		String id = product.optString("id");
    		String name = product.optString("name");
    		
    		// Make sure id and name are contained in the date because we need it to create the URL
    		if(StringUtils.isBlank(id) || StringUtils.isBlank(name))
    		{
    			EventLogger.BROWSE.error("Invalid product URL details found at page", Context.getPage().getUrl().toString());
    			continue;
    		}
    		
    		// Create the URL from given details
    		try
    		{
    			productLinks.add("/posters/productDetail/" + URLEncoder.encode(name, "UTF-8") + "?productId=" + id);
    		}
    		catch(UnsupportedEncodingException uee)
    		{
    			EventLogger.BROWSE.error("Failed to encode URL created from product details found at page", Context.getPage().getUrl().toString());
    			continue;
    		}
    	}
    	
    	// Apply filter to product links
    	return productLinks.stream().filter(url -> !discardedUrls.stream().anyMatch(s -> url.contains(s))).collect(Collectors.toList());    	
    }
    
    private List<String> getProductUrlsFromExistingTilesAndApplyFilter(List<String> discardedUrls)
    {
    	// Get all product tiles that contain a link
    	List<HtmlElement> productElements = locate().byCss("div[id^='product'] > div.container-fluid > a[href]").all();
    	
    	// Retrieve all URLs and apply the URL filter
    	return productElements.stream().map(e -> e.getAttribute("href")).filter(url -> !discardedUrls.stream().anyMatch(filterUrl -> url.contains(filterUrl))).collect(Collectors.toList());
    }
}