package com.xceptance.loadtest.api.util.crawler;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.common.util.ParseUtils;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.configuration.ConfigProbability;
import com.xceptance.loadtest.api.configuration.ConfigTimeRange;
import com.xceptance.loadtest.api.configuration.ConfigurationBuilder;
import com.xceptance.loadtest.api.configuration.EnumConfigList;
import com.xceptance.loadtest.api.configuration.LTProperties;
import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.util.Context;

/**
 * Base class for storing crawler flow configuration.
 * <ul>
 * <li>set patterns to include and/or exclude URLs</li>
 * <li>process page links in dependency of page text</li>
 * <li>limit crawler by depth, number of pages, or runtime</li>
 * <li>configure session drop (always, never, or when certain limit of pages or runtime is reached)</li>
 * </ul>
 *
 */
public class CrawlerConfig
{
    /**
     * The prefix for crawler properties
     */
    public static final String CRAWLER_PROPERTY_PREFIX = "crawler.";

    /** Crawler property key for URL exclusions */
    public static final String PROPERTY_EXCLUDE_URLS_CRAWLER = CRAWLER_PROPERTY_PREFIX + "excludeURLs";

    /** Common URL exclude filter property key */
    public static final String PROPERTY_EXCLUDE_URLS_GLOBAL = "com.xceptance.xlt.http.filter.exclude";

    /** Crawler property key for URL inclusions */
    public static final String PROPERTY_INCLUDE_URLS_CRAWLER = CRAWLER_PROPERTY_PREFIX + "includeURLs";

    /** Common URL include filter property key */
    public static final String PROPERTY_INCLUDE_URLS_GLOBAL = "com.xceptance.xlt.http.filter.include";

    public static final String PROPERTY_DROPSESSION = CRAWLER_PROPERTY_PREFIX + "dropSession";

    public static final String START_AT_KNOWN_PAGE = "KNOWN_PAGE";
    public static final String START_AT_SITEMAP = "SITEMAP";

