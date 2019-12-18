package com.xceptance.loadtest.api.actions.debug;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;

/**
 * This is a simple test class for pulling urls.
 *
 * @author Rene Schwietzke
 * @version
 */
public class DebugUrl extends PageAction<DebugUrl>
{
    private final String urlString;

    /**
     * Creates an SimpleUrl object.
     *
     * @param urlString
     *            the URL which is being loaded
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
        // simply nothing .. really .. nothing .. no the faintest check
    }
}
