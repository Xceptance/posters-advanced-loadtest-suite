package com.xceptance.loadtest.api.actions.debug;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;

/**
 * Loads the provided (debug) URL.
 *
 * @author Xceptance Software Technologies
 */
public class DebugUrl extends PageAction<DebugUrl>
{
    private final String urlString;

    /**
     * Creates the action and sets the URL to load.
     *
     * @param urlString The URL to load.
     */
    public DebugUrl(final String urlString)
    {
        this.urlString = urlString;
    }

    @Override
    protected void doExecute() throws Exception
    {
        if (Context.getPage() == null)
        {
            Context.setBasicAuthenticationHeader();
            loadPage(Page.makeFullyQualifiedUrl(this.urlString));
        }
        else
        {
            loadPageByUrlClick(Page.makeFullyQualifiedUrl(this.urlString));
        }
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Do nothing
    }
}