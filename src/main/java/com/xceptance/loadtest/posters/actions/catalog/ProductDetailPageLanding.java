package com.xceptance.loadtest.posters.actions.catalog;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;

/**
 * Opens a product detail page directly via the provided link without need for browsing the catalog or searching.
 * 
 * @author Xceptance Software Technologies
 */
public class ProductDetailPageLanding extends PageAction<ProductDetailPageLanding>
{
    protected final String urlString;

    /**
     * Creates an action that directly lands on a PDP.
     *
     * @param urlString The URL to load.
     */
    public ProductDetailPageLanding(final String urlString)
    {
        super();
        
        this.urlString = urlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExecute() throws Exception
    {
        Context.setBasicAuthenticationHeader();

        loadPage(urlString);
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();
        
        ProductDetailPage.instance.validate();
    }
}