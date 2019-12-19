package com.xceptance.loadtest.posters.actions.catalog;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;

/**
 * Opens a product detail page of a randomly chosen product.
 * 
 * @author Xceptance Software Technologies
 */
public class ClickProductDetails extends PageAction<ClickProductDetails>
{
    private String url;

    @Override
    public void precheck()
    {
    	url = RandomUtils.randomEntry(ProductListingPage.instance.productGrid.getFilteredProductUrls());
    			
    	// Sanity check that there is at least one URL
    	Assert.assertTrue("Expected valid product URL", !StringUtils.isBlank(url));
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