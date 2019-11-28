package com.xceptance.loadtest.api.tests;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.data.SiteSupplier;

public interface SiteByMarketShare
{
    default Site supplySite()
    {
        return SiteSupplier.randomSite().get();
    }
}
