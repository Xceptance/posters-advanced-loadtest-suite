package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.data.SearchTermSupplier;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.Search;

/**
 * Executes a search operation either creating a search hit or miss.
 * 
 * @author Xceptance Software Technologies
 */
public class SearchFlow
{
    public boolean run() throws Throwable
    {
    	// The search option indicates if the search should result in a hit or miss
        final SearchOption option = getSearchOption(Context.configuration().searchNoHitsProbability.random());

        // Get a search phrase
        final String searchPhrase = option == SearchOption.HITS ? SearchTermSupplier.getTermWithHit() : SearchTermSupplier.getTermWithoutHits();

        // Run the search with an appropriate search phrase according to the search option
        new Search(searchPhrase, option).run();

        return true;
    }

    /**
     * Returns a search option using the given probability.
     *
     * @param searchWithNoExpectedHits Shall we search with no hits or with hits
     * @return The respective searchOption
     */
    private SearchOption getSearchOption(final boolean searchWithNoExpectedHits)
    {
        if (searchWithNoExpectedHits)
        {
            return SearchOption.MISS;
        }
        else
        {
            return SearchOption.HITS;
        }
    }
}