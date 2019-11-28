package com.xceptance.loadtest.api.hpu;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.helpers.AttributesImpl;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.InputElementFactory;

/**
 * Test the implementation of {@link HPU}.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class HPUTest
{
    private WebClient webClient;

    @Before
    public void init()
    {
        webClient = new WebClient();
    }

    @After
    public void cleanup()
    {
        webClient.close();
    }

    /*
     * Find
     */

    /**
     * Create unasserted finder.
     */
    @Test
    public void testFind()
    {
        HPU.find();
    }

    /*
     * In
     */

    /**
     * Set an {@link HtmlPage} as lookup base.
     */
    @Test
    public void testFindInPage() throws Throwable
    {
        HPU.find().in(getHtmlPage(""));
    }

    /**
     * Set an {@link HtmlPage} as lookup base. In case of <code>null</code> an
     * exception is expected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindInPageNull() throws Throwable
    {
        HPU.find().in((HtmlPage) null);
    }

    /**
     * Set an {@link HtmlElement} as lookup base.
     */
    @Test
    public void testFindInElement() throws Throwable
    {
        HPU.find().in(getHtmlElement());
    }

    /**
     * Set an {@link HtmlElement} as lookup base. In case of <code>null</code>
     * an exception is expected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindInElementNull()
    {
        HPU.find().in((HtmlElement) null);
    }

    /*
     * By
     */

    /**
     * Test valid XPath expression (total path).
     *
     * @throws Throwable
     */
    @Test
    public void testByXPathExpression() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("/html/body/foo").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test valid XPath expression (function).
     *
     * @throws Throwable
     */
    @Test
    public void testByXPathExpressionFunction() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("id('foo')").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test invalid XPath expression (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = RuntimeException.class)
    public void testByXPathExpressionInvalid() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("#foo").raw();
    }

    /**
     * Test empty XPath expression (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByXPathExpressionEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("").raw();
    }

    /**
     * Test XPath expression set to <code>null</code> (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByXPathExpressionNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath(null).raw();
    }

    /**
     * Test valid CSS locator.
     *
     * @throws Throwable
     */
    @Test
    public void testByCssExpression() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byCss("#foo").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test invalid CSS locator (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = RuntimeException.class)
    public void testByCssExpressionInvalid() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss("/html/body").raw();
    }

    /**
     * Test empty CSS locator (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByCssExpressionEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss("").raw();
    }

    /**
     * Test CSS locator set to <code>null</code> (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByCssExpressionNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss(null).raw();
    }

    /**
     * Test valid ID String.
     *
     * @throws Throwable
     */
    @Test
    public void testByIdString() throws Throwable
    {
        // HPU.find().in(getHtmlPage("")).byId("foo").raw();
    }

    /**
     * Test empty ID String (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByIdStringEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byId("").raw();
    }

    /**
     * Test ID String set to <code>null</code> (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByIdStringNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byId(null).raw();
    }

    /*
     * Lookup strategies
     */

    /**
     * Test XPath strategy finds the right element (exactly 1).
     *
     * @throws Throwable
     */
    @Test
    public void testXPathOneResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@name='foo']")
                        .all();
        Assert.assertEquals("Exactly 1 result element expected.", 1, all.size());
    }

    /**
     * Test XPath strategy finds the right elements (exactly 2).
     *
     * @throws Throwable
     */
    @Test
    public void testXPathManyResults() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@name='foo']")
                        .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Test CSS strategy finds the right element (exactly 1).
     *
     * @throws Throwable
     */
    @Test
    public void testCssOneResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageOne())
                        .byCss(".foo")
                        .all();
        Assert.assertEquals("Exactly 1 result element expected.", 1, all.size());
    }

    /**
     * Test CSS strategy finds the right elements (exactly 2).
     *
     * @throws Throwable
     */
    @Test
    public void testCssManyResults() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageMany())
                        .byCss(".foo")
                        .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Test if ID strategy finds the correct element.
     *
     * @throws Throwable
     */
    @Test
    public void testIdStrategieResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo")
                        .all();
        Assert.assertEquals("Wrong number of results", all.size(), 1);
        Assert.assertEquals("Wrong element returned", all.get(0).getAttribute("nr"), "1");
    }

    /**
     * Test if ID strategy brakes for multiple equal ids (in Dev Mode).
     *
     * @throws Throwable
     */
    @Test
    public void testIdException() throws Throwable
    {
        try
        {
            HPU.find()
                            .in(getHtmlPageMany())
                            .byId("foo")
                            .all();
            Assert.fail("IllegalStateException expected");
        }
        catch (final IllegalStateException e)
        {
            Assert.assertEquals("Id foo is used multiple times in page.", e.getMessage());
        }
    }

    /**
     * Test if ID strategy accepts multiple equal ids (in loadtest mode).
     *
     * @throws Throwable
     */
    @Test
    public void testIdExceptionLoadTestMode() throws Throwable
    {
        final Field devModeField = By.class.getDeclaredField("isDevMode");
        devModeField.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(devModeField, devModeField.getModifiers() & ~Modifier.FINAL);

        devModeField.setBoolean(null, false);

        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageMany())
                        .byId("foo")
                        .all();
        Assert.assertEquals("Wrong number of results", all.size(), 1);
        Assert.assertEquals("Wrong element returned", all.get(0).getAttribute("nr"), "1");

        devModeField.setBoolean(null, true);

    }

    /**
     * Test if XPath ID function accepts multiple equal ids (in load test Mode).
     *
     * @throws Throwable
     */
    @Test
    public void testXPathIdFunctionMultipleIdsExceptionLoadTestMode() throws Throwable
    {
        final Field devModeField = By.class.getDeclaredField("isDevMode");
        devModeField.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(devModeField, devModeField.getModifiers() & ~Modifier.FINAL);

        devModeField.setBoolean(null, false);

        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("id('foo')")
                        .all();
        Assert.assertEquals("Wrong number of results", all.size(), 1);
        Assert.assertEquals("Wrong element returned", all.get(0).getAttribute("nr"), "1");

        devModeField.setBoolean(null, true);
    }

    /**
     * Test if XPath ID function brakes for multiple equal ids (in Dev Mode).
     *
     * @throws Throwable
     */
    @Test
    public void testXPathIdFunctionMultipleIdsException2() throws Throwable
    {
        try
        {
            HPU.find()
                            .in(getHtmlPageMany())
                            .byXPath("id(\"foo\")")
                            .all();
            Assert.fail("Excetion");
        }
        catch (final IllegalStateException e)
        {
            Assert.assertEquals("Id foo is used multiple times in page.", e.getMessage());
        }
    }

    /**
     * Test if XPath ID function brakes for multiple equal ids (in Dev Mode).
     *
     * @throws Throwable
     */
    @Test
    public void testXPathIdFunctionMultipleIdsException() throws Throwable
    {
        try
        {
            HPU.find()
                            .in(getHtmlPageMany())
                            .byXPath("id('foo')")
                            .all();
            Assert.fail("Excetion");
        }
        catch (final IllegalStateException e)
        {
            Assert.assertEquals("Id foo is used multiple times in page.", e.getMessage());
        }
    }

    /*
     *
     * Results access
     */

    /*
     * Raw
     */

    /**
     * Access unparsed results (many).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsRawMany() throws Throwable
    {
        final List<?> raw = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']")
                        .raw();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, raw.size());
    }

    /**
     * Access unparsed results (none).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsRawNothing() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /*
     * All
     */

    /**
     * Access cast results (many).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsAllMany() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']")
                        .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Access cast results (none).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsAllNothing() throws Throwable
    {
        final List<HtmlElement> all = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").all();
        Assert.assertTrue("List is expected to be empty.", all.isEmpty());
    }

    /*
     * Existence
     */

    /**
     * Test element existence check in case of valid results.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsExists() throws Throwable
    {
        final boolean exists = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").exists();
        Assert.assertTrue("Existing element not found.", exists);
    }

    /**
     * Test element existence check in case of no results.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsExistsNot() throws Throwable
    {
        final boolean exists = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").exists();
        Assert.assertFalse("Element not found falsely.", exists);
    }

    /*
     * First
     */

    /**
     * Get first element out of many.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsFirstMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").first();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get first element out of one.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsFirstOne() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").first();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get first element out of none (<code>null</code>).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsFirstNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").first();
        Assert.assertNull("Existing element not found.", element);
    }

    /*
     * Last
     */

    /**
     * Get last element out of many.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsLastMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").last();
        Assert.assertEquals("Existing element not found.", "2", element.getAttribute("nr"));
    }

    /**
     * Get last element out of one.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsLastOne() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").last();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get last element out of none (<code>null</code>).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsLastNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").last();
        Assert.assertNull("Existing element not found.", element);
    }

    /*
     * Count
     */

    /**
     * Count all elements (2).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsCountMany() throws Throwable
    {
        final int count = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']")
                        .count();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, count);
    }

    /**
     * Count all elements (1).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsCountOne() throws Throwable
    {
        final int count = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").count();
        Assert.assertEquals("Exactly 1 result element expected.", 1, count);
    }

    /**
     * Count all elements (0).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsCountNothing() throws Throwable
    {
        final int count = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").count();
        Assert.assertEquals("Exactly 0 result element expected.", 0, count);
    }

    /*
     * Is count
     */

    /**
     * There are not pre-checks anymore to save resources, because this is checked a millions times
     * and has always the same result, hence we just say nay.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountNegative() throws Throwable
    {
        Assert.assertFalse(
                        HPU.find()
                                        .in(getHtmlPage(""))
                                        .byXPath("//*[@class='foo']")
                                        .isCount(-1));
    }

    /**
     * Number of results matches expected number.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountOne() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").isCount(1);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /**
     * Number of results does not match expected number.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountOneFalse() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").isCount(1);
        Assert.assertFalse("Assertted count should be false.", isCount);
    }

    /**
     * Number of results matches expected number when we have many results.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountMany() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPageMany()).byXPath("//*[@class='foo']").isCount(2);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /**
     * Number of results matches expected number when we have no results.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountNone() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").isCount(0);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /*
     * Is count (min/max)
     */

    /**
     * One result, both range limits are equal, number of results is in given
     * range.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOne() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']")
                        .isCount(1, 1);
        Assert.assertTrue(isCount);
    }

    /**
     * Many results, number of results is in given range.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOneOrMany() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']")
                        .isCount(1, 2);
        Assert.assertTrue(isCount);
    }

    /**
     * Many results, lower limit is <code>0</code>, number of results is in
     * given range.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeNoneOrMany() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']")
                        .isCount(0, 2);
        Assert.assertTrue(isCount);
    }

    /**
     * No result, both range limits are <code>0</code>
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeNone() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']")
                        .isCount(0, 0);
        Assert.assertTrue(isCount);
    }

    /**
     * One result, lower range limit is higher than number of results (must be
     * <code>false</code>).
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOutOfBounds() throws Throwable
    {
        final boolean isCount = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").isCount(2, 3);
        Assert.assertFalse("Asserted count should be true.", isCount);
    }

    /**
     * Lower range limit is negative (exception expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountNegativeMin() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").isCount(-1, 1);
    }

    /**
     * Upper range limit is lower than the lower range limit (exception
     * expected).
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountMaxTooLow() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPageMany()).byXPath("//*[@class='foo']").isCount(2, 1);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /*
     * Index
     */

    /**
     * Get first result.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIndexManyFirst() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").index(0);
        Assert.assertEquals("Expected 1st element.", "1", element.getAttribute("nr"));
    }

    /**
     * Get second result.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIndexManySecond() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").index(1);
        Assert.assertEquals("Expected 2nd element.", "2", element.getAttribute("nr"));
    }

    /**
     * Query first result but there are no results at all.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsIndexNoneFirst() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").index(0);
        Assert.assertNull("No such index.", element);
    }

    /**
     * Negative index number.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIndexNegative() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").index(-1);
    }

    /*
     * Random
     */

    /**
     * Test random result element selection.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsRandom() throws Throwable
    {
        final LookUpResult results = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']");
        int countNr1 = 0;
        int countNr2 = 0;

        for (int i = 0; i < 100; i++)
        {
            final HtmlElement element = results.random();
            final String nr = element.getAttribute("nr");
            if (nr.equals("1"))
            {
                countNr1++;
            }
            else if (nr.equals("2"))
            {
                countNr2++;
            }
            else
            {
                Assert.fail("Unexpected element found.");
            }
        }
        Assert.assertTrue("Element 1 was never selected.", countNr1 > 0);
        Assert.assertTrue("Element 2 was never selected.", countNr2 > 0);
    }

    /**
     * Select random element out of <code>0</code> elements.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsRandomNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").random();
        Assert.assertNull("Found element falsely.", element);
    }

    /*
     * Single
     */

    /**
     * Get one and only result element.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsSingle() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageOne())
                        .byXPath("//*[@class='foo']").random();
        Assert.assertEquals("Unexpected element found.", "1", element.getAttribute("nr"));
    }

    /**
     * Many results, so there's no 'single' result.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsSingleMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").single();
        Assert.assertNull("Found element falsely.", element);
    }

    /**
     * No result.
     *
     * @throws Throwable
     */
    @Test
    public void testResultsSingleNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage(""))
                        .byXPath("//*[@class='foo']").single();
        Assert.assertNull("Found element falsely.", element);
    }

    /*
     * Asserted Lookup
     */

    /**
     * Raw: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedRaw() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().raw();
    }

    /**
     * Raw: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedRawFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().raw();
    }

    /**
     * All: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedAll() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().all();
    }

    /**
     * All: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedAllFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().all();
    }

    /**
     * Count: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedCount() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().count();
    }

    /**
     * Count: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedCountFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().count();
    }

    /**
     * Exists: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedExists() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().exists();
    }

    /**
     * Exists: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedExistsFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().exists();
    }

    /**
     * IsCount: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedIsCount() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().isCount(1);
    }

    /**
     * IsCount: Assertion failed (count doesn't match)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().isCount(2);
    }

    /**
     * IsCount: Assertion failed (count matches but no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountFail2() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().isCount(0);
    }

    /**
     * IsCount: Assertion fulfilled (at least 1 result, number of results within
     * given range), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedIsCountRange() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().isCount(0, 1);
    }

    /**
     * IsCount: Assertion failed (number of results within given range, but no
     * result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountRangeFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().isCount(0, 1);
    }

    /**
     * IsCount: Assertion failed (at least 1 result, but number of results not
     * within given range)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountRangeFail2() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().isCount(2, 3);
    }

    /**
     * Index: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIndex() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().index(0);
    }

    /**
     * Index: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIndexFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().index(0);
    }

    /**
     * Index: Assertion failed (many results but index out of bounds)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIndexFail2() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().index(5);
    }

    /**
     * First: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedFirst() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().first();
    }

    /**
     * First: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedFirstFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().first();
    }

    /**
     * Last: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedLast() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().last();
    }

    /**
     * Last: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedLastFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().last();
    }

    /**
     * Random: Assertion fulfilled (at least 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedRandom() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().random();
    }

    /**
     * Random: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedRandomFail() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().random();
    }

    /**
     * Single: Assertion fulfilled (exactly 1 result), no error
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedSingle() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageOne())
                        .byId("foo").asserted().single();
    }

    /**
     * Single: Assertion failed (too many results)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedSingleFailMany() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPageMany())
                        .byXPath("//*[@class='foo']").asserted().single();
    }

    /**
     * Single: Assertion failed (no result)
     *
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedSingleFailNone() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage(""))
                        .byId("foo").asserted().single();
    }

    /**
     * Test custom assertion message.
     *
     * @throws Throwable
     */
    @Test
    public void testAssertedCustomMessage() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("foo").asserted("myCustomMessage").raw();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /*
     * Message
     */

    /**
     * Override message by successive assertion messages.
     *
     * @throws Throwable
     */
    @Test
    public void testMessageOverride_2() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("x").asserted("foo").asserted("myCustomMessage").exists();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /**
     * Override several successive assertion messages
     *
     * @throws Throwable
     */
    @Test
    public void testMessageOverride_3() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("x").asserted("foo").asserted("bar").asserted("baz").asserted("myCustomMessage").exists();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /**
     * Override assertion message of previous locator
     *
     * @throws Throwable
     */
    @Test
    public void testMessageOverride_4() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("x").asserted("foo").byCss("y").asserted("myCustomMessage").exists();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /**
     * New locator will remove all previous validations.
     *
     * @throws Throwable
     */
    @Test(expected = TestException.class)
    public void testMessageOverride_5() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("x").asserted("foo").byCss("y").asserted("myCustomMessage").byXPath("./z").exists();
            throw new TestException(); // make sure we see when we don't get an assertion
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /*
     * Chaining
     */

    /**
     * Test lookup strategy chain.
     *
     * @throws Throwable
     */
    @Test
    public void testStrategyChaining() throws Throwable
    {
        final HtmlElement element = HPU.find()
                        .in(getHtmlPage("<html><body><div id=\"a\"><div class=\"aa\"><div class=\"aaa\" nr=\"1\"></div></div></div><div id=\"b\"><div class=\"aa\"><div class=\"aaa\" nr=\"2\"></div></div></div></body><html>"))
                        .byId("a").byCss(".aa").byXPath("./*[@class='aaa']").single();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Test lookup strategy chain.
     *
     * @throws Throwable
     */
    @Test
    public void testStrategyChaining2() throws Throwable
    {
        final List<HtmlElement> elements = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<div id=\"a\">"
                                        + "    <div class=\"aa\">"
                                        + "        <div class=\"aaa\" nr=\"1\">"
                                        + "        </div>"
                                        + "    </div>"
                                        + "</div>"
                                        + "<div id=\"b\">"
                                        + "    <div class=\"aa\">"
                                        + "        <div class=\"aaa\" nr=\"2\">"
                                        + "        </div>"
                                        + "    </div>"
                                        + "</div>"
                                        + "</body><html>"))
                        .byCss("#a").byCss(".aa").all();

        Assert.assertEquals(1, elements.size());
    }

    @Test
    public void testStrategyChainingSeparated() throws Throwable
    {
        // lookup by ID
        LookUpResult results = HPU.find()
                        .in(getHtmlPage("<html><body><div id=\"a\"><div class=\"aa\"><div class=\"aaa\" nr=\"1\"></div></div></div><div id=\"b\"><div class=\"aa\"><div class=\"aaa\" nr=\"2\"></div></div></div></body><html>"))
                        .byId("a");

        // refine by CSS
        results = results.byCss(".aa");

        // refine by XPath
        results = results.byXPath("./*[@class='aaa']");
        final HtmlElement element = results.single();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Creates an {@link HtmlPage} object from the passed HTML source code.
     *
     * @throws IOException
     * @throws MalformedURLException
     * @throws FailingHttpStatusCodeException
     */
    public HtmlPage getHtmlPage(final String htmlSource) throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        final MockWebConnection connection = new MockWebConnection();
        connection.setDefaultResponse(htmlSource);
        webClient.setWebConnection(connection);

        return webClient.getPage("http://localhost/");
    }

    /**
     * Page containing 2 <code>div</code> elements but only 1 with name/class/id
     * <code>'foo'</code>.
     *
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public HtmlPage getHtmlPageOne() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        return getHtmlPage("<html><body><div  id=\"foo\" name=\"foo\" class=\"foo\" nr=\"1\"></div><div id=\"bar\" name=\"bar\" class=\"bar\" nr=\"2\"></div></body></html>");
    }

    /**
     * Page containing 3 <code>div</code> elements but only 2 with name/class/id
     * <code>'foo'</code>.
     *
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public HtmlPage getHtmlPageMany() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        return getHtmlPage(
                        "<html><body><div id=\"foo\" name=\"foo\" class=\"foo\" nr=\"1\"></div><div id=\"foo\" name=\"foo\" class=\"foo\" nr=\"2\"></div><div nr=\"3\"></body></html>");
    }

    /**
     * Creates an {@link HtmlInput}.
     *
     * @throws IOException
     * @throws MalformedURLException
     * @throws FailingHttpStatusCodeException
     */
    public HtmlElement getHtmlElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        final HtmlPage page = getHtmlPage("");

        final AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(null, "name", "name", "", "myName");
        atts.addAttribute(null, "value", "value", "", "myValue");
        atts.addAttribute(null, "type", "type", "", "text");
        final HtmlInput input = (HtmlInput) InputElementFactory.instance.createElement(page, HtmlInput.TAG_NAME, atts);

        return input;
    }

    /**
     * Unchecked exception for testing purposes.
     */
    private static class TestException extends RuntimeException
    {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;
    }

    /**
     * Get elements within first result set only. Lookup element by ID, so
     * another lookup to same ID via CSS should not return any results.
     *
     * @throws Throwable
     */
    @Test
    public void testByIdByCssID() throws Throwable
    {
        final List<?> raw = HPU.find()
                        .in(getHtmlPage("<html><body><div id='foo'><div>bar</div></div></body></html>"))
                        .byId("foo")
                        .byCss("#foo")
                        .raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * An XPath starting with a slash is an absolute path (root is page document
     * root). If an element is looked up in relation to any node but the page it
     * self, this lookup must fail.
     *
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testXPathNestedAbsolute() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage("<html><body><div id='foo'><div>bar</div></div></body></html>"))
                        .byId("foo")
                        .byXPath("//div")
                        .raw();
    }

    /**
     * An XPath starting with the ID function does a page wide lookup. That does
     * not make sense if the lookup is nested (and therefore expected to be done
     * within a certain DOM tree branch only). So if a nested XPath ises the
     * ID-function it must fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testXPathNestedIdFunction() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage("<html><body><div id='foo'><div>bar</div></div></body></html>"))
                        .byId("foo")
                        .byXPath("id('foo')")
                        .raw();
    }

    /**
     * Nested XPath lookups, that base on a relative XPath, are welcome.
     *
     * @throws Throwable
     */
    @Test
    public void testXPathNestedRelativePath() throws Throwable
    {
        HPU.find()
                        .in(getHtmlPage("<html><body><div id='foo'><div>bar</div></div></body></html>"))
                        .byId("foo")
                        .byXPath(".//div")
                        .raw();
    }

    // **************************************************************
    // Filtering
    // **************************************************************

    /**
     * Test filter by string
     */
    @Test
    public void testDiscardByString_HappyPath() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("bar", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("foo", result.get(0).getAttribute("class"));
    }

    /**
     * Test keep by string
     */
    @Test
    public void testKeepByString_HappyPath() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar c1'>"
                                        + "<a class='bar c2'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .keep("bar", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("bar c1", result.get(0).getAttribute("class"));
        Assert.assertEquals("bar c2", result.get(1).getAttribute("class"));
    }

    /**
     * Test keep by string
     */
    @Test
    public void testKeepByList_HappyPath() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar c1'>"
                                        + "<a class='bar c2'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .keep(Arrays.asList(new String[] { "bar" }), e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("bar c1", result.get(0).getAttribute("class"));
        Assert.assertEquals("bar c2", result.get(1).getAttribute("class"));
    }

    /**
     * Filter all
     */
    @Test
    public void testFilterByStringFilterAll() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("bar", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(0, result.size());
    }

    /**
     * Filter no match
     */
    @Test
    public void testFilterByStringNoMatch() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("sweet", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(3, result.size());
    }

    /**
     * Non matching get
     */
    @Test
    public void testFilterByStringFunctionDoesNotReturnAnything() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("bar", e -> e.getAttribute("href"))
                        .all();

        Assert.assertEquals(3, result.size());
    }

    /**
     * Partially matching only
     */
    @Test
    public void testFilterByStringFunctionalPartialMatch() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a href='/bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("bar", e -> e.getAttribute("href"))
                        .all();

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("foo", result.get(0).getAttribute("class"));
        Assert.assertEquals("bar", result.get(1).getAttribute("class"));
    }

    /**
     * Two filters, both match
     */
    @Test
    public void testFilterByStringTwoTimes() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a href='/bar'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("bar", e -> e.getAttribute("href"))
                        .discard("bar", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("foo", result.get(0).getAttribute("class"));
    }

    /**
     * Two filters, by regexp
     */
    @Test
    public void testFilterByStringTwoTimesRegexp() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='foobar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("^bar$", e -> e.getAttribute("class"))
                        .discard("foo$", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("foobar", result.get(0).getAttribute("class"));
    }

    /**
     * Three filters, first two matches all already
     */
    @Test
    public void testFilterByStringThreeFilters() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='foobar'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard("foo", e -> e.getAttribute("class"))
                        .discard("bar", e -> e.getAttribute("class"))
                        .discard("foobar", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(0, result.size());
    }

    /**
     * Filter lists with none matches
     */
    @Test
    public void testFilterByStringNonMatch() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='qwer'>"
                                        + "<a class='tzu'>"
                                        + "<a class='oisu'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard(Arrays.asList(new String[] { "foo", "bar" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] { "foo3", "1bar" }), e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(3, result.size());
    }

    /**
     * Filter lists with late matches
     */
    @Test
    public void testFilterByStringLateMatch() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='qwer'>"
                                        + "<a class='tzu'>"
                                        + "<a class='oisu'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard(Arrays.asList(new String[] { "foo", "bar", "qwer" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] { "foo3", "1bar", "khgf", "tzu" }), e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("oisu", result.get(0).getAttribute("class"));
    }

    /**
     * Filter lists with late matches
     */
    @Test
    public void testFilterByListEmptyLists() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='qwer'>"
                                        + "<a class='tzu'>"
                                        + "<a class='oisu'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .discard(Arrays.asList(new String[] {}), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] {}), e -> e.getAttribute("foobae"))
                        .all();

        Assert.assertEquals(3, result.size());
    }

    /**
     * Mixed test of keep and discard
     */
    @Test
    public void testFilterByListKeepAndDiscard() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "<a class='circus'>"
                                        + "<a class='foo'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .keep(Arrays.asList(new String[] { "bar", "circus" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] { "bar" }), e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("circus", result.get(0).getAttribute("class"));
    }

    /**
     * Mixed test of keep and discard
     */
    @Test
    public void testFilterByStringChained() throws Throwable
    {
        final LookUpResult result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='foo' selected='selected'>"
                                        + "<a class='bar'>"
                                        + "</body></html>"))
                        .byCss("body").byCss(".foo");
        final List<HtmlElement> finalResult = result.discard("selected", e -> e.getAttribute("selected")).all();

        Assert.assertEquals(1, finalResult.size());
        Assert.assertFalse(finalResult.get(0).hasAttribute("selected"));
    }

    /**
     * Max bad
     */
    @Test
    public void testFilterByListMixed() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "<a class='circus'>"
                                        + "<a class='foo'>"
                                        + "<img class='master'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .keep(Arrays.asList(new String[] { "bar", "circus" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] { "bar" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] { "sadfasdf" }), e -> e.getAttribute("class"))
                        .keep(Arrays.asList(new String[] { "sadfasdf" }), e -> e.getAttribute("class"))
                        .discard(Arrays.asList(new String[] {}), e -> e.getAttribute("class"))
                        .discard("", e -> e.getAttribute("selected"))
                        .keep("", e -> e.getAttribute("selected"))
                        .discard("foo", e -> e.getAttribute("class"))
                        .discard("fasdfasdfoo", e -> e.getAttribute("asdfasfd"))
                        .keep("asdfasdf", e -> e.getAttribute("clasdfasdfass"))
                        .keep("asdfasdf", e -> e.asText())
                        .byCss("img")
                        .keep("master", e -> e.getAttribute("class"))
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("master", result.get(0).getAttribute("class"));
    }

    /**
     * Filter by predicate
     */
    @Test
    public void testFilterHappyPath() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a class='foo'>"
                                        + "<a class='bar'>"
                                        + "<a class='bar'>"
                                        + "<a hrf='dd'>"
                                        + "<a class='foo'>"
                                        + "<img class='master'>"
                                        + "</body></html>"))
                        .byCss("a")
                        .filter(e -> e.hasAttribute("class"))
                        .all();

        Assert.assertEquals(4, result.size());
    }

    /**
     * Filter by predicate
     */
    @Test
    public void testFilterHappyPathComplex() throws Throwable
    {
        final List<HtmlElement> result = HPU.find()
                        .in(getHtmlPage("<html><body>"
                                        + "<a href='dd'>"
                                        + "<a class='foo'>"
                                        + "<img class='master'>"
                                        + "</body></html>"))
                        .byCss("*")
                        .filter(e ->
                        {
                            final String s = e.getAttribute("class");
                            if (s != null && s.trim().length() > 0)
                            {
                                return s.equals("foo");
                            }
                            return false;
                        })
                        .all();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("foo", result.get(0).getAttribute("class"));
        Assert.assertEquals("a", result.get(0).getTagName());
    }

    @Test
    public void testFindElementInElementByCss() throws Throwable
    {
        final HtmlPage page = getHtmlPage("<html><body>"
                        + "<div class='foo'>"
                        + "  <div class='bar' data-id='right'>bar1</div>"
                        + "</div>"
                        + "<div class='bar' data-id='wrong'>bar2</div>"
                        + "</body></html>");

        // base element
        final HtmlElement foo = HPU.find().in(page).byCss(".foo").single();

        // lookup inside the base element
        final LookUpResult bar = HPU.find().in(foo).byCss(".bar");
        
        // ensure we only get the inner DIV, not the sibling
        Assert.assertEquals(1, bar.count());
        Assert.assertEquals("right", bar.single().getAttribute("data-id"));
    }

    /**
     * CSS locator alternatives (separated by comma) must stick to the parent scope only.
     *
     * @throws Throwable
     */
    @Test
    public void testFindElementInElementByMultiCss() throws Throwable
    {
        final HtmlPage page = getHtmlPage("<html><body>"
                        + "<div class='foo'>"
                        + "  <div class='bar' data-id='right'>bar1</div>"
                        + "</div>"
                        + "<div class='bar bum' data-id='wrong'>bar2</div>"
                        + "</body></html>");

        // base element
        final HtmlElement foo = HPU.find().in(page).byCss(".foo").single();

        // lookup alternatives inside the base element
        final LookUpResult barbum = HPU.find().in(foo).byCss(".bar, .bum");
        
        // ensure we only get the inner DIV, not the sibling
        Assert.assertEquals(1, barbum.count());
        Assert.assertEquals("right", barbum.single().getAttribute("data-id"));
    }
}
