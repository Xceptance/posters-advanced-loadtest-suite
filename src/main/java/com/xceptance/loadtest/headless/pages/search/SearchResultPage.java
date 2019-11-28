package com.xceptance.loadtest.headless.pages.search;

import com.xceptance.loadtest.headless.pages.components.plp.ProductGrid;
import com.xceptance.loadtest.headless.pages.components.plp.ProductSearchResult;
import com.xceptance.loadtest.headless.pages.components.plp.ProductSearchResultCount;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

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

        validate(has(productSearchResult, productSearchResultCount));
    }

    @Override
    public boolean is()
    {
        return matches(has(productSearchResult, productSearchResultCount));
    }
}
