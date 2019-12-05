package posters.flows;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.SimpleURL;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.crawler.CrawlerConfig;
import com.xceptance.loadtest.api.util.crawler.DropSession;
import com.xceptance.loadtest.api.util.crawler.LinkCache;
import com.xceptance.loadtest.api.util.crawler.Robots;
import com.xceptance.xlt.api.util.XltRandom;

import posters.actions.crawler.CrawlerURL;
import posters.actions.crawler.RobotsTxt;

/**
 * <p>
 * Opens a page (level L0) and collects links on that page.
 * </p>
 *
 * <pre>
 *            L0
 *            /\
 *           /  \
 *         L1a   L1b
 *         /\    /\
 *        /  \  /  \
 *       L2a  L2b  L2c
 * </pre>
 * <p>
 * Follows all level L1* links first and collects links on these pages (to level L2*). Then follows level L2* links and collects links on these pages<br>
 * .. and so on.
 * </p>
 * <p>
 * The order of link on a page is ignored. Link duplicates are ignored.
 * </p>
 * <p>
 * Example page visit order:<br>
 * [L0] -> [L1a, L1b] -> [L2b, L2c, L2a]
 * </p>
 *
 * @author matthias.ullrich
 *
 */
public class CrawlerFlow extends Flow
{
    /** crawler configuration */
    private final CrawlerConfig crawlerConfig;

    /** Collected URLs with corresponding crawl depth */
    private final List<KeyValue> urlsTovisit = new LinkedList<>();

    /** Visited URLs */
    private final Collection<String> visitedUrls = new HashSet<>();

    /** Crawler start time in seconds */
    private long startTime;

