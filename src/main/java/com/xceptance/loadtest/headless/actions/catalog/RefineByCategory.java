package com.xceptance.loadtest.headless.actions.catalog;

import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

/**
 * Clicks a category link at the refinements panel.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class RefineByCategory extends AbstractRefine<RefineByCategory>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
        super.precheck();

        // Randomly choose unselected refinement that contains our request URL
        refinementLink = ProductListingPage.instance.refinementBar.getUnSelectedCategoryRefinements().asserted("No category refinements found").random();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Do not expect PLP but whatever page, because we can basically hit anything (PDP, PLP, LandingPage)
        GeneralPages.instance.validate();
    }
}