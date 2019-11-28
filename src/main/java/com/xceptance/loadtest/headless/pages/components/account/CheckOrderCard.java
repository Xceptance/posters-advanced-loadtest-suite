package com.xceptance.loadtest.headless.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public class CheckOrderCard implements Component
{
    public final static CheckOrderCard instance = new CheckOrderCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".login-page .card").hasCss(".track-order-header");
    }

    /**
     * Indicates if this component exists
     *
     * @return
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
