package com.xceptance.loadtest.posters.models.pages.catalog;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.models.components.plp.ItemCount;
import com.xceptance.loadtest.posters.models.components.plp.Pagination;
import com.xceptance.loadtest.posters.models.components.plp.ProductGrid;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents a product listing page.
 * 
 * @author Xceptance Software Technologies
 */
public class ProductListingPage extends GeneralPages
{
    public static final ProductListingPage instance = new ProductListingPage();
    
    public final ProductGrid productGrid = ProductGrid.instance;
    
    public final ItemCount itemCount = ItemCount.instance;
    
    public final Pagination pagination = Pagination.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(this.has(productGrid, itemCount));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(productGrid, itemCount));
    }
    
    public String getCategoryType()
    {
    	String categoryType = RegExUtils.getFirstMatch(Context.getPage().getUrl().toString(), "\\/posters\\/([^/]*)", 1);
    	Assert.assertTrue("Expected category type to be contained in URL", !StringUtils.isBlank(categoryType) && categoryType.toLowerCase().contains("category"));
    	
    	return categoryType;
    }
    
    public String getCategoryId()
    {
    	// Get paging script
    	String pagingScript = AjaxUtils.getScript("getProductOfTopCategory", Page.find().byId("main").asserted("Failed to find main container").single());
    	Assert.assertTrue("Expected paging script", !StringUtils.isBlank(pagingScript));
    	
    	// Extract categoryId from paging script
    	String categoryId = RegExUtils.getFirstMatch(pagingScript, "categoryId\\:\\s*(\\d+),", 1);
    	Assert.assertTrue("Expected category id to be contained in paging script", !StringUtils.isBlank(categoryId));
    	
    	return categoryId;
    }
}