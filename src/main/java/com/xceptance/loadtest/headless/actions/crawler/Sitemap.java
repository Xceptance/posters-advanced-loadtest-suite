package com.xceptance.loadtest.headless.actions.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.util.crawler.Robots;

import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;

public class Sitemap extends PageAction<Sitemap>
{
    /** INPUT */
    private final Collection<String> sitemapUrls;

    /** URL container */
    final Collection<SiteMapURL> urls = new HashSet<>();

    public Sitemap() throws Exception
    {
        super();
        sitemapUrls = Robots.TXT.getSitemapUrls();
    }

    @Override
    protected void doExecute() throws Exception
    {
        // load all sitemaps referenced in robots.txt and store their final page URLs
        for(final String sitemapUrl : sitemapUrls)
        {
            final Collection<SiteMapURL> results = loadSiteMap(new SiteMap(sitemapUrl));
            urls.addAll(results);
        }
    }

    private Collection<SiteMapURL> loadSiteMap(final AbstractSiteMap sitemap) throws Exception
    {
        final Collection<SiteMapURL> collectedResults = new LinkedList<>();

        final WebRequest request = new WebRequest(sitemap.getUrl(), HttpMethod.GET);
        final WebResponse r = getWebClient().loadWebResponse(request);

        // loading was successful
        if (r.getStatusCode() == 200)
        {
            // parse sitemap content
            final AbstractSiteMap asm = new SiteMapParser().parseSiteMap(r.getResponseHeaderValue("content-type"), r.getContentAsString().getBytes(), sitemap);

            switch (asm.getType()) {
                case INDEX:
                    // if it is a sitemap index (references other sitemaps), load them recursively
                    final SiteMapIndex sitemapIndex = (SiteMapIndex) asm;
                    for (final AbstractSiteMap sitemapInternal : sitemapIndex.getSitemaps())
                    {
                        final Collection<SiteMapURL> results = loadSiteMap(sitemapInternal);
                        collectedResults.addAll(results);
                    }
                    break;
                default:
                    // otherwise read all the page URLs
                    final SiteMap sm = (SiteMap) asm;
                    return new ArrayList<>(sm.getSiteMapUrls());
            }
        }
        else
        {
            // loading failed
            EventLogger.CRAWLER.error("Skipped loading sitemap.", sitemap.getUrl().toExternalForm());
        }

        return collectedResults;
    }

    @Override
    protected void postExecute() throws Exception
    {
        // nothing
    }

    @Override
    protected void postValidate() throws Exception
    {
        // nothing
    }

    public List<String> getSitemappedUrls()
    {
        return getSitemappedUrls(false);
    }

    public List<String> getSitemappedUrlsPriorised()
    {
        return getSitemappedUrls(true);
    }

    /**
     * Sort the entries by priority if desired, extract the URL strings, remove
     * duplicates.
     *
     * @param prioritized
     * @return
     */
    private List<String> getSitemappedUrls(final boolean prioritized)
    {
        Stream<SiteMapURL> stream = urls.parallelStream();

        if (prioritized)
        {
            // sort them by priority
            stream = stream.sorted((s1, s2) -> Double.compare(s1.getPriority(), s2.getPriority()));
        }

        return  // get the URLs only
                stream.map(siteMapUrl -> siteMapUrl.getUrl().toExternalForm())

                // ignore multiple entries of same URL
                .distinct()

                // return the resulting list
                .collect(Collectors.toList());
    }
}
