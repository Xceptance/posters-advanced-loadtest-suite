package com.xceptance.loadtest.posters.models.pages.search;

import com.xceptance.loadtest.posters.models.components.plp.ProductGrid;
import com.xceptance.loadtest.posters.models.components.plp.ProductSearchResult;
import com.xceptance.loadtest.posters.models.components.plp.ProductSearchResultCount;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

public class SearchResultPage extends GeneralPages
{
    public static final SearchResultPage instance = new SearchResultPage();

    public ProductSearchResult productSearchResult = ProductSearchResult.instance;
    public ProductSearchResultCount productSearchResultCount = ProductSearchResultCount.instance;
    public ProductGrid productGrid = ProductGrid.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(productSearchResult, productSearchResultCount, productGrid));
    }

    @Override
    public boolean is()
    {
        return matches(has(productSearchResult, productSearchResultCount, productGrid));
    }
}
