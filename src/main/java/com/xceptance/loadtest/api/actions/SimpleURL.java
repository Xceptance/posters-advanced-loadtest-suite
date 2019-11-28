package com.xceptance.loadtest.api.actions;

import com.xceptance.loadtest.api.util.Context;

/**
 * This is a simple test class for pulling urls.
 *
 * @author Rene Schwietzke
 * @version
 */
public class SimpleURL extends PageAction<SimpleURL>
{
    private final String url;

    /**
     * Creates an SimpleUrl object.
     *
     * @param url
     *            the URL which is beeing loaded
     */
    public SimpleURL(final String url)
    {
        this.url = url;
    }

    /**
     * Creates an SimpleUrlObject.
     *
     * @param action
     *            the previous action
     * @param url
     *            the URL which is being loaded
     */
    public SimpleURL(final com.xceptance.xlt.api.actions.AbstractHtmlPageAction action, final String url)
    {
        super(action);
        setTimerName("SimpleURL.Followup");

        this.url = url;
    }

    @Override
    protected void doExecute() throws Exception
    {
        if (Context.getPage() == null)
        {
            Context.setBasicAuthenticationHeader();
            loadPage(url);
        }
        else
        {
            loadPageByUrlClick(url);
        }
    }

    @Override
    protected void postValidate() throws Exception
    {
        // simply nothing .. really .. nothing .. no the faintest check
    }
}
