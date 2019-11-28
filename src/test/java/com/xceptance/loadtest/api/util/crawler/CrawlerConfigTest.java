package com.xceptance.loadtest.api.util.crawler;

import java.text.ParseException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.ConfigBooleanTest1;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequestTest;
import com.xceptance.xlt.api.util.XltProperties;

import util.TestUtils;

/**
 * <pre>
 * CrawlerConfig crawlerConfig = new CrawlerConfig().loadDefaults()
 *                 .includeUrlPattern(&quot;&circ;https?://foo.bar.net&quot;) // stack patterns
 *                 .excludeUrlPattern(&quot;badUrl&quot;) // stack patterns
 *                 .depthMax(3)
 *                 .disallowText(&quot;don't process page containing this text&quot;)
 *                 .requireText(&quot;this text is required to process page&quot;)
 *                 .numberOfPages(250)
 *                 .dropSession().every(5).minutes() // drop session (last one
 *                                                   // wins)
 *                 .dropSession().every(300).seconds()
 *                 .dropSession().every(10).pages()
 *                 .dropSession().always()
 *                 .dropSession().never()
 *                 .runMax(10).minutes() // run max (last one wins)
 *                 .runMax(600).seconds()
 *                 .noCache(true);
 * </pre>
 */
// @RunWith(PowerMockRunner.class)
// @PrepareForTest({
// CrawlerConfig.class
// })
public class CrawlerConfigTest
{
    static final String KEY = "CrawlerConfigTest";

    @BeforeClass
    public static void init() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        Context.createContext(properties, HttpRequestTest.class.getSimpleName(), HttpRequestTest.class.getName(), SiteSupplier.randomSite().get());
    }

    /*
     * DropSession
     */
    @Test
    public void dropSession_never() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "never");
            final CrawlerConfig config = CrawlerConfig.create();

            Assert.assertEquals(DropSession.DROP_NEVER,
                            TestUtils.<Integer>getFieldValue(config.dropSession, "threshold").intValue());
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }
    }

    @Test
    public void dropSession_always() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "always");
            final CrawlerConfig config = CrawlerConfig.create();

            Assert.assertEquals(DropSession.DROP_ALWAYS,
                            TestUtils.<Integer>getFieldValue(config.dropSession, "threshold").intValue());
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }

    }

    @Test
    public void dropSession_every_seconds() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every 1s");
            final CrawlerConfig config = CrawlerConfig.create();

            final DropSession dropSession = config.dropSession;
            Assert.assertEquals(1000, TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
            Assert.assertEquals(DropSession.DropUnit.SECONDS, TestUtils.getFieldValue(dropSession, "unit"));
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }

    }

    @Test
    public void dropSession_every_combined() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every 1h1m1s");
            final CrawlerConfig config = CrawlerConfig.create();

            final DropSession dropSession = config.dropSession;
            Assert.assertEquals(3661000, TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
            Assert.assertEquals(DropSession.DropUnit.SECONDS, TestUtils.getFieldValue(dropSession, "unit"));
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }

    }

    @Test
    public void dropSession_every_1_pages() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every 1p");
            final CrawlerConfig config = CrawlerConfig.create();

            final DropSession dropSession = config.dropSession;
            Assert.assertEquals(1, TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
            Assert.assertEquals(DropSession.DropUnit.PAGES, TestUtils.getFieldValue(dropSession, "unit"));
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }

    }

    @Test(expected = ParseException.class)
    public void dropSession_unknown() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "foo");
            CrawlerConfig.create();
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }

    }

    @Test(expected = ParseException.class)
    public void dropSession_negative_seconds() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every -10s");
            CrawlerConfig.create();
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }
    }

    @Test(expected = ParseException.class)
    public void dropSession_negative_pages() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every -10p");
            CrawlerConfig.create();
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }
    }

    @Test(expected = ParseException.class)
    public void dropSession_unknown_unit() throws ParseException
    {
        final String property = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_DROPSESSION);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, "every 10x");
            CrawlerConfig.create();
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_DROPSESSION, property);
        }
    }

    /*
     * Exclude Patterns
     */

    @Test
    public void excludePattern() throws ParseException
    {
        final String property1 = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_GLOBAL);
        final String property2 = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_CRAWLER);
        try
        {

            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_GLOBAL, "foo1 bar1");
            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_CRAWLER, "baz1 bum1");

            final CrawlerConfig config = CrawlerConfig.create();

            Assert.assertTrue(config.excludePatterns.contains("foo1"));
            Assert.assertTrue(config.excludePatterns.contains("bar1"));
            Assert.assertTrue(config.excludePatterns.contains("baz1"));
            Assert.assertTrue(config.excludePatterns.contains("bum1"));

        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_GLOBAL, property1);
            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_CRAWLER, property2);
        }

    }

    /*
     * Include Patterns
     */

    @Test
    public void includePattern() throws ParseException
    {
        final String property1 = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_GLOBAL);
        final String property2 = XltProperties.getInstance().getProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_CRAWLER);
        try
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_INCLUDE_URLS_GLOBAL, "foo2 bar2");
            setCrawlerProperty(CrawlerConfig.PROPERTY_INCLUDE_URLS_CRAWLER, "baz2 bum2");

            final CrawlerConfig config = CrawlerConfig.create();

            Assert.assertTrue(config.includePatterns.contains("foo2"));
            Assert.assertTrue(config.includePatterns.contains("bar2"));
            Assert.assertTrue(config.includePatterns.contains("baz2"));
            Assert.assertTrue(config.includePatterns.contains("bum2"));
        }
        finally
        {
            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_GLOBAL, property1);
            setCrawlerProperty(CrawlerConfig.PROPERTY_EXCLUDE_URLS_CRAWLER, property2);
        }

    }

    private void setCrawlerProperty(final String key, final String value)
    {
        XltProperties.getInstance().setProperty(key, value);
        Context.get().configuration.properties.addProperties(Optional.of(XltProperties.getInstance().getProperties()));
    }
}

class CrawlerConfig1
{
    @Property(key = CrawlerConfigTest.KEY + "1a")
    public boolean foo1a;

    @Property(key = ConfigBooleanTest1.KEY + "1b")
    public boolean foo1b;
}
