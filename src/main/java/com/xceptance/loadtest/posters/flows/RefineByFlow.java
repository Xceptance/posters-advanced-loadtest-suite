package com.xceptance.loadtest.posters.flows;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.catalog.ClickProductDetails;
import com.xceptance.loadtest.posters.actions.catalog.ClickQuickView;
import com.xceptance.loadtest.posters.actions.catalog.DisplayMore;
import com.xceptance.loadtest.posters.pages.catalog.CategoryLandingPage;
import com.xceptance.loadtest.posters.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.posters.pages.catalog.QuickviewPage;
import com.xceptance.loadtest.posters.pages.search.SearchNoResultPage;

/**
 * Browses based on reached PLP or PDPs
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class RefineByFlow extends Flow
{
    private final SafetyBreak breaker = new SafetyBreak(5);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        while (!breaker.reached())
        {
            // Process the results (refine, sort, page, change page size).
            if (ProductListingPage.instance.is())
            {
                // refine the hell out of the displayed items
                new ProductListingPagesFlow().run();

                // View a product's details if this option is available.
                if (ProductListingPage.instance.is())
                {
                    viewProductDetails();
                }
            }

            // ended on PDP/QV?
            if (ProductDetailPage.instance.is() || QuickviewPage.instance.is())
            {
                // all good, that round is done
                break;
            }
            else if (CategoryLandingPage.instance.is())
            {
                // Nothing to do here, landing page
                break;
            }
            else if (SearchNoResultPage.instance.is())
            {
                // We got no product hits and did not open the product details. So there's
                // nothing to do.
                break;
            }
            else
            {
                // We already logged an event if we've refined to a page without any hits.
                Assert.fail("Browsing flow ended on unknown page.");
            }
        }

        return true;
    }

    /**
     * Just do the product handling
     * @throws Throwable
     */
    private void viewProductDetails() throws Throwable
    {
        // don't view more product than we actually have
        final int itemCount = ProductListingPage.instance.itemCount.getItemCount();

        // first keep the information if we have done a view more before, because
        // that means we might have to focus more on the last set of products than the first
        final boolean sawDisplayMoreBefore = Context.getCurrentAction() instanceof DisplayMore;

        // Decide to open details as quick view or separate details
        // page.
        if (Context.configuration().quickViewProbability.random())
        {
            // we might view several products
            int quickViewCount = Context.configuration().quickViewCount.random();
            quickViewCount = quickViewCount > itemCount ? itemCount : quickViewCount;

            for (int i = 0; i < quickViewCount; i++)
            {
                // Show details as quick view
                new ClickQuickView(sawDisplayMoreBefore).runIfPossible();
            }
        }
        else
        {
            // we might view several products
            int viewCount = Context.configuration().productViewCount.random();
            viewCount = viewCount > itemCount ? itemCount : viewCount;

            // keep the context
            final PageAction<?> currentAction = Context.getCurrentAction();
            for (int i = 0; i < viewCount; i++)
            {
                if (i != 0)
                {
                    // if we are not doing this for the first time, emulate a
                    // page back in the browser or you can also see that as
                    // multi-tab opening
                    Context.resetCurrentAction(currentAction);
                }

                // Show details on separate page.
                // might fail due to filter matching
                if (new ClickProductDetails(sawDisplayMoreBefore).runIfPossible().isPresent() == false)
                {
                    // if we couldn't do it, we can stop, nothing will change in
                    // this loop
                    break;
                }
            }
        }
    }
}
