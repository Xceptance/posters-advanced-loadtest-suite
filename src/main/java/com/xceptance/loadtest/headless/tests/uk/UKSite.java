package com.xceptance.loadtest.headless.tests.uk;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.tests.SiteByMarketShare;

public interface UKSite extends SiteByMarketShare
{
    /**
     * Use this to configure the test case the hard way and not via mapping. Or use this for
     * debugging
     */
    @Override
    default Site supplySite()
    {
        return SiteSupplier.siteById("UK").get();
    }
}
