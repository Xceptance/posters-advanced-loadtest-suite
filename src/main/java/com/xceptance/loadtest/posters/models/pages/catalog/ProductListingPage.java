package com.xceptance.loadtest.posters.models.pages.catalog;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.posters.models.components.plp.ItemCount;
import com.xceptance.loadtest.posters.models.components.plp.Pagination;
import com.xceptance.loadtest.posters.models.components.plp.ProductGrid;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.util.Context;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

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

        validate(has(productGrid, itemCount));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(productGrid, itemCount));
    }

    public String getCategoryType()
    {
        final String categoryType = RegExUtils.getFirstMatch(Context.get().getPage().getUrl().toString(), "\\/posters\\/([^/]*)", 1);
        Assert.assertTrue("Expected category type to be contained in URL", !StringUtils.isBlank(categoryType) && categoryType.toLowerCase().contains("category"));

        return categoryType;
    }

    public String getCategoryId()
    {
        // Get paging script
        final String pagingScript = AjaxUtils.getScript("getProductOfTopCategory", Page.find().byId("main").asserted("Failed to find main container").single());
        Assert.assertTrue("Expected paging script", !StringUtils.isBlank(pagingScript));

        // Extract categoryId from paging script
        final String categoryId = RegExUtils.getFirstMatch(pagingScript, "categoryId\\:\\s*(\\d+),", 1);
        Assert.assertTrue("Expected category id to be contained in paging script", !StringUtils.isBlank(categoryId));

        return categoryId;
    }
}