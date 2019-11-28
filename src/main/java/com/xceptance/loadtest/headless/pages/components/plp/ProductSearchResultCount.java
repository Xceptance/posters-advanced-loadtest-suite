package com.xceptance.loadtest.headless.pages.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;

public enum ProductSearchResultCount implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return ProductSearchResult.instance.locate().byCss(".result-count");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
