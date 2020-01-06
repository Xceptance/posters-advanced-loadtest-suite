package com.xceptance.loadtest.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Supplies a site either randomly via the given distribution (market share) or a fixed one when requesting via ID.
 * 
 * @author Xceptance Software Technologies
 */
public class SiteSupplier
{
    private static List<Site> getSitesAndMarketShare()
    {
    	final List<Site> sites = new ArrayList<>(100);
    	
        // Setup all sites, distributed by their given market share
        for (final Site site : Context.defaultConfiguration.get().sites.unweightedList())
        {
            // If the site is not active, ignore it
            if (site.active == false)
            {
                continue;
            }

            // Add the current site as often as the market share indicates
            for (int i = 0; i < site.marketshare; i++)
            {
                sites.add(site);
            }
        }
        
        return sites;
    }

    /**
     * Return a random site based on the active sites and their market share.
     * 
     * @return A randomly chosen site.
     */
    public static Optional<Site> getRandomSite()
    {
    	final List<Site> sites = getSitesAndMarketShare();
        if (sites.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(sites.get(XltRandom.nextInt(sites.size())));
        }
    }

    /**
     * Returns a site by id or any empty optional.
     *
     * @param id The site id.
     * @return The site with this id or an empty optional.
     */
    public static Optional<Site> siteById(final String id)
    {
        return Context.defaultConfiguration.get().sites.getById(id);
    }
}