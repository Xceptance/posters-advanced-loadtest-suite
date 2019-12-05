package posters.actions.catalog;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.actions.NonPageView;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DOMUtils;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.xlt.api.util.XltRandom;

import posters.pages.catalog.QuickviewPage;
import posters.pages.general.GeneralPages;

/**
 * Enter a given search phrase in the site's search bar and submit.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class SearchSuggestion extends AjaxAction<SearchSuggestion> implements NonPageView
{
    private static final int MIN_PHRASE_LENGTH = 3;
    private static final int MIN_KEYSPRESSED_LENGTH = 2;
    private static final int MAX_KEYSPRESSED_LENGTH = 4;

    /** Search phrase. */
    private final String phrase;

    private HtmlElement searchSuggestionContainer;

    private String url;

    /**
     * Constructor.
     *
     * @param phrase
     *            search phrase
     */
    public SearchSuggestion(final String phrase)
    {
        this.phrase = phrase;
    }

    @Override
    public void precheck()
    {
        Assert.assertTrue("Search phrase shorter than Minimum", phrase.length() >= MIN_PHRASE_LENGTH);

        // Lookup the search suggestion URL. It is located in an embedded script
        // that contains the 'app resources'. The desired URL is extracted by a
        // precompiled regular expression pattern.
        // url = AjaxUtils.getAppResourceValue("searchsuggest");
        searchSuggestionContainer = GeneralPages.instance.siteSearch.locateSuggestionContainer().asserted("No single suggestions wrapper found.").single();
        url = searchSuggestionContainer.getAttribute("data-url");

        // close existing quick view, this is safe
        QuickviewPage.instance.quickview.closeQuickview();
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
            cacheBuster = Search.CACHEBUSTERFIXEDPART + XltRandom.nextInt(Context.configuration().searchCacheBustingCount);
        }
        else
        {
            cacheBuster = "";
        }

        // If used manually the search bar will present suggestions for
        // phrases based on the yet entered letters. These suggestions are
        // requested by XHR calls.

        // Since a human mostly enters a search phrase slow enough to present
        // some search suggestion while still typing, we simulate this behavior
        // for the test case. So we split the search phrase and request the
        // suggestions for the partial search phrase.

        int snippetLength = 0;
        final int phraseLength = phrase.length();

        do
        {
            // Calculate new length.
            snippetLength += XltRandom.nextInt(MIN_KEYSPRESSED_LENGTH, MAX_KEYSPRESSED_LENGTH);

            // Adjust length if necessary.
            snippetLength = snippetLength > phraseLength ? phraseLength : snippetLength;

            // Get the snippet.
            final String snippet = phrase.substring(0, snippetLength);

            // Get the suggestion(s)
            final WebResponse response = new HttpRequest().XHR().url(url)
                            .removeParam("q")
                            .param("q", snippet + cacheBuster)
                            .assertStatusCode(200)
                            .fire();

            // right now, we can have two responses, this is still highly
            // incorrect, but not yet fixed in MFSG
            if (response.getContentType().equals("application/json"))
            {
                // response was empty
            }
            else if (response.getContentType().equals("text/html"))
            {
                // ok, that is better
                DOMUtils.replaceContent(searchSuggestionContainer, response.getContentAsString());
            }
            else
            {
                // ok, that is wrong, really wrong
                Assert.fail("Unexpected content type " + response.getContentType());
            }
        }
        while (snippetLength < phraseLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // no page validation, because we have not loaded a page or can know for
        // sure that we got anything
    }
}
