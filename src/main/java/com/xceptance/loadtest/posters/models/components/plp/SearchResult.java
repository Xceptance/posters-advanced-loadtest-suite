package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * SearchResult component.
 * 
 * @author Xceptance Software Technologies
 */
public enum SearchResult implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#productOverview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists() && Page.find().byId("titleSearchText").exists();
    }
}