    /**
     * create new crawler flow
     *
     * @param crawlerConfig
     *            crawler configuration
     * @param statUrl
     *            where to start crawling
     */
    public CrawlerFlow(final CrawlerConfig crawlerConfig) throws Throwable
    {
        this.crawlerConfig = crawlerConfig;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public boolean execute() throws Throwable
    {
        startTime = System.currentTimeMillis() / 1000;

        final String startAt = crawlerConfig.startAt.value;

        // pre-requisits
        if (crawlerConfig.applyRobotsTxtRules)
        {
            // load robots.txt and initialize Robots.TXT util
            new RobotsTxt().run();
        }
        else if (CrawlerConfig.START_AT_KNOWN_PAGE.equals(startAt))
        {
            addUrlToVisit(LinkCache.getRandom());
        }

        // Either Start URL was chosen as entry point
        // OR for the entry points above no URL is available.
        // In both cases start at the general Start URL.
        if (urlsTovisit.isEmpty())
        {
            addUrlToVisit(Context.configuration().siteUrlHomepage);
        }

        // follow links on that page as far as possible
        while (!urlsTovisit.isEmpty() && !isTimedOut() && !isMaxNumberOfPagesReached())
        {
            final KeyValue link = urlsTovisit.remove(0);
            follow(link);
        }
        return true;
    }

    /**
     * Open initial page and collect links on that page to follow.
     *
     * @param url
     *            URL of page to open
     * @throws Throwable
     */
    void follow(final String url) throws Throwable
    {
        follow(new KeyValue(url, 0));
    }

    /**
     * Open page and collect links on that page to follow.
     *
     * @param link
     *            link object, containing URL and link depth
     * @throws Throwable
     */
    void follow(final KeyValue link) throws Throwable
    {
        final String href = link.getHref();

        // update session
        updateSession();

        // update history
        visitedUrls.add(href);

        // open page
        final boolean success = openPage(href);
        if (success)
        {
            // remember "known" page
            LinkCache.put(href);

            final int nextDepth = link.getDepth() + 1;

            // follow links on page if text assertions match and
            // we haven't reached maximum depth already.
            if (isPageTextOk() && isDepthAllowed(nextDepth))
            {
                for (final String url : getPageLinks())
                {
                    addUrlToVisit(url, nextDepth);
                    // urlsTovisit.add(new KeyValue(url, nextDepth));
                }
            }
        }
    }

    private void addUrlToVisit(final String url)
    {
        addUrlToVisit(url, 0);
    }

    private void addUrlToVisit(final String url, final int depth)
    {
        if (isAllowedToBeAdded(url) && depth >= 0)
        {
            urlsTovisit.add(new KeyValue(url, depth));
        }
    }

    /**
     * Open page referenced by link
     *
     * @param href
     *            the link
     * @returns <code>true</code> if page load finished without problems,
     *          <code>false</code> otherwise
     * @see {@link CrawlerURL#postValidate}
     * @throws Throwable
     */
    boolean openPage(String href) throws Throwable
    {
        // add some parameter to bypass server side caching
        if (crawlerConfig.bypassCache)
        {
            href = appendNoCacheParameter(href);
        }

        try
        {
            final SimpleURL simpleClick = new CrawlerURL(href);
            simpleClick.run();
            return true;
        }
        catch (final AssertionError e)
        {
            String eventName = e.getMessage();
            if (StringUtils.isBlank(eventName))
            {
                eventName = "Unknown Assertion Error";
            }
            EventLogger.CRAWLER.error(eventName, href);
        }

        return false;
    }

    /**
     * Grab the links on page.
     *
     * @throws Throwable
     */
    private List<String> getPageLinks() throws Throwable
    {
        final List<String> urls = new ArrayList<>();

        // check if meta tags restrict or permit following links on the current page
        if (isMetaTagPermission())
        {
            final Collection<HtmlAnchor> anchors = getPageAnchors();
            for (final HtmlAnchor anchor : anchors)
            {
                final String url = Context.getPage().getFullyQualifiedUrl(anchor.getHrefAttribute()).toExternalForm();
                urls.add(url);
            }
        }
        return urls;
    }

    private boolean isAllowedToBeAdded(final String url)
    {
        // check URL includes and excludes
        return (!visitedUrls.contains(url) && isIncluded(url) && !isExcluded(url) && isRobotsTxtPermission(url));
    }

    /**
     * Following links is forbidden, if it's explicitly disallowed in robots.txt. Applying these rules can be overridden by configuration.
     *
     * @see {@link CrawlerConfig#isRobotsTxtRuleDesired()}
     *
     * @param href
     *            the URL to follow
     * @return <code>true</code> if following this URL is permitted,
     *         <code>false</code> otherwise
     */
    private boolean isRobotsTxtPermission(final String href)
    {
        if (crawlerConfig.applyRobotsTxtRules)
        {
            return Robots.TXT.isAllowed(crawlerConfig.userName, href);
        }

        return true;
    }

    /**
     * Following link is forbidden, if
     * <ul>
     * <li>a meta tag with name 'robots' exists AND</li>
     * <li>it's content attribute has the value nofollow.</li>
     * </ul>
     * In case of absence of such a tag, following links is okay. <br>
     * <br>
     * This rule might be overridden by configuration.
     *
     * @see {@link CrawlerConfig#isMetaTagRuleDesired()}
     *
     * @return <code>true</code> if following links is permitted,
     *         <code>false</code> otherwise
     */
    private boolean isMetaTagPermission()
    {
        if (crawlerConfig.applyMetaTagRules)
        {
            // Lookup 'robots' meta tag ...
            final LookUpResult robotsMetatag = Page.find().byXPath("/html/head/meta[@name='robots' and @content]");

            // ... then check if it's forbidden to follow links
            return !robotsMetatag.exists() || !"nofollow".equalsIgnoreCase(robotsMetatag.first().getAttribute("content"));
        }

        return true;
    }

    /**
     * Check page text for required or disallowed text
     *
     * @return <code>true</code> if page text contains required text and does not contain disallowed text
     */
    private boolean isPageTextOk()
    {
        // check that page contains required text(s) AND
        // doesn't contain disallowed text(s)
        final String pageText = getPageText();
        return isRequiredText(pageText) && !isDisallowedText(pageText);
    }

    /**
     * Get page text
     *
     * @return page text
     */
    private String getPageText()
    {
        return Context.getPage().getTextContent();
    }

    /**
     * Are we allowed to proceed to next depth
     *
     * @return
     */
    private boolean isDepthAllowed(final int depth)
    {
        return depth < crawlerConfig.maxDepthOfRecursion;
    }

    /**
     * Get page anchors, having a 'href' attribute, if desired don't have an
     * attribute 'rel=nofollow', shuffled around to get some randomness.
     *
     * @return shuffled page anchors
     */
    private List<HtmlAnchor> getPageAnchors()
    {
        // anchors found on page
        final List<HtmlAnchor> anchors = new ArrayList<>(Context.getPage().getAnchors());

        anchors.removeIf(anchor -> !anchor.hasAttribute("href"));
        anchors.removeIf(anchor -> anchor.getAttribute("href").contains("//"));

        // if desired, ignore all links tagged with attribute "rel=nofollow"
        if (crawlerConfig.applyMetaTagRules)
        {
            anchors.removeIf(anchor -> "nofollow".equalsIgnoreCase(anchor.getAttribute("rel")));
        }

        // bring some random into game
        Collections.shuffle(anchors, XltRandom.getRandom());

        return anchors;
    }

    /**
     * Update the crawler session
     */
    private void updateSession()
    {
        final DropSession dropSession = crawlerConfig.dropSession;
        if (dropSession != null)
        {
            dropSession.update();
        }
    }

    public static String appendNoCacheParameter(final String url)
    {
        return url + (url.contains("?") ? "&" : "?") + System.currentTimeMillis();
    }

    /**
     * Has the crawler reached its EOL?
     *
     * @return
     */
    private boolean isTimedOut()
    {
        if (crawlerConfig.runtime.value <= 0)
        {
            return false;
        }

        return now() - startTime >= crawlerConfig.runtime.value;
    }

    /**
     * Current time
     *
     * @return
     */
    private long now()
    {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Has the crawler parsed the maximum number of pages?
     *
     * @return
     */
    private boolean isMaxNumberOfPagesReached()
    {
        return visitedUrls.size() >= crawlerConfig.totalNumberOfPages;
    }

    /**
     * Anchor URL is included if no include filter is given or it matches at least one include filter
     *
     * @param anchor
     * @return
     */
    private boolean isIncluded(final String href)
    {
        boolean isIncluded = true;

        if (StringUtils.isNotBlank(href))
        {
            if (!crawlerConfig.includePatterns.isEmpty())
            {
                isIncluded = false;
                final String urlString = getFullyQualifiedUrlString(href);
                if (StringUtils.isNotBlank(urlString))
                {
                    isIncluded = crawlerConfig.includePatterns.parallelStream().anyMatch(pattern -> RegExUtils.isMatching(urlString, pattern));

                    if (!isIncluded && !Context.isLoadTest)
                    {
                        EventLogger.CRAWLER.debug("Skipped loading (not included)", urlString);
                    }
                }
                else if (!Context.isLoadTest)
                {
                    EventLogger.CRAWLER.warn("Skipped loading (malformed URL)", href);
                }
            }
        }
        else
        {
            isIncluded = false;
            if (!Context.isLoadTest)
            {
                EventLogger.CRAWLER.debug("Skipped loading (blank)", "(blank)");
            }
        }

        return isIncluded;
    }

    /**
     * Anchor URL is excluded if it matches at least one exclude filter
     *
     * @param anchor
     * @return
     */
    private boolean isExcluded(final String href)
    {
        boolean isExcluded = false;

        if (StringUtils.isNotBlank(href))
        {
            final String urlString = getFullyQualifiedUrlString(href);
            if (StringUtils.isNotBlank(urlString))
            {
                isExcluded = crawlerConfig.excludePatterns.parallelStream().anyMatch(pattern -> RegExUtils.isMatching(urlString, pattern));

                if (isExcluded && !Context.isLoadTest)
                {
                    EventLogger.CRAWLER.debug("Skipped loading (excluded)", urlString);
                }
            }
            else if (!Context.isLoadTest)
            {
                EventLogger.CRAWLER.warn("Skipped loading (malformed URL)", href);
            }
        }
        else
        {
            isExcluded = true;
            if (!Context.isLoadTest)
            {
                EventLogger.CRAWLER.debug("Skipped loading (blank)", "(blank)");
            }
        }

        return isExcluded;
    }

    private String getFullyQualifiedUrlString(final String urlString)
    {
        try
        {
            return urlString.startsWith("http:") | urlString.startsWith("https:")
                    ? urlString
                    : Context.getPage().getFullyQualifiedUrl(urlString).toExternalForm();
        }
        catch (final MalformedURLException e)
        {
            EventLogger.CRAWLER.warn("Skipped (malformed URL)", urlString);
        }

        return null;
    }

    private boolean isRequiredText(final String pageText)
    {
        boolean isRequiredText = true;

        // no restriction -> required text condition is fulfilled
        // otherwise check page
        if (!crawlerConfig.requireTexts.isEmpty())
        {
            isRequiredText = false;
            if (StringUtils.isNotEmpty(pageText))
            {
                isRequiredText = true;
                // check if at least one of the required texts is contained
                // req texts condition: ALL or ONE
                for (final String text : crawlerConfig.requireTexts.weightedList())
                {
                    if (!RegExUtils.isMatching(pageText, text))
                    {
                        isRequiredText = false;
                        break;
                    }
                }
            }

            // log event if no required text was found on page
            if (!isRequiredText && !Context.isLoadTest)
            {
                EventLogger.CRAWLER.info("Skipped processing (required text not found)", getCurrentUrlString());
            }
        }

        return isRequiredText;
    }

    private boolean isDisallowedText(final String pageText)
    {
        boolean isDisallowedText = false;

        // no restriction -> disallowed text condition cannot be fulfilled
        // otherwise check page
        if (!crawlerConfig.disallowedTexts.isEmpty())
        {
            if (StringUtils.isNotEmpty(pageText))
            {
                // check if at least one of the required texts is contained
                // req texts condition: ALL or ONE
                for (final String text : crawlerConfig.disallowedTexts.weightedList())
                {
                    if (RegExUtils.isMatching(pageText, text))
                    {
                        isDisallowedText = true;
                        break;
                    }
                }
            }

            // log event if disallowed text was found on page
            if (isDisallowedText && !Context.isLoadTest)
            {
                EventLogger.CRAWLER.info("Skipped processing (disallowed text found)", getCurrentUrlString());
            }
        }

        return isDisallowedText;
    }

    private String getCurrentUrlString()
    {
        return Context.getPage().getUrl().toExternalForm();
    }

    private static class KeyValue
    {
        private final String href;

        private final int depth;

        public KeyValue(final String href, final int depth)
        {
            this.href = href;
            this.depth = depth;
        }

        public String getHref()
        {
            return href;
        }

        /**
         * <pre>
         *            L0
         *            /\
         *           /  \
         *         L1a   L1b
         *         /\    /\
         *        /  \  /  \
         *       L2a  L2b  L2c
         *       /\   /\   /\
         *     .. .. .. .. .. ..
         * </pre>
         *
         * <p>
         * Example:<br>
         * Links TO level L0 have depth 0.<br>
         * Links ON level L0 (aka links TO level L1) have depth 1.<br>
         * Links ON level L1 (aka links TO level L2) have depth 2.<br>
         *
         * </p>
         *
         * @return
         */
        public int getDepth()
        {
            return depth;
        }
    }
}
