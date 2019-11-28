package com.xceptance.loadtest.headless.pages.search;

import com.xceptance.loadtest.headless.pages.components.plp.BlacklistedProductGrid;
import com.xceptance.loadtest.headless.pages.components.plp.ProductGrid;
import com.xceptance.loadtest.headless.pages.components.plp.ProductSearchResult;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

public class SearchNoResultPage extends GeneralPages
{
    public static final SearchNoResultPage instance = new SearchNoResultPage();

    @Override
    public void validate()
    {
        super.validate();

        validate(has(ProductSearchResult.instance), hasAnyOf(has(BlacklistedProductGrid.instance), hasNot(ProductGrid.instance)));
    }

    @Override
    public boolean is()
    {
        return super.is() && (matches(has(ProductSearchResult.instance), hasNot(ProductGrid.instance)) || matches(has(BlacklistedProductGrid.instance)));
    }
}
