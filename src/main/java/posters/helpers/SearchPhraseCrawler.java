package posters.helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.tests.LoadTestCase;

import posters.actions.catalog.DisplayMore;
import posters.actions.catalog.Search;
import posters.actions.crawler.CrawlerURL;
import posters.flows.VisitFlow;
import posters.pages.catalog.ProductListingPage;
import posters.pages.general.GeneralPages;
import posters.pages.search.SearchResultPage;

/**
 * Simple Test to find search phrases on the page.
 *
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class SearchPhraseCrawler extends LoadTestCase
{
    /**
     * Regexpt to filter unwanted searchphrases. Currently only words with at
     * least 3, but only alphabetically characters are allowed.
     */
    private static final String VALID_PHRASES_REGEXP = "^[a-zA-Z]{3,}$";

    /**
     * Indicates if the result list of valid search terms should be written to
     * the config/data/search-phrases.txt. </br>
     * </br>
     * true: written to file </br>
     * false: printed in system out
     */
    private static final boolean SAVE_TO_FILE = false;

    /**
     * If results should get saved to file this constant specifies the file.
     */
    private static final String SEARCH_PHRASES_FILE = "config/data/search-phrases.txt";

    /**
     * Indicates if only category names should be used for search phrases. </br>
     * </br>
     * true: only (top)category names are used (faster but less results) </br>
     * false: also product names are used (slower but more results)
     */
    private static final boolean CATEGORIES_ONLY = true;

    /**
     * Stop searching after the list has reached MAX_PHRASES valid search terms.
     */
    private static final int MAX_PHRASES = 100;

    /**
     * Indicates if search terms with only one result should be used for the
     * search term list
     */
    private static final boolean PHRASES_WITH_MULTIPLE_RESULTS_ONLY = true;

    @Override
    protected void test() throws Throwable
    {
            // Open start page.
            new VisitFlow().run();

            // Initialize phrases lists.
            final Set<String> searchTerms = new HashSet<>();
            final Set<String> possiblePhrases = new HashSet<>();

            // Collect phrases.
            collectPossiblePhrases(possiblePhrases);

            // Search previously collected phrases and remember the phrases that had a hit.
            for (final String phrase : possiblePhrases)
            {
                try
                {
                    // Search for current phrase.
                    new Search(phrase, SearchOption.HITS).run();

                    // Valid search result page?
                    if (SearchResultPage.instance.is())
                    {
                        // Remember search phrase depending on the decision to keep all positive or phrases with multiple
                        // results only.
                        if (!PHRASES_WITH_MULTIPLE_RESULTS_ONLY || ProductListingPage.instance.is())
                        {
                            searchTerms.add(phrase);
                        }

                        // Stop if enough search phrases were collected.
                        if (searchTerms.size() >= MAX_PHRASES)
                        {
                            break;
                        }
                    }
                }
                catch (final AssertionError e)
                {
                    if (!SearchResultPage.instance.productSearchResult.exists())
                    {
                        // We are on some error page (e.g. server transmission error) so simply visit home page again
                        try
                        {
                            // Open home page
                            new VisitFlow().run();

                            // Verify that we are able to search
                            Assert.assertTrue("Failed to find search field on homepage.", SearchResultPage.instance.productSearchResult.exists());
                        }
                        catch (final AssertionError ae)
                        {
                            // Failed again, so stop search and print results collected so far
                            break;
                        }
                    }
                    else
                    {
                        // No hit for this one. Simply continue with next phrase.
                    }
                }
            }

            // Print some statistics.
            System.out.println("Extracted" + searchTerms.size() + " out of " + possiblePhrases.size() + " initial phrases.");

            // Output search phrases.
            printOut(searchTerms);
    }

    /**
     *
     */
    private void printOut(final Set<String> searchTerms) throws IOException
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("#######################################\n");
        sb.append("Found ").append(searchTerms.size()).append(" valid search terms.\n");
        sb.append("#######################################\n\n");

        // Decide to save the phrases to file or print it to console.
        if (SAVE_TO_FILE)
        {
            // Write to file.
            try (final FileWriter writer = new FileWriter(SEARCH_PHRASES_FILE))
            {
                String prefix = "";
                for (final String searchTerm : searchTerms)
                {
                    writer.write(prefix + searchTerm);
                    prefix = "\n";
                }
                sb.append("Search phrases written to file.\n");
            }
        }
        else
        {
            // Print to console.
            for (final String serchTerm : searchTerms)
            {
                sb.append(serchTerm).append("\n");
            }

            sb.append("#######################################\n");
            sb.append("#######################################\n");
        }

        System.out.println(sb.toString());
    }

    /**
     * Add some search phrases to the collection of possible search phrases.
     *
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     */
    private void collectPossiblePhrases(final Set<String> possibleSearchPhrases) throws Throwable
    {
        // Collect by sub categories.
        addCategoryFindingsToList(possibleSearchPhrases, GeneralPages.instance.navigation.getTopCategories().<HtmlAnchor>all());

        // collect by top categories.
        addCategoryFindingsToList(possibleSearchPhrases, GeneralPages.instance.navigation.getCategories().<HtmlAnchor>all());
    }

    /**
     * Add search phrases extracted from sub category names to list of possible
     * search phrases.
     *
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     * @param elements
     *            category links
     */
    private void addCategoryFindingsToList(final Set<String> possibleSearchPhrases, final List<HtmlAnchor> elements) throws Throwable
    {
        // Get the passed category names.
        for (final HtmlAnchor htmlElement : elements)
        {
            // Split into separate words and add each word.
            final String[] split = htmlElement.getTextContent().trim().split(" ");
            for (final String string : split)
            {
                final String possiblePhrase = string.trim();
                if (!StringUtils.isEmpty(possiblePhrase)
                                && RegExUtils.isMatching(possiblePhrase, VALID_PHRASES_REGEXP))
                {
                    // Add trimmed.
                    possibleSearchPhrases.add(possiblePhrase.toLowerCase());
                }
            }

            // If we search for category phrases we can stop here. Otherwise we
            // browse the category and try to extract
            // product names as well.
            if (!CATEGORIES_ONLY)
            {
                // Browse the category to extract product names.
                addProductGridFindingsToList(possibleSearchPhrases, htmlElement);
            }
        }
    }

    /**
     * Browse category and extract search phrases from found products.
     *
     * @param possibleSearchPhrases
     *            collection of possible search phrases
     * @param anchor
     *            category link
     */
    private void addProductGridFindingsToList(final Set<String> possibleSearchPhrases, final HtmlAnchor anchor)
                    throws MalformedURLException, Throwable
    {
        // Follow category link.
        new CrawlerURL(anchor.getHrefAttribute()) //
                        .assertBasics() //
                        .assertWebResponse("Expected resonse code 200.", r -> r.getStatusCode() == 200) //
                        .run();

        // If we have infinite scroll just scroll and load more products.
        while (new DisplayMore().runIfPossible().isPresent())
        {
            // scroll as long as possible
        }

        // If paging is possible do it once (since we choose random paging link
        // and don't know how long to loop)
        if (new DisplayMore().runIfPossible().isPresent())
        {
            // perform as many paging actions as possible
        }

        // Extract search phrases.
        selectSearchPhrasesAction(possibleSearchPhrases);
    }

    private void selectSearchPhrasesAction(final Set<String> list)
    {
        final List<HtmlElement> all = getProductNames();
        for (final HtmlElement htmlElement : all)
        {
            final String[] split = htmlElement.getTextContent().trim().split(" ");
            for (final String string : split)
            {
                final String possiblePhrase = string.trim();
                if (!StringUtils.isEmpty(possiblePhrase)
                                && RegExUtils.isMatching(possiblePhrase, VALID_PHRASES_REGEXP))
                {
                    // Add trimmed.
                    list.add(possiblePhrase.toLowerCase());
                }
            }
        }
    }

    private static List<HtmlElement> getProductNames()
    {
        return ProductListingPage.instance.productGrid.getProducts().all();
    }
}
