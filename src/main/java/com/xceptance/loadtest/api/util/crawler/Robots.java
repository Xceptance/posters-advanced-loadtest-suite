package com.xceptance.loadtest.api.util.crawler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.util.Context;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public enum Robots
{
    TXT;

    private final Map<Site, RobotsTxtData> robotsTxtDatas = new ConcurrentHashMap<>();

    /**
     * <pre>
     * [Site -> [BotName -> RobotsTxt]]
     * </pre>
     */
    private final Map<Site, Map<String, BaseRobotRules>> siteRobotsTxts = new ConcurrentHashMap<>();

    /**
     * <pre>
     * [Site -> URLs]
     * </pre>
     */
    private final Map<Site, Collection<String>> sitemapUrls = new ConcurrentHashMap<>();

    public boolean isAllowed(final String user, final String href)
    {
        return getRobotsTxt(Context.getSite(), user).isAllowed(href);
    }

    public Collection<String> getSitemapUrls() throws Exception
    {
        return sitemapUrls.computeIfAbsent(Context.getSite(), s -> getRobotsTxt(Context.getSite(), "*").getSitemaps());
    }

    private BaseRobotRules getRobotsTxt(final Site site, final String user)
    {
        final Map<String, BaseRobotRules> robotsTxts = siteRobotsTxts.computeIfAbsent(site, s -> new ConcurrentHashMap<>());

        // if not loaded already, get and parse robots.txt (lazy)
        return robotsTxts.computeIfAbsent(user, u ->
        {
            final RobotsTxtData robotsTxtData = robotsTxtDatas.get(site);
            Assert.assertNotNull("robots.txt not initialized. Please try to load robots.txt first.", robotsTxtData);

            return new SimpleRobotRulesParser().parseContent(robotsTxtData.getUrl(), robotsTxtData.getContent(), robotsTxtData.getContentType(), user);
        });
    }

    public Robots readFrom(final WebResponse robotsTxtResponse)
    {
        robotsTxtDatas.computeIfAbsent(Context.getSite(), s ->
        {
            final String robotsUrl = robotsTxtResponse.getWebRequest().getUrl().toExternalForm();
            final byte[] robotsContent;
            final String robotsContentType;

            if (robotsTxtResponse.getStatusCode() == 200)
            {
                robotsContent = robotsTxtResponse.getContentAsString().getBytes();
                robotsContentType = robotsTxtResponse.getResponseHeaderValue("content-type");
            }
            else
            {
                // robots.txt is optional, so ignore missing robot.txt file.
                // But fake an empty robots.txt, what is equal to a missing robots.txt.
                robotsContent = new byte[0];
                robotsContentType = "UTF-8";
            }
            return new RobotsTxtData(robotsUrl, robotsContent, robotsContentType);
        });

        return this;
    }

    /**
     * Raw data of robots.txt file.
     */
    private class RobotsTxtData
    {
        private final String url;
        private final byte[] content;
        private final String contentType;

        public RobotsTxtData(final String url, final byte[] content, final String contentType)
        {
            this.url = url;
            this.content = content;
            this.contentType = contentType;
        }

        public String getUrl()
        {
            return url;
        }

        public byte[] getContent()
        {
            return content;
        }

        public String getContentType()
        {
            return contentType;
        }
    }
}
