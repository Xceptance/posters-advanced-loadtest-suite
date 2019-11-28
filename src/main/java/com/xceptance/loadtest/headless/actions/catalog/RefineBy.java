package com.xceptance.loadtest.headless.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.headless.pages.catalog.QuickviewPage;
import com.xceptance.loadtest.headless.pages.components.plp.RefinementBar;

/**
 * Performs a refinement, excluding category refinements.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 * @author Rene Schwietzke (Xceptance Software Technologies GmbH)
 */
public class RefineBy extends AbstractRefine<RefineBy>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
        super.precheck();

        // Close existing quick view. This is safe, just to make sure we do not find later on links in the quick view.
        QuickviewPage.instance.quickview.closeQuickview();

        // Randomly select a non-category refinement type
        final HtmlElement refinement = ProductListingPage.instance.refinementBar.getNonCategoryRefinements().asserted("No non-category refinements found").random();

        // Randomly select the refinement option with contained refinement link
        refinementLink = RefinementBar.linkFromRefinement(refinement).asserted("No specific refinement link found in determined refinement section").random();
    }
}