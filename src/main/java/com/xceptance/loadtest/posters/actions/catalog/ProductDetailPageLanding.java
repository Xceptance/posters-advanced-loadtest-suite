package com.xceptance.loadtest.posters.actions.catalog;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.validators.Validator;

/**
 * Opens a product detail page directly via the provided link without need for browsing the catalog or searching.
 */
public class ProductDetailPageLanding extends PageAction<ProductDetailPageLanding>
{
    protected final String urlString;

    /**
     * Constructor.
     * 
     * Used if product details page is called directly per URL.
     *
     * @param urlString The URL which should be loaded.
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
        
        // TODO validate PDP
    }
}
