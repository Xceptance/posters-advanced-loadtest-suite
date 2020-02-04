package com.xceptance.loadtest.posters.tests.helpers;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.ProductDetailPageLanding;
import com.xceptance.loadtest.posters.flows.SingleProductOrderFlow;

/**
 * Open a specific product detail page, configure the product, add it to cart and the proceed through checkout.
 * 
 * @author Xceptance Software Technologies
 */
public class TDirectProductOrder extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Directly open the given product detail page
        new ProductDetailPageLanding(Context.configuration().directOrderUrl).run();

        // Configure, add to cart and order the product
        new SingleProductOrderFlow().run();
    }
}