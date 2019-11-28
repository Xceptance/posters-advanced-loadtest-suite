package com.xceptance.loadtest.api.validators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.XHTMLValidator;
import com.xceptance.xlt.common.XltConstants;
import com.xceptance.xlt.engine.SessionImpl;

public class Validator
{
    /**
     * Validates the html page source of the response.
     *
     * Will validate three properties of the source: - Content length matches the content length
     * given in the header - Does the site contain a closing html end tag (</html>) - Will apply the
     * XHTML validator to check global conformity
     *
     * NOTE: This will not validate the response status code. This check occurs already in class
     * {@link PageAction}.
     *
     * @throws Exception
     */
    public static void validatePageSource() throws Exception
    {
        final HtmlPage page = Context.getPage();

        // Does the length match?
        ContentLengthValidator.getInstance().validate(page);

        // Use xhtml validator for a global conformity check
        XHTMLValidator.getInstance().validate(page);
    }

    public static void validateHtmlEndTag() throws IOException
    {
        try
        {
            HtmlEndTagValidator.getInstance().validate(Context.getPage());
        }
        catch (final AssertionError e)
        {
            // We either have no response string, the response string is empty, or did not find HTML
            // end tag. Now let's try to gather some additional data.
            dumpResponseContentAndFail(Context.getPage().getWebResponse(), e.getMessage());
        }
    }

    /**
     * The method stores the last n readable bytes of the response and logs them as even along with
     * URL and content length. Finally it throws an error. Error and Event will reference to each
     * other by an ID (which will be the request ID if present).
     *
     * @param response
     * @throws IOException
     */
    public static void dumpResponseContentAndFail(final WebResponse response, final String message)
    {
        if (response != null)
        {
            final SessionImpl session = (SessionImpl) Session.getCurrent();

            // dump directory
            final File resultsDirectory = new File(session.getResultsDirectory(), XltConstants.DUMP_OUTPUT_DIR);
            final File dumpDirectory = new File(resultsDirectory, session.getID());
            final File responseDirectory = new File(dumpDirectory, XltConstants.DUMP_RESPONSES_DIR);
            responseDirectory.mkdirs();

            // dump file
            final String requestId = getId(response);
            final File dumpFile = new File(responseDirectory, requestId + ".txt");

            // read-write-fail
            try (final InputStream inputStream = response.getContentAsStream(); FileOutputStream outputStream = new FileOutputStream(dumpFile))
            {
                IOUtils.copy(inputStream, outputStream);
                Session.getCurrent().getValueLog().put("Defective response", dumpFile.getName());
                Assert.fail(message + " -> check custom dump file (referenced by ID in result browser log)");
            }
            catch (final Exception e)
            {
                XltLogger.runTimeLogger.error("Cannot write response content dump to file: " + dumpFile.getAbsolutePath(), e);
            }
        }
        else
        {
            XltLogger.runTimeLogger.error("Cannot dump from NULL response");
        }
    }

    /**
     * Get the request ID or generate a new one if the request ID is not available.
     *
     * @param response
     * @return request ID
     */
    private static String getId(final WebResponse response)
    {
        // first try to get the official request header ID
        final String requestIdHeaderName = Context.get().configuration.getProperties().getProperty("com.xceptance.xlt.http.requestId.headerName");
        if (requestIdHeaderName != null)
        {
            final String requestId = response.getWebRequest().getAdditionalHeaders().get(requestIdHeaderName);
            if (requestId != null)
            {
                return requestId;
            }
        }

        // if the request header ID is not present, we generate our own
        return UUID.randomUUID().toString();
    }

    public static class StatusCodeValidator
    {
        private static final int DEFAULT_STATUS_CODE = 200;

        private int expectedStatusCode = DEFAULT_STATUS_CODE;
        private String expectedStatusCodePattern = null;

        public void expect(final int statusCode)
        {
            Assert.assertTrue("Blank status code pattern is not allowed.", statusCode > 0);
            Assert.assertTrue("Blank status code pattern is not allowed.", statusCode < 600);
            expectedStatusCode = statusCode;
            expectedStatusCodePattern = null;
        }

        public void expect(final String statusCodePattern)
        {
            Assert.assertFalse("Blank status code pattern is not allowed.", StringUtils.isBlank(statusCodePattern));
            expectedStatusCode = -1;
            expectedStatusCodePattern = statusCodePattern;
        }

        public void validate()
        {
            final int currentStatusCode = getCurrentStatusCode();

            // we can go with an int status code check if we have no specific pattern given
            if (expectedStatusCode > 0)
            {
                Assert.assertEquals("Response code does not match.", expectedStatusCode, currentStatusCode);
            }
            else
            {
                // prepare the status code matching
                final String currentStatusCodeString = Integer.toString(currentStatusCode);

                // match and check
                final boolean match = RegExUtils.isMatching(currentStatusCodeString, expectedStatusCodePattern);
                Assert.assertTrue("Response code does not match. Expectation pattern: " + expectedStatusCodePattern + ", Input: " + currentStatusCodeString, match);

            }

            // reset - The custom status code pattern is valid for one page load only.
            expectedStatusCodePattern = null;
            expectedStatusCode = DEFAULT_STATUS_CODE;
        }

        protected int getCurrentStatusCode()
        {
            return Context.getPage().getWebResponse().getStatusCode();
        }
    }
}
