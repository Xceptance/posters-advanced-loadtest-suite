package com.xceptance.loadtest.headless.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.catalog.ClickACategory;
import com.xceptance.loadtest.headless.actions.catalog.ClickACategorySlot;
import com.xceptance.loadtest.headless.actions.catalog.ClickATopCategory;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;

/**
 * Browse the catalog and view product details.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class CategoryFlow extends Flow
{
    /**
     * Browse the the catalog, a category and refine potentially
     */
    @Override
    public boolean execute() throws Throwable
    {
        // Click top or sub category from the top menu.
        if (Context.configuration().topCategoryBrowsing.random())
        {
            new ClickATopCategory().run();

            // load from a advertising slot
            new ClickACategorySlot().runIfPossible();

            // in case this is not really ending up anywhere useful, give us another
            // direct category
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