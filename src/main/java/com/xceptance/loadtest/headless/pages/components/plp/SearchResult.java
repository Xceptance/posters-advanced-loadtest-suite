package com.xceptance.loadtest.headless.pages.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public enum SearchResult implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".page > .search-results");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
