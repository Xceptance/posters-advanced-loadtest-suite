package com.xceptance.loadtest.posters.tests.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.data.SearchTermSupplier;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.posters.actions.catalog.Search;
import com.xceptance.loadtest.posters.flows.VisitFlow;
import com.xceptance.loadtest.posters.models.pages.search.SearchResultPage;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Helper class to check all predefined search phrases for having results.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TSearchAllKeywords extends LoadTestCase
{

    @Override
    public void test() throws Throwable
    {
        // This list will contains all the problematic search phrases.
        final List<String> misses = new ArrayList<>();
        final List<String> fatals = new ArrayList<>();
        final List<String> hits = new ArrayList<>();

        // Start at the landing page.
        new VisitFlow().run();

        SearchTermSupplier.getTermWithHit();

        // Then we query for each search phrase one by one.
        for (final String searchPhrase : SearchTermSupplier.getAllTerms())
        {
            // Since the script checks the predefined phrases that are
            // assumed to end in one or more results we pass the search
            // option HITS (expects results[s]) to the search action.
            try
            {
                // Perform the search.
                new Search(searchPhrase, SearchOption.HITS).run();
            }
            catch (final AssertionError ae)
            {
                // don't break on search miss or unexpected page
            }

            if (SearchResultPage.instance.is())
            {
                hits.add(searchPhrase);
            }
            else
            {
                // If there was a problem we collect the phrase and move on.
                misses.add(searchPhrase);
            }

            // Make sure the next search starts on a page with a search field.
            // This is necessary as some sites result in a broken page for some
            // search phrases and all following searches would break immediately
            // because of the missing field.
            if (!SearchResultPage.instance.search.exists())
            {
                // if the search phrase broke the page layout, add it to a
                // special list
                fatals.add(searchPhrase);

                // open the homepage to bring back the search field
                new VisitFlow().run();

                // check the desired result
                SearchResultPage.instance.search.locate().asserted("No search field on homepage.").exists();
            }
        }

        // warn if all phrases failed
        if (hits.isEmpty())
        {
            XltLogger.runTimeLogger.error("All search phrases failed.");

        }
        // print the working search phrases in cases we've got 'misses'
        else if (!misses.isEmpty())
        {
            XltLogger.runTimeLogger.info("############ The following phrases WORKED: ############");
            hits.forEach(System.out::println);
            System.out.println("\n(copy&paste the list above to the search-phrase file)");
        }

        // print the search misses (might also include 'fatals')
        if (!misses.isEmpty())
        {
            XltLogger.runTimeLogger.warn("############ The following phrases FAILED: ############");
            misses.forEach(System.out::println);
        }

        // print the search phrases that caused heavy problems
        if (!fatals.isEmpty())
        {
            XltLogger.runTimeLogger.error("############ The following phrases caused PAGE LAYOUT PROBLEMS: ############");
            fatals.forEach(System.out::println);
        }

        // break the test, listing the search misses (and fatals)
        Assert.assertTrue("The following phrases result in a search miss: " + Arrays.toString(misses.toArray()), misses.isEmpty() && fatals.isEmpty());
    }
}
