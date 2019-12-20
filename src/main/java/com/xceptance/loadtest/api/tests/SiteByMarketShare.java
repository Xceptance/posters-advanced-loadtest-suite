package com.xceptance.loadtest.api.tests;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.data.SiteSupplier;

/**
 * Interface for sites with market share load distribution.
 * 
 * @author Xceptance Software Technologies
 */
public interface SiteByMarketShare
{
    default Site supplySite()
    {
        return SiteSupplier.randomSite().get();
    }
}