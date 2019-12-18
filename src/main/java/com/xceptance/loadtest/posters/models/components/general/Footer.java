package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class Footer implements Component
{
    public final static Footer instance = new Footer();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("html > body > footer");
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
