package com.xceptance.loadtest.headless.pages.components.category;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

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
