package com.xceptance.loadtest.posters.models.components.homepage;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class PromotedProducts implements Component
{
    public final static PromotedProducts instance = new PromotedProducts();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#productList");
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
