package com.xceptance.loadtest.api.configuration;

import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.data.Site;

/**
 * This class holds the initialized site values which are needed before a test case starts, hence
 * they don't fit into the general configuration. This also means they are not test case specific
 * and can not be put into a user context.
 *
 * @author Xceptance Software Technologies
 */
public class DefaultConfiguration
{
    // General list of sites
    @EnumProperty(key = "sites", clazz = Site.class, required = true, stopOnGap = true, byId = true)
    public EnumConfigList<Site> sites;
}
