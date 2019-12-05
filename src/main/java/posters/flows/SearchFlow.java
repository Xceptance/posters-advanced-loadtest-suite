package posters.flows;

import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.data.SearchTermSupplier;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.catalog.Search;
import posters.actions.catalog.SearchSuggestion;

public class SearchFlow
{
    public boolean run() throws Throwable
    {
        // The search option is the indicator whether to search for one of
        // the search phrases from the 'HITS_PROVIDER' that results in a hit
        // or a generated phrase that results in a 'no results' page.
        final SearchOption option = getSearchOption(Context.configuration().searchNoHitsProbability.random());

        // get a term
        final String searchPhrase = option == SearchOption.HITS ? SearchTermSupplier.getTermWithHit() : SearchTermSupplier.getTermWithoutHits();

        // shall we do suggestions? Don't rely on the prevalidate exception, be
        // more efficient
        if (Context.configuration().searchSuggestionsEnabled)
        {
            // Do the search suggestions requests.
            new SearchSuggestion(searchPhrase).runIfPossible();
        }

        // Run the search with an appropriate search phrase according to the
        // search option.
        new Search(searchPhrase, option).run();

        // Browse the results, open product details, and configure product
        // if possible
        new RefineByFlow().run();

        return true;
    }

    /**
     * Returns a search option using the given probability.
     *
     * @param searchWithNoExpectedHits
     *            Shall we search with no hits or with hits... at least try
     * @return search option
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
