package com.xceptance.loadtest.headless.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DOMUtilsTest;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.XltProperties;

import util.TestUtils;

public class PageTest
{
    @BeforeClass
    public static void init() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        Context.createContext(properties, PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.randomSite().get());
    }

    @Before
    public void setUp() throws Throwable
    {
        final PageAction<DOMUtilsTest> action = new PageAction<DOMUtilsTest>()
        {
            @Override
            protected void postValidate() throws Exception
            {
            }

            @Override
            protected void doExecute() throws Exception
            {
            }
        };

        TestUtils.invokeMethod(Context.get(), "setCurrentActionInternal", new Class<?>[] { PageAction.class }, action);

        Context.setCurrentPage(TestUtils.getFakePage());
        Context.get().configuration.properties.addProperties(Optional.of(XltProperties.getInstance().getProperties()));
//        Context.get().configuration.consentAsk = false;
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. Using the body as parent.
     * The wanted element is in the page already. It's expected that the element
     * is returned only. Nothing will be created or modified.
     *
     * @throws Exception
     */
    @Test
    public void testGetOrCreateHit() throws Exception
    {
        // init element to find
        final HtmlElement parent = Page.getBody();
        final HtmlElement findMe = HtmlPageUtils.createHtmlElement("div", parent);
        findMe.setAttribute("id", "test");

        // search for existing element
        final HtmlElement getOrCreateElement = Page.getOrCreateByID("test");

        Assert.assertNotNull("Element not found.", getOrCreateElement);
        Assert.assertEquals("Unexpected element ID.", "test", getOrCreateElement.getId());
        Assert.assertSame("Resulting element is not the initial one.", findMe, getOrCreateElement);
        Assert.assertSame("Unexpected element count (falsely creation of element(s)).", 1, Page.find().byCss("div").count());
        Assert.assertSame("Unexpected parent.", parent, getOrCreateElement.getParentNode());
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. Using the body as parent.
     * The wanted element needs to be created in the page.
     *
     * @throws Exception
     */
    @Test
    public void testGetOrCreateMiss() throws Exception
    {
        final HtmlElement parent = Page.getBody();

        // search for non existing element
        final HtmlElement getOrCreateElement = Page.getOrCreateByID("test");

        Assert.assertNotNull("Element not created.", getOrCreateElement);
        Assert.assertEquals("Unexpected element ID.", "test", getOrCreateElement.getId());
        Assert.assertSame("Unexpected element count (falsely creation of element(s)).", 1, Page.find().byCss("div").count());
        Assert.assertSame("Unexpected parent.", parent, getOrCreateElement.getParentNode());
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. Using another element
     * (not body) as parent. The wanted element is already in the page. It's
     * expected that the element is returned only. Nothing will be created or
     * modified.
     *
     * @throws Exception
     */
    @Test
    public void testGetOrCreateHitParent() throws Exception
    {
        // create the parent
        final HtmlElement parent = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        parent.setAttribute("id", "parent");

        // init element to find
        final HtmlElement findMe = HtmlPageUtils.createHtmlElement("div", parent);
        findMe.setAttribute("id", "test");

        // search for existing element
        final HtmlElement getOrCreateElement = Page.getOrCreateByID("test", parent);

        Assert.assertNotNull("No element found nor created.", getOrCreateElement);
        Assert.assertEquals("Unexpected element ID.", "test", getOrCreateElement.getId());
        Assert.assertSame("Resulting element is not the initial one.", findMe, getOrCreateElement);
        Assert.assertSame("Unexpected element count (falsely creation of element(s)).", 2, Page.find().byCss("div").count());
        Assert.assertSame("Unexpected parent.", parent, getOrCreateElement.getParentNode());
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. Using another div as
     * parent, when the wanted element needs to be created in the page.
     *
     * @throws Exception
     */
    @Test
    public void testGetOrCreateMissParent() throws Exception
    {
        final HtmlElement parent = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        parent.setAttribute("id", "parent");

        // search for non existing element
        final HtmlElement getOrCreateElement = Page.getOrCreateByID("test", parent);

        Assert.assertNotNull("No element created.", getOrCreateElement);
        Assert.assertEquals("Unexpected element ID.", "test", getOrCreateElement.getId());
        Assert.assertSame("Unexpected element count (falsely creation of element(s)).", 2, Page.find().byCss("div").count());
        Assert.assertSame("Unexpected parent.", parent, getOrCreateElement.getParentNode());
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. There are multiple
     * elements with given ID.
     *
     * @throws Exception
     */
    @Test(expected = Throwable.class)
    public void testGetOrCreate_multipleIDs_explicitParent() throws Exception
    {
        final HtmlElement element1 = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        element1.setAttribute("id", "test");

        final HtmlElement element2 = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        element2.setAttribute("id", "test");

        final HtmlElement parent = Page.getBody();

        Page.getOrCreateByID("test", parent);
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. There are multiple
     * elements with given ID.
     *
     * @throws Exception
     */
    @Test(expected = Throwable.class)
    public void testGetOrCreate_multipleIDs() throws Exception
    {
        final HtmlElement element1 = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        element1.setAttribute("id", "test");

        final HtmlElement element2 = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        element2.setAttribute("id", "test");

        Page.getOrCreateByID("test");
    }

    /**
     * Checks the {@link Page#getOrCreateByID} method. Element exists initially,
     * but parent is incorrect.
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public void testGetOrCreate_unexpectedParent() throws Exception
    {
        final HtmlElement parent = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        parent.setAttribute("id", "parent");

        final HtmlElement findMe = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        findMe.setAttribute("id", "test");

        Page.getOrCreateByID("test", parent);
    }
}
