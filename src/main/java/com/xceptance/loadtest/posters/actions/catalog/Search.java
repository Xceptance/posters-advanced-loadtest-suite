package com.xceptance.loadtest.posters.actions.catalog;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.CustomDataLogger;
import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.FormUtils;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.models.pages.search.SearchNoResultPage;
import com.xceptance.loadtest.posters.models.pages.search.SearchResultPage;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Searches for a given search phrase and expects a given search result.
 *
 * @author Xceptance Software Technologies
 */
public class Search extends PageAction<Search>
{
    /**
     * The fixed part of the cache buster which is added to a search phrase.
     */
    public final static String CACHEBUSTERFIXEDPART = " -9823754123321";

    private final String phrase;

    private final SearchOption searchOption;

    private long searchTime;

    /**
     * Constructor.
     *
     * @param phrase
     *            search phrase
     * @param searchOption
     *            search option specifies if search results are expected or not
     */
    public Search(final String phrase, final SearchOption searchOption)
    {
        this.searchOption = searchOption;
        this.phrase = phrase;

        // Set timer name
        if (searchOption == SearchOption.MISS)
        {
            this.setTimerName(this.getTimerName() + "NoHits");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final String cacheBuster;
        if (Context.configuration().searchCacheBusting)
        {
            cacheBuster = CACHEBUSTERFIXEDPART + XltRandom.nextInt(Context.configuration().searchCacheBustingCount);
        }
        else
        {
            cacheBuster = "";
        }

        // We do not change the URL any longer, we enter a search phrase that
        // excludes some data that is always not in, hence we also avoid the hit
        // in the search cache partially not only in the page cache
        // avoid that the cache buster part is
        final LookUpResult inputField = GeneralPages.instance.search.getSearchField();
        FormUtils.setInputValue(inputField, phrase + cacheBuster);

        // Fill the search form with the given phrase and add a hidden field
        // containing an always changing value to bypass the query cache.
        final HtmlForm searchForm = inputField.single().getEnclosingForm();

        // start measurement for search request
        final CustomDataLogger time = CustomDataLogger.start("Search");

        // Submit the search.
        loadPageByFormSubmit(searchForm);

        // Stop timer
        searchTime = time.stopAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
    	// Validate the page load
        Validator.validatePageSource();

        // Have we expected hits?
        final boolean hitsExpected = SearchOption.HITS.equals(searchOption);

        // The name to log
        String name = "Search - N/A";
        
        // The status to log
        boolean failed = false;

        // Did we result in a no hits page?
        try
        {
            if (SearchNoResultPage.instance.is())
            {
                name = "Search - Miss";

                if (hitsExpected)
                {
                    failed = true;
                    EventLogger.BROWSE.warn("Unexpected Search Miss", phrase);
                }

                // Validate resulting page
                SearchNoResultPage.instance.validate();
            }
            // Did we land on a Search result?
            else if (SearchResultPage.instance.is())
            {
                name = "Search - Hits";

                if (!hitsExpected)
                {
                    failed = true;
                    EventLogger.BROWSE.warn("Unexpected Search Hits", phrase);
                }

                // Validate resulting page
                SearchResultPage.instance.validate();
            }
            else if (ProductDetailPage.instance.is())
            {
                // Direct hit
                name = "Search - Hits";

                if (!hitsExpected)
                {
                    failed = true;
                    EventLogger.BROWSE.warn("Unexpected Search Hits", phrase);
                }

                // Validate resulting page
                ProductDetailPage.instance.validate();
            }
            else
            {
                // No valid search result identified. Fail to get aware of it.
                failed = true;
                Assert.fail("Search resulted in unknown search result page.");
            }
        }
        finally
        {
            // Log timer record
            CustomDataLogger.log(name, searchTime, failed);
        }
    }
}