package com.xceptance.loadtest.posters.tests.helpers;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.ProductDetailPageLanding;
import com.xceptance.loadtest.posters.flows.SingleProductOrderFlow;

/**
 * Open the landing page, register account if necessary and browse the catalog to a random product. Configure this
 * product and add it to the cart. Finally process the checkout including the final order placement step.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class TDirectProductOrder extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        Context.get().data.attachAccount();

        new ProductDetailPageLanding(Context.configuration().directOrderUrl).run();

        new SingleProductOrderFlow().run();
    }
}
