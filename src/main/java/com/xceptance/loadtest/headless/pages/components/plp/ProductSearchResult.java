package com.xceptance.loadtest.headless.pages.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public enum ProductSearchResult implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#product-search-results");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
