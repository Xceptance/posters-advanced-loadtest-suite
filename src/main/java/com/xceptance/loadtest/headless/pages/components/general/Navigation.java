package com.xceptance.loadtest.headless.pages.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;

public class Navigation implements Component
{
    public final static Navigation instance = new Navigation();

    /**
     * Lookup the navigation.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#sg-navbar-collapse");
    }

    /**
     * Indicates if this component exists
     *
     * @return
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public LookUpResult getTopCategories()
    {
        return filterinvalidLinks(locate().byCss("ul.nav > li.nav-item:not(.d-lg-none) > a.nav-link"));
    }

    public LookUpResult getCategories()
    {
        return filterinvalidLinks(locate().byCss("ul.dropdown-menu li.dropdown-item a.dropdown-link"));
    }

    private LookUpResult filterinvalidLinks(final LookUpResult links)
    {
        return links.filter(Page.VALIDLINKS)
                        .discard(Context.configuration().filterCategoryUrls.unweightedList(),
                        e -> e.getAttribute("href"));
    }
}
