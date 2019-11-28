package com.xceptance.loadtest.api.actions;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.engine.XltWebClient;

/**
 * Base class of all AJAX-only actions.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public abstract class AjaxAction<T> extends PageAction<T>
{
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
