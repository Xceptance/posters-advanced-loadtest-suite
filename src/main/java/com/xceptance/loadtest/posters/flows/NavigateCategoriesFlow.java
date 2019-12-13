package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.ClickACategory;
import com.xceptance.loadtest.posters.actions.catalog.ClickATopCategory;
import com.xceptance.loadtest.posters.pages.catalog.ProductListingPage;

/**
 * Browse a (top) category.
 */
public class NavigateCategoriesFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        if (Context.configuration().topCategoryBrowsing.random())
        {
            new ClickATopCategory().run();

            // In case this is not really ending up anywhere useful, give us another direct category
            if (ProductListingPage.instance.is() == false)
            {
                new ClickACategory().run();
            }
        }
        else
        {
            new ClickACategory().run();
        }

        return true;
    }
}