package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

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
        return locate().exists();
    }
}
