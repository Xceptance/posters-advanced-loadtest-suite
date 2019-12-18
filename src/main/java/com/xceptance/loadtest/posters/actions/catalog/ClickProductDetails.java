package com.xceptance.loadtest.posters.actions.catalog;

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
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.components.plp.ProductTile;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.posters.util.PageState;

/**
 * Opens a product detail page of a randomly chosen product. 
 */
public class ClickProductDetails extends PageAction<ClickProductDetails>
{
    private String url;

    @Override
    public void precheck()
    {
    	// Get a random product link either directly from the page or from the embedded product data JSON
    	if(PageState.hasProducts())
    	{
    		url = getProductLinkFromState();
    	}
    	else
    	{
    		url = getProductLinkFromExistingTiles();
    	}
    	
    	// Sanity check that one valid URL results
    	Assert.assertTrue("Expected valid product URL", !StringUtils.isBlank(url));
    }
    
    // TODO Decide if this logic or parts of it should move to the component
    
    private String getProductLinkFromState()
    {
    	// Get product information embedded in current page
    	JSONObject productState = PageState.retrieveProducts();
    	Assert.assertTrue("Expected products array in product state", productState.has("products"));
    	
    	JSONArray products = productState.getJSONArray("products");
    	Assert.assertTrue("Expected at least one product in products array", products.length() > 0);
    	
    	// Generate list of product links from product details
    	List<String> productUrls = new ArrayList<>();
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
    			productUrls.add("/posters/productDetail/" + URLEncoder.encode(name, "UTF-8") + "?productId=" + id);
    		}
    		catch(UnsupportedEncodingException uee)
    		{
    			EventLogger.BROWSE.error("Failed to encode URL created from product details found at page", Context.getPage().getUrl().toString());
    			continue;
    		}
    	}

    	// Apply URL filter
    	List<String> discardedUrls = Context.configuration().filterProductUrls.unweightedList().stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
    	productUrls = productUrls.stream().filter(url -> !discardedUrls.stream().anyMatch(s -> url.contains(s))).collect(Collectors.toList());

    	// Verify that at least one URL remains
    	Assert.assertTrue("Expected at least one product with valid URL details", !productUrls.isEmpty());

    	// Randomly select one of the links
    	return RandomUtils.randomEntry(productUrls);
    }

    private String getProductLinkFromExistingTiles()
    {
        // Get product tiles
        final LookUpResult productTilesResult = ProductListingPage.instance.productGrid.getProducts().asserted("No product tiles found");

        // Apply product link filter
        final List<HtmlElement> productTiles = ProductTile.getPDPLinks(productTilesResult)
									                        .discard(Context.configuration().filterProductUrls.unweightedList()
									                                        .stream().filter(s -> StringUtils.isNotBlank(s))
									                                        .collect(Collectors.toList()),
									                                        e -> e.getAttribute("href"))
									                        .asserted("No product link in tiles found").all();

        // Randomly select a tile and get its product URL
        return RandomUtils.randomEntry(productTiles).getAttribute("href");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByUrlClick(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
    	Validator.validatePageSource();
    	
        ProductDetailPage.instance.validate();
    }
}