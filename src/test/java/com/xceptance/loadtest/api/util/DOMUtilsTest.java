package com.xceptance.loadtest.api.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.XltProperties;

import util.TestUtils;


public class DOMUtilsTest
{
    @BeforeClass
    public static void init() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        Context.createContext(properties, DOMUtilsTest.class.getSimpleName(), DOMUtilsTest.class.getName(), SiteSupplier.randomSite().get());
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

    @Test
    public void testAddClass()
    {
        // init element to find
        final HtmlElement parent = Page.getBody();
        final HtmlElement e = HtmlPageUtils.createHtmlElement("div", parent);

        e.setAttribute("class", "");
        Assert.assertEquals("test", DOMUtils.addClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "test");
        Assert.assertEquals("test", DOMUtils.addClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo");
        Assert.assertEquals("foo test", DOMUtils.addClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo bar");
        Assert.assertEquals("foo bar test", DOMUtils.addClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo test bar");
        Assert.assertEquals("foo bar test", DOMUtils.addClass(e, "test").getAttribute("class"));

        e.removeAttribute("class");
        Assert.assertEquals("test", DOMUtils.addClass(e, "test").getAttribute("class"));
    }

    @Test
    public void testRemoveClass()
    {
        // init element to find
        final HtmlElement parent = Page.getBody();
        final HtmlElement e = HtmlPageUtils.createHtmlElement("div", parent);

        e.setAttribute("class", "");
        Assert.assertEquals("", DOMUtils.removeClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo");
        Assert.assertEquals("foo", DOMUtils.removeClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo bar");
        Assert.assertEquals("foo bar", DOMUtils.removeClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo test bar");
        Assert.assertEquals("foo bar", DOMUtils.removeClass(e, "test").getAttribute("class"));

        e.setAttribute("class", "foo test bar test");
        Assert.assertEquals("foo bar", DOMUtils.removeClass(e, "test").getAttribute("class"));

        e.removeAttribute("class");
        Assert.assertEquals("", DOMUtils.removeClass(e, "test").getAttribute("class"));
    }

    @Test
    public void testHasClasses()
    {
        // init element to find
        final HtmlElement parent = Page.getBody();
        final HtmlElement e = HtmlPageUtils.createHtmlElement("div", parent);

        e.setAttribute("class", "");
        Assert.assertFalse(DOMUtils.hasClasses(e, "test"));

        e.setAttribute("class", "test");
        Assert.assertTrue(DOMUtils.hasClasses(e, "test"));

        e.setAttribute("class", "test foo");
        Assert.assertTrue(DOMUtils.hasClasses(e, "test"));

        e.setAttribute("class", "test foo");
        Assert.assertTrue(DOMUtils.hasClasses(e, "test", "foo"));

        e.setAttribute("class", "foo test");
        Assert.assertTrue(DOMUtils.hasClasses(e, "test", "foo"));

        e.setAttribute("class", "bar test");
        Assert.assertFalse(DOMUtils.hasClasses(e, "test", "foo"));

        e.setAttribute("class", "foo test bar");
        Assert.assertTrue(DOMUtils.hasClasses(e, "test"));

        e.setAttribute("class", "foo bar");
        Assert.assertFalse(DOMUtils.hasClasses(e, "test"));

        e.removeAttribute("class");
        Assert.assertFalse(DOMUtils.hasClasses(e, "test"));
    }

    @Test
    public void isSubnodeOf()
    {
        /*
         * x and y are siblings. z is child of y.
         */

        final HtmlElement x = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        x.setId("x");

        final HtmlElement y = HtmlPageUtils.createHtmlElement("div", Page.getBody());
        y.setId("y");

        final HtmlElement z = HtmlPageUtils.createHtmlElement("div", y);
        z.setId("z");

        // all elements are sub nodes of the body, no matter what DOM level they
        // are
        Assert.assertTrue(DOMUtils.isSubnodeOf(x, Page.getBody()));
        Assert.assertTrue(DOMUtils.isSubnodeOf(y, Page.getBody()));
        Assert.assertTrue(DOMUtils.isSubnodeOf(z, Page.getBody()));

        // z is also direct child of y
        Assert.assertTrue(DOMUtils.isSubnodeOf(z, y));

        // z is not a descendant of x
        Assert.assertFalse(DOMUtils.isSubnodeOf(z, x));

        // x is not a descendant of y
        Assert.assertFalse(DOMUtils.isSubnodeOf(x, y));

        // y is not a descendant of z
        Assert.assertFalse(DOMUtils.isSubnodeOf(y, z));
    }
}
