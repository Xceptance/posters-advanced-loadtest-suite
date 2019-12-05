package posters.tests;

import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.crawler.CrawlerConfig;

import posters.actions.crawler.CrawlerURL;
import posters.flows.CrawlerFlow;

/**
 * A crawler exloring the site as configured by property file or API while API rules over property.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TCrawler extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Configure crawler
        /**
         * <pre>
         * final CrawlerConfig crawlerConfig.loadDefaults() //
         *                                  .includeUrlPattern("whitelist_substring_pattern").includeUrlPatterns(collection) //
         *                                  .excludeUrlPattern("blacklist_substring_pattern").excludeUrlPatterns(collection) //
         *                                  .requireText("good").requireTexts(collection) //
         *                                  .disallowText("bad").disallowTexts(collection) //
         *                                  .depthMax(3) //
         *                                  .numberOfPages(100) //
         *                                  .runMax(10).minutes() //
         *                                  .dropSession().always() //
         *                                  .dropSession().every(30).minutes() //
         *                                  .dropSession().every(10).pages() //
         *                                  .dropSession().never() //
         *                                  .noCache(true)
         *                                  ;
         * </pre>
         */
        final CrawlerConfig crawlerConfig = CrawlerConfig.create();

        if (crawlerConfig.forceLinkMissProbability.value)
        {
            // Open link that is expected to point to an non-existing page.
            openMissLink(crawlerConfig);
        }
        else
        {
            // initialize the crawler flow
            new CrawlerFlow(crawlerConfig).run();
        }
    }

    private void openMissLink(final CrawlerConfig crawlerConfig) throws Throwable
    {
        final String startURL = Context.configuration().siteUrlHomepage;

        final URIBuilder uriBuilder = new URIBuilder(startURL);
        final String url = uriBuilder.setPath(uriBuilder.getPath() + "/" + UUID.randomUUID().toString())
                                     .removeQuery()
                                     .build().toString();

        // initialize request
        CrawlerURL simpleAction = null;
        final String statusCodePattern = crawlerConfig.expectedMissStatusCodePattern;
        if (statusCodePattern != null)
        {
            // set status code assertion
            simpleAction = new CrawlerURL(url, statusCodePattern);
        }
        else
        {
            simpleAction = new CrawlerURL(url);
        }

        // set text assertion
        if (crawlerConfig.expectedMissTextPattern != null)
        {
            simpleAction.assertWebResponse("Unexpected error message.", r -> RegExUtils.isMatching(r.getContentAsString(), crawlerConfig.expectedMissTextPattern));
        }

        simpleAction.run();
    }
}
