package com.xceptance.loadtest.api.action;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.XltConstants;
import com.xceptance.xlt.engine.SessionImpl;

public class PageActionTest
{
    @BeforeClass
    public static void init()
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        properties.setProperty("com.xceptance.xlt.http.filter.include", "^http://localhost");

        Context.createContext(properties, PageActionTest.class.getSimpleName(), PageActionTest.class.getName(), SiteSupplier.randomSite().get());
    }

    @Test(expected = AssertionError.class)
    public void testBlankPatternReset() throws Throwable
    {
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                expectStatusCode("").loadPage("localhost");
            }
        };

        action.run();
    }

    @Test
    public void testPatternReset() throws Throwable
    {
        final String url500 = "http://localhost/test500";
        final String url200 = "http://localhost/test200";

        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url200);

                expectStatusCode(500).loadPageByClick(HPU.find().in(getHtmlPage()).byCss("a").asserted().single());
                Assert.assertEquals("Mock returned the wrong status code", 500, getHtmlPage().getWebResponse().getStatusCode());

                loadPageByClick(HPU.find().in(getHtmlPage()).byCss("a").asserted().single());
                Assert.assertEquals("Mock returned the wrong status code", 200, getHtmlPage().getWebResponse().getStatusCode());
            }
        };

        action.addResponse(url500, "<a href='" + url200 + "'></a>", 500);
        action.addResponse(url200, "<a href='" + url500 + "'></a>");

        action.run();
    }

    @Test
    public void testRegexpVariations() throws Throwable
    {
        final String[] states = new String[] { "500", "^5", ".*", ".", "(500)", "\\d+", "500(somethingfunny)?", "50{2}" };

        for (final String status : states)
        {
            final String url = "http://localhost/pageLoad";
            final TestAction action = new TestAction()
            {
                @Override
                public void doIt() throws Exception
                {
                    loadPage(url);

                    expectStatusCode(status).loadPageByClick(HPU.find().in(getHtmlPage()).byCss("a").asserted().single());
                    Assert.assertEquals("Mock returned the wrong status code", 500, getHtmlPage().getWebResponse().getStatusCode());
                }
            };

            action.addResponse("http://localhost/test", 500);
            action.addResponse(url, "<a href='http://localhost/test'></a>");

            action.run();
        }
    }

    @Test
    public void testLoadPageFormSubmitExpectStatus() throws Throwable
    {
        final int[] states = new int[] { 500, 200, 418 };

        for (final int status : states)
        {
            final String url = "http://localhost/pageLoad";
            final TestAction action = new TestAction()
            {
                @Override
                public void doIt() throws Exception
                {
                    loadPage(url);

                    expectStatusCode(status).loadPageByFormSubmit(HPU.find().in(getHtmlPage()).byCss("form").asserted().single());
                    Assert.assertEquals("Mock returned the wrong status code", status, getHtmlPage().getWebResponse().getStatusCode());
                }
            };

            action.addResponse(url, "<form action='http://localhost/test' method=\"post\"></form>");
            action.addResponse("http://localhost/test", status);

            action.run();
        }
    }

    @Test
    public void testLoadPageFormSubmitDefault() throws Throwable
    {
        final String url = "http://localhost/pageLoad";
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
                loadPageByFormSubmit(HPU.find().in(getHtmlPage()).byCss("form").asserted().single());
                Assert.assertEquals("Mock returned the wrong status code", 200, getHtmlPage().getWebResponse().getStatusCode());
            }
        };

        action.addResponse(url, "<form action='http://localhost/test' method=\"post\"></form>");
        action.addResponse("http://localhost/test");

        action.run();
    }

    @Test
    public void testLoadPageDefault() throws Throwable
    {
        final String url = "http://localhost/pageLoad";
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
                Assert.assertEquals("Mock returned the wrong status code", 200, getHtmlPage().getWebResponse().getStatusCode());
            }
        };

        action.addResponse(url);
        action.run();
    }

    @Test
    public void testLoadPageDefaultExpectStatus() throws Throwable
    {
        final int[] states = new int[] { 200, 418, 410, 302 };

        for (final int status : states)
        {
            final String url = "http://localhost/pageLoad";
            final TestAction action = new TestAction()
            {
                @Override
                public void doIt() throws Exception
                {
                    expectStatusCode(status).loadPage(url);
                    Assert.assertEquals("Mock returned the wrong status code", status, getHtmlPage().getWebResponse().getStatusCode());
                }
            };

            action.addResponse(url, status);
            action.run();
        }
    }

    @Test
    public void testLoadPageByClickDefault() throws Throwable
    {
        final String url = "http://localhost/pageLoad";
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
                loadPageByClick(HPU.find().in(getHtmlPage()).byCss("a").asserted().single());
                Assert.assertEquals("Mock returned the wrong status code", 200, getHtmlPage().getWebResponse().getStatusCode());
            }
        };

        action.addResponse(url, "<a href='http://localhost/test'></a>");
        action.addResponse("http://localhost/test");

        action.run();
    }

    @Test
    public void testLoadPageByClickExpectStatus() throws Throwable
    {
        final int[] states = new int[] { 500, 200, 418 };

        for (final int status : states)
        {
            final String url = "http://localhost/pageLoad";
            final TestAction action = new TestAction()
            {
                @Override
                public void doIt() throws Exception
                {
                    loadPage(url);
                    expectStatusCode(status).loadPageByClick(HPU.find().in(getHtmlPage()).byCss("a").asserted().single());
                    Assert.assertEquals("Mock returned the wrong status code", status, getHtmlPage().getWebResponse().getStatusCode());
                }
            };

            action.addResponse(url, "<a href='http://localhost/test'></a>");
            action.addResponse("http://localhost/test", status);

            action.run();
        }
    }

    @Test(expected = AssertionError.class)
    public void testHtmlEndTagIssue() throws Throwable
    {
        final String url = "http://localhost/pageLoad";
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
            }
        };

        action.addPlainResponse(url, "foo", 200);
        action.run();
    }

    @Test
    public void testHtmlEndTagIssueLog() throws Throwable
    {
        // prepare action

        final String url = "http://localhost/pageLoad";
        final String content = "foo";
        final String messageInUnexpectedsuccessCase = "RUN method is expected to fail";

        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
            }
        };

        action.addPlainResponse(url, content, 200);

        // run action
        try
        {
            action.run();

            // expect that it fails
            Assert.fail(messageInUnexpectedsuccessCase);
        }
        catch (final AssertionError e)
        {
            // ensure it's not our own assertion error
            if (messageInUnexpectedsuccessCase.equals(e.getMessage()))
            {
                throw e;
            }

            // check that the file dump exists and has the correct content
            final SessionImpl session = (SessionImpl) Session.getCurrent();
            final String fileName = (String) session.getValueLog().get("Defective response");
            final File resultsDirectory = new File(session.getResultsDirectory(), XltConstants.DUMP_OUTPUT_DIR);
            final File dumpDirectory = new File(resultsDirectory, session.getID());
            final File responseDirectory = new File(dumpDirectory, XltConstants.DUMP_RESPONSES_DIR);
            final File dumpFile = new File(responseDirectory, fileName);

            try (FileInputStream fis = new FileInputStream(dumpFile))
            {
                Assert.assertArrayEquals("File content does not match", content.getBytes(), Files.readAllBytes(dumpFile.toPath()));
            }
        }
    }

    @Test
    public void testHtmlEndTagOK() throws Throwable
    {
        final String url = "http://localhost/pageLoad";
        final TestAction action = new TestAction()
        {
            @Override
            public void doIt() throws Exception
            {
                loadPage(url);
            }
        };

        action.addResponse(url, "<html><body> foo </body></html>", 200);
        action.run();
    }

    abstract public class TestAction extends PageAction<Object>
    {
        private final MockWebConnection conn;

        public TestAction()
        {
            this.conn = new MockWebConnection();
        }

        public void addResponse(final String url) throws Exception
        {
            addResponse(url, "", 200);
        }

        public void addResponse(final String url, final String response) throws Exception
        {
            addResponse(url, response, 200);
        }

        public void addResponse(final String url, final int status) throws Exception
        {
            addResponse(url, "", status);
        }

        public void addResponse(final String url, final String response, final int status) throws Exception
        {
            final String validResponse = "<html><body>\n" + response + "\n</body></html>";
            addPlainResponse(url, validResponse, status);
        }

        public void addPlainResponse(final String url, final String response, final int status) throws Exception
        {
            conn.setResponse(new URL(url), response,
                            status,
                            "", "text/html",
                            new ArrayList<NameValuePair>());
        }

        @Override
        protected void doExecute() throws Exception
        {
            getWebClient().setWebConnection(conn);
            doIt();
        }

        abstract public void doIt() throws Exception;

        @Override
        protected void postExecute() throws Exception
        {
        }

        @Override
        protected void postValidate() throws Exception
        {
        }
    }
}
