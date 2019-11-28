package com.xceptance.loadtest.headless.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;

public class PasswordCard implements Component
{
    public final static PasswordCard instance = new PasswordCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".card").hasCss("a[aria-label = '" + Context.configuration().localizedText("account.passwordcard.link.aria-label") + "']");
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
