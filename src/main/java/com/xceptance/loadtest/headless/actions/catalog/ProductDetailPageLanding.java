package com.xceptance.loadtest.headless.actions.catalog;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.headless.pages.catalog.ProductDetailPage;

/**
 * Opens a product detail page directly without need for browsing the catalog or
 * searching.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class ProductDetailPageLanding extends PageAction<ProductDetailPageLanding>
{
    private final String urlString;

    /**
     * Constructor. Used if product details page is called directly per URL.
     *
     * @param urlString
     *            the URL which should be loaded.
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
        // Set the Basic Authentication header if necessary.
        Context.setBasicAuthenticationHeader();

        loadPage(this.urlString);
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        ProductDetailPage.identify();
    }
}
