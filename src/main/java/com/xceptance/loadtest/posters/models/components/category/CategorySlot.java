package com.xceptance.loadtest.posters.models.components.category;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class CategorySlot implements Component
{
    public final static CategorySlot instance = new CategorySlot();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".category-slot");
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

    public LookUpResult getCategorySlotsLinks()
    {
        return locate().byCss(".category-item a");
    }
}
