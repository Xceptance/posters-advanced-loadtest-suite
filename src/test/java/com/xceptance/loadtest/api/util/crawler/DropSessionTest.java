package com.xceptance.loadtest.api.util.crawler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.util.XltRandom;

import util.TestUtils;

public class DropSessionTest
{
    private static final int DUMMY_PAGE_COUNT = 123;

    private long DUMMY_LAST_DROP_TIME;

    private DropSession dropSession;


    @BeforeClass
    public static void initClass() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        Context.createContext(properties, DropSessionTest.class.getSimpleName(), DropSessionTest.class.getName(), SiteSupplier.randomSite().get());
    }

    @Before
    public void init() throws Exception
    {
        // insert action, so we can get a page from the context
        final PageAction<DropSessionTest> a = new PageAction<DropSessionTest>()
        {
            @Override
            protected void doExecute() throws Exception
            {
            }

            @Override
            protected void postValidate() throws Exception
            {
            }
        };
        TestUtils.invokeMethod(Context.get(), "setCurrentActionInternal", new Class<?>[] { PageAction.class }, a);

        Context.setCurrentPage(TestUtils.getFakePage());
        DUMMY_LAST_DROP_TIME = System.currentTimeMillis();
        dropSession = new DropSession();
        fakeSession();
    }

    /*
     * configure
     */

    @Test
    public void dropSesissonAlways() throws Exception
    {
        dropSession.always();
        Assert.assertEquals(DropSession.DROP_ALWAYS,
                        TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
    }

    @Test
    public void dropSesissonNever() throws Exception
    {
        dropSession.never();
        Assert.assertEquals(DropSession.DROP_NEVER,
                        TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropSesissonEverySecondsNegative() throws Exception
    {
        dropSession.every(-1, DropSession.DropUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dropSesissonEverySecondsZero() throws Exception
    {
        dropSession.every(0, DropSession.DropUnit.SECONDS);
    }

    @Test
    public void dropSesissonEverySecondsPositive() throws Exception
    {
        dropSession.every(1, DropSession.DropUnit.SECONDS);
        Assert.assertEquals(1000, TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
        Assert.assertEquals(DropSession.DropUnit.SECONDS, TestUtils.getFieldValue(dropSession, "unit"));
    }

    @Test
    public void dropSesissonEveryPages() throws Exception
    {
        dropSession.every(1, DropSession.DropUnit.PAGES);
        Assert.assertEquals(1, TestUtils.<Integer>getFieldValue(dropSession, "threshold").intValue());
        Assert.assertEquals(DropSession.DropUnit.PAGES, TestUtils.getFieldValue(dropSession, "unit"));
    }

    /*
     * drop the session
     */

    @Test
    public void dropSession() throws Exception
    {
        TestUtils.invokeMethod(dropSession, "dropSession");
        assertSessionDropped(dropSession);
    }

    /*
     * update
     */

    @Test
    public void updateAlways() throws Exception
    {
        TestUtils.setFieldValue(dropSession, "threshold", TestUtils.getFieldValue(dropSession, "DROP_ALWAYS"));
        for (int i = 0; i < XltRandom.nextInt(10); i++)
        {
            TestUtils.invokeMethod(dropSession, "update");
            assertSessionDropped(dropSession);
            fakeSession();
        }
    }

    @Test
    public void updateNever() throws Exception
    {
        TestUtils.setFieldValue(dropSession, "threshold", TestUtils.getFieldValue(dropSession, "DROP_NEVER"));
        for (int i = 0; i < XltRandom.nextInt(10); i++)
        {
            TestUtils.invokeMethod(dropSession, "update");
            assertSessionNotDropped(dropSession);
        }
    }

    @Test
    public void updatePageCountIncrease() throws Exception
    {
        // prepare to not drop session
        dropSession.every(DUMMY_PAGE_COUNT + 100, DropSession.DropUnit.PAGES);

        // get page count before and after update
        final int pageCountBefore = TestUtils.getFieldValue(dropSession, "pageCount");
        TestUtils.invokeMethod(dropSession, "update");
        final int pageCountAfter = TestUtils.getFieldValue(dropSession, "pageCount");

        // page count should be increased by 1 after update
        Assert.assertEquals(pageCountBefore + 1, pageCountAfter);
    }

    @Test
    public void updatePageCountLowerThanThreshold() throws Exception
    {

        dropSession.every(DUMMY_PAGE_COUNT + 100, DropSession.DropUnit.PAGES);
        TestUtils.invokeMethod(dropSession, "update");

        assertSessionNotDropped(dropSession);
    }

    @Test
    public void updatePageCountOneBeforeThreshold() throws Exception
    {

        dropSession.every(DUMMY_PAGE_COUNT + 1, DropSession.DropUnit.PAGES);
        TestUtils.invokeMethod(dropSession, "update");

        assertSessionDropped(dropSession);
    }

    @Test
    public void updatePageCountEqualsThreshold() throws Exception
    {

        dropSession.every(DUMMY_PAGE_COUNT, DropSession.DropUnit.PAGES);
        TestUtils.invokeMethod(dropSession, "update");

        assertSessionDropped(dropSession);
    }

    @Test
    public void updatePageCountHigherThreshold() throws Exception
    {

        dropSession.every(DUMMY_PAGE_COUNT - 1, DropSession.DropUnit.PAGES);
        TestUtils.invokeMethod(dropSession, "update");

        assertSessionDropped(dropSession);
    }

    @Test
    public void updateRuntimeLowerThanThreshold() throws Exception
    {
        prepareTimeBasedDrop(100);

        // invoke
        TestUtils.invokeMethod(dropSession, "update");

        // assert
        assertSessionNotDropped(dropSession);
    }

    @Test
    public void updateRuntimeEqualsThreshold() throws Exception
    {
        DUMMY_LAST_DROP_TIME -= 100 * 1000;
        fakeSession();
        prepareTimeBasedDrop(100);

        // invoke
        TestUtils.invokeMethod(dropSession, "update");

        // assert
        assertSessionDropped(dropSession, DUMMY_LAST_DROP_TIME);
    }

    @Test
    public void updateRuntimeHigherThanThreshold() throws Exception
    {
        DUMMY_LAST_DROP_TIME -= 110 * 1000;
        fakeSession();

        prepareTimeBasedDrop(100);
        fakeSession();

        // invoke
        TestUtils.invokeMethod(dropSession, "update");

        // assert
        assertSessionDropped(dropSession, DUMMY_LAST_DROP_TIME);
    }

    /**
     *
     * sets the threshold to 100 seconds
     *
     * @param thresholdSeconds
     *            threshold in seconds
     * @return DropCookies listener (check {@link DropCookies#triggered})
     * @throws Exception
     */
    private void prepareTimeBasedDrop(final int thresholdSeconds) throws Exception
    {
        // prepare : threshold = DUMMY_LAST_DROP_TIME
        dropSession.every(thresholdSeconds, DropSession.DropUnit.SECONDS);
    }

    /*
     * UTILS
     */

    private void fakeSession() throws Exception
    {
        TestUtils.setFieldValue(dropSession, "lastDropTime", DUMMY_LAST_DROP_TIME);
        TestUtils.setFieldValue(dropSession, "pageCount", DUMMY_PAGE_COUNT);
        Context.getPage().getWebClient().getCookieManager().addCookie(new Cookie("Test", "test", "test"));
    }

    private <T> void assertSessionDropped(final DropSession dropCookies) throws Exception
    {
        assertSessionDropped(dropCookies, Long.valueOf(System.currentTimeMillis() / 1000).intValue());
    }

    private <T> void assertSessionDropped(final DropSession dropCookies, final long nowSeconds) throws Exception
    {
        final long lastDropTime = TestUtils.<Long>getFieldValue(dropSession, "lastDropTime");
        final int pageCount = TestUtils.<Integer>getFieldValue(dropSession, "pageCount");

        Assert.assertTrue("Session not dropped, lastTropTime wrong", lastDropTime >= nowSeconds - 10000);
        Assert.assertEquals("Session not dropped, pagecount wrong", 0, pageCount);
        Assert.assertTrue("Session not dropped, cookies still available", Context.getPage().getWebClient().getCookieManager().getCookies().isEmpty());

    }

    private <T> void assertSessionNotDropped(final DropSession dropCookies) throws Exception
    {
        final long lastDropTime = TestUtils.<Long>getFieldValue(dropSession, "lastDropTime");
        final int pageCount = TestUtils.<Integer>getFieldValue(dropSession, "pageCount");

        Assert.assertEquals(DUMMY_LAST_DROP_TIME, lastDropTime);
        Assert.assertTrue(pageCount >= DUMMY_PAGE_COUNT);
        Assert.assertFalse("Session dropped, cookies empty", Context.getPage().getWebClient().getCookieManager().getCookies().isEmpty());
    }
}
