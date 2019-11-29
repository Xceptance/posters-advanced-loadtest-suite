package com.xceptance.loadtest.headless.pages.catalog;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.pages.components.plp.GridSort;
import com.xceptance.loadtest.headless.pages.components.plp.PLPItemCount;
import com.xceptance.loadtest.headless.pages.components.plp.ProductGrid;
import com.xceptance.loadtest.headless.pages.components.plp.RefinementBar;
import com.xceptance.loadtest.headless.pages.components.plp.SearchResult;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

public class ProductListingPage extends GeneralPages
{
    public static final ProductListingPage instance = new ProductListingPage();

    public final SearchResult searchResult = SearchResult.instance;
    public final RefinementBar refinementBar = RefinementBar.instance;
    public final ProductGrid productGrid = ProductGrid.instance;
    public final PLPItemCount itemCount = PLPItemCount.instance;
    public final GridSort gridSort = GridSort.instance;

    @Override
    public void validate()
    {
        super.validate();

        // We can have empty categories during refinement right now and to avoid that we fail, we simply check that before we go full validation
        if (matches("Make sure we don't have an empty category page", hasNot(searchResult)))
        {
            // Add grid sort if availability should be enforced
            validate(this.has(productGrid, itemCount/*, gridSort */));
        }
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(productGrid, itemCount/*, gridSort */));
    }

    /**
     * Return true if we have enough products for refinements on the page and
     * not just one or two
     *
     * @return true if sufficient, false otherwise
     */
    public boolean productCountSufficientForRefine()
    {
        return itemCount.getItemCount() >= Context.configuration().refinementMinimumProductCount.value;
    }
}