    /** The total number of pages to crawl */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "maxPages")
    public int totalNumberOfPages;

    /** The depth of recursion */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "maxDepth")
    public int maxDepthOfRecursion;

    /** Maximum runtime of crawler in seconds */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "runtime")
    public ConfigTimeRange runtime;

    /** NoCache - add some fake parameter to URL to bypass server side caching */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "noCache")
    public boolean bypassCache;

    /** Returns the probability to request a non-existing page. */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "miss")
    public ConfigProbability forceLinkMissProbability;

    /** Returns the pattern string for the expected status code of miss pages. */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "miss.statusCode")
    public String expectedMissStatusCodePattern;

    /** Returns the {@link Pattern} for text expected to appear on miss pages. */
    @Property(key = CRAWLER_PROPERTY_PREFIX + "miss.expectedTextRegEx")
    public Pattern expectedMissTextPattern;

    @Property(key = CRAWLER_PROPERTY_PREFIX + "user.name")
    public String userName;

    @Property(key = CRAWLER_PROPERTY_PREFIX + "rule.robots")
    public boolean applyRobotsTxtRules;

    @Property(key = CRAWLER_PROPERTY_PREFIX + "rule.meta")
    public boolean applyMetaTagRules;

    @Property(key = CRAWLER_PROPERTY_PREFIX + "rule.linkrel")
    public boolean applyLinkRelRules;

    @EnumProperty(key = CRAWLER_PROPERTY_PREFIX + "requireText", clazz = String.class, required = false)
    public EnumConfigList<String> requireTexts;

    @EnumProperty(key = CRAWLER_PROPERTY_PREFIX + "disallowText", clazz = String.class, required = false)
    public EnumConfigList<String> disallowedTexts;

    /** Exclude patterns for URLs */
    public final Collection<String> excludePatterns = new HashSet<>();

    /** Include patterns for URLs */
    public final Collection<String> includePatterns = new HashSet<>();

    /** Session drop configuration */
    public DropSession dropSession;

    // FIXME what if ...
    // - no kown page yet
    // - no sitemap present
    // - robots txt deactivated via properties
    // - sitemap empty
    // - sitemap links forbidden somehow
    // move all these problems to the flow, clean the testcase
    @EnumProperty(key = CRAWLER_PROPERTY_PREFIX + "startAt", clazz = String.class, stopOnGap = false)
    public EnumConfigList<String> startAt;

    private CrawlerConfig()
    {
        // Create via fabric method only.
    }

    /**
     * Create new crawler configuration object.
     *
     * @return crawler configuration
     * @throws ParseException
     *             if loading the properties fails
     */
    public static CrawlerConfig create() throws ParseException
    {
        final CrawlerConfig config = ConfigurationBuilder.buildDefault(CrawlerConfig.class);
        config.loadExcludeFilters();
        config.loadIncludefilters();
        config.initializeSessionDropping();
        return config;
    }

    protected void initializeSessionDropping() throws ParseException
    {
        // Get property value and check for keywords 'always', 'never', and
        // 'every'
        final String prop = Context.configuration().properties.getProperty(PROPERTY_DROPSESSION);
        if (StringUtils.isNotBlank(prop))
        {
            if (prop.equals("always"))
            {
                dropSession = dropSession().always();
            }
            else if (prop.equals("never"))
            {
                dropSession = dropSession().never();
            }
            else if (prop.startsWith("every"))
            {
                // Extract sub value and convert to time (or pages respectively)
                final String interval = StringUtils.substringAfter(prop, " ").trim();
                try
                {
                    // Unit is SECONDS
                    final int seconds = ParseUtils.parseTimePeriod(interval);
                    dropSession = dropSession().every(seconds, DropSession.DropUnit.SECONDS);
                }
                catch (final Exception e)
                {
                    // Unit is PAGES
                    final String pagesRAW = RegExUtils.getFirstMatch(interval, "^(\\d+)p$", 1);
                    if (pagesRAW != null)
                    {
                        final int pages = Integer.valueOf(pagesRAW);
                        dropSession = dropSession().every(pages, DropSession.DropUnit.PAGES);
                    }
                    else
                    {
                        // Unknown time format
                        throw new ParseException(String.format("Unknown format of drop session period '%s'.", prop), 0);
                    }
                }
            }
            else
            {
                // Unknown value
                throw new ParseException(String.format("Unknown value for drop session: '%s'.", prop), 0);
            }
        }
    }

    /**
     * Load exclude filters from properties.
     */
    protected void loadExcludeFilters()
    {
        final LTProperties properties = Context.configuration().properties;
        addFilter(properties.getProperty(PROPERTY_EXCLUDE_URLS_GLOBAL), excludePatterns);
        addFilter(properties.getProperty(PROPERTY_EXCLUDE_URLS_CRAWLER), excludePatterns);
    }

    /**
     * Load include filters from properties.
     */
    protected void loadIncludefilters()
    {
        final LTProperties properties = Context.configuration().properties;
        addFilter(properties.getProperty(PROPERTY_INCLUDE_URLS_GLOBAL), includePatterns);
        addFilter(properties.getProperty(PROPERTY_INCLUDE_URLS_CRAWLER), includePatterns);
    }

    /**
     * Add pattern strings from property to the given collection.
     *
     * @param prop
     *            property key
     * @param filterCollection
     *            filter collection destination
     */
    private void addFilter(final String prop, final Collection<String> filterCollection)
    {
        if (StringUtils.isNotBlank(prop))
        {
            // Patterns are separated by whitespace
            final String[] patterns = StringUtils.split(prop);
            for (final String pattern : patterns)
            {
                filterCollection.add(pattern);
            }
        }
    }

    /**
     * Initialize session drop configuration.
     *
     * @return initialized session dropper
     */
    private DropSession dropSession()
    {
        return new DropSession();
    }
}
