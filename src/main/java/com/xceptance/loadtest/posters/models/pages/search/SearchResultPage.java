package com.xceptance.loadtest.posters.models.pages.search;

import com.xceptance.loadtest.posters.models.components.plp.SearchQuery;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;

/**
 * Represent a search result page.
 * 
 * Next to PLP components this page also has a search query component.
 * 
 * @author Xceptance Software Technologies
 */
public class SearchResultPage extends ProductListingPage
{
    public static final SearchResultPage instance = new SearchResultPage();
    
    public final SearchQuery searchQuery = SearchQuery.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(searchQuery));
    }

    @Override
    public boolean is()
    {
        return matches(has(searchQuery));
    }
}