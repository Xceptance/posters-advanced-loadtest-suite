package com.xceptance.loadtest.posters.models.pages.catalog;

import com.xceptance.loadtest.posters.models.components.plp.PLPItemCount;
import com.xceptance.loadtest.posters.models.components.plp.ProductGrid;
import com.xceptance.loadtest.posters.models.components.plp.SearchResult;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents a product listing page. Expects the contained components to be available.
 */
public class ProductListingPage extends GeneralPages
{
    public static final ProductListingPage instance = new ProductListingPage();

    public final SearchResult searchResult = SearchResult.instance;
    public final ProductGrid productGrid = ProductGrid.instance;
    public final PLPItemCount itemCount = PLPItemCount.instance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate()
    {
        super.validate();

        // Only do the full validation if it is not an empty category page
        if (matches("Make sure we don't have an empty category page", hasNot(searchResult)))
        {
            validate(this.has(productGrid, itemCount));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean is()
    {
        return super.is() && matches(has(productGrid, itemCount));
    }
}