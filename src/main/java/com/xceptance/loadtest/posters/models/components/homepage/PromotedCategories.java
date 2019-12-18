package com.xceptance.loadtest.posters.models.components.homepage;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class PromotedCategories implements Component
{
    public final static PromotedCategories instance = new PromotedCategories();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".home-categories.homepage");
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
