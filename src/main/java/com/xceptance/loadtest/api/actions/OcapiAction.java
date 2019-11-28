package com.xceptance.loadtest.api.actions;

import java.net.URL;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.engine.XltWebClient;

/**
 * Base class of all OCAPI-only actions.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public abstract class OcapiAction<T> extends PageAction<T>
{
    /**
     * Sets an empty page with given URL.
     *
     * @param pageUrl
     *            the full qualified URL String of the page
     * @throws Exception
     *             if an error occurred while parsing the URL or setting the page
     */
    public void setEmptyPage(final String pageUrl) throws Exception
    {
        final String emptyPageString = "<html><head></head><body></body></html>";

        final StringWebResponse webResponse = new StringWebResponse(emptyPageString, new URL(pageUrl));

        final WebClient webClient = getWebClient();
        final HtmlPage page = (HtmlPage) webClient.getPageCreator().createPage(webResponse, webClient.getCurrentWindow());
        setHtmlPage(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postExecute() throws Exception
    {
        // try to load stuff during debug that has been added for easier viewing in the
        // result browser
        if (Context.isLoadTest == false)
        {
            final boolean state = ((XltWebClient) getWebClient()).getOptions().isJavaScriptEnabled();

            // needed for image loading
            ((XltWebClient) getWebClient()).getOptions().setJavaScriptEnabled(true);

            // load new images
            ((XltWebClient) getWebClient()).loadNewStaticContent(this.getHtmlPage());

            // reset state
            ((XltWebClient) getWebClient()).getOptions().setJavaScriptEnabled(state);
        }

        super.handleLongRunningRequests();
    }
}
