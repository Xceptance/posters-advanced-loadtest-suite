package com.xceptance.loadtest.api.actions;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.engine.XltWebClient;

/**
 * Base class of all AJAX-only actions.
 *
 * @author Xceptance Software technologies
 */
public abstract class AjaxAction<T> extends PageAction<T>
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void postExecute() throws Exception
    {
        // Load additional information when in debug mode to improve result browser output
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
    }
}