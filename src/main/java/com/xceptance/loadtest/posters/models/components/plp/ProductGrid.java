package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.posters.util.Context;
import com.xceptance.loadtest.posters.util.PageState;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product grid component.
 *
 * @author Xceptance Software Technologies
 */
public class ProductGrid implements HtmlComponent
{
    public static final ProductGrid instance = new ProductGrid();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("product-overview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public List<String> getFilteredProductUrls()
    {
        // Prepare a list of all discarded URLs
        final List<String> discardedUrls = Context.get().configuration.filterProductUrls.unweightedList().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

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

    private List<String> getProductUrlsFromPageStateAndApplyFilter(final List<String> discardedUrls)
    {
        // Get product information embedded in current page
        final JSONObject productState = PageState.retrieveProducts();
        Assert.assertTrue("Expected products array in product state", productState.has("products"));

        final JSONArray products = productState.getJSONArray("products");
        Assert.assertTrue("Expected at least one product in products array", products.length() > 0);

        // Generate list of product links from product details
        final List<String> productLinks = new ArrayList<>();
        for(int i=0; i<products.length(); i++)
        {
            final JSONObject product = products.getJSONObject(i);
            final String id = product.optString("id");
            final String name = product.optString("name");

            // Make sure id and name are contained in the date because we need it to create the URL
            if(StringUtils.isBlank(id) || StringUtils.isBlank(name))
            {
                EventLogger.BROWSE.error("Invalid product URL details found at page", Context.get().getPage().getUrl().toString());
                continue;
            }

            // Create the URL from given details
            try
            {
                productLinks.add("/en-US/productDetail/" + URLEncoder.encode(name, "UTF-8") + "?productId=" + id);
            }
            catch(final UnsupportedEncodingException uee)
            {
                EventLogger.BROWSE.error("Failed to encode URL created from product details found at page",
                        Context.get().getPage().getUrl().toString());
                continue;
            }
        }

        // Apply filter to product links
        return productLinks.stream().filter(url -> !discardedUrls.stream().anyMatch(s -> url.contains(s))).collect(Collectors.toList());    	
    }

    private List<String> getProductUrlsFromExistingTilesAndApplyFilter(final List<String> discardedUrls)
    {
        // Get all links from product tiles
        final List<HtmlElement> productElements = locate().byCss(".product-tile a[href]").all();

        // Retrieve all URLs and apply the URL filter
        return productElements.stream().map(e -> e.getAttribute("href")).filter(url -> !discardedUrls.stream().anyMatch(filterUrl -> url.contains(filterUrl))).collect(Collectors.toList());
    }
}