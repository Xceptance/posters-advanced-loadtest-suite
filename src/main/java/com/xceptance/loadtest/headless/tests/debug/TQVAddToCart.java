package com.xceptance.loadtest.headless.tests.debug;

import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.headless.actions.cart.AddToCart;
import com.xceptance.loadtest.headless.actions.catalog.ClickQuickView;
import com.xceptance.loadtest.headless.actions.catalog.Search;
import com.xceptance.loadtest.headless.flows.ConfigureProductFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;

/**
 * Simple test to get all possible add to cart operations tested easily From ViewView, not supposed
 * to work right now
 */
public class TQVAddToCart extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Set
        new Search("random selection", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Bundle
        new Search("sony-ps3-bundle", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // standard product
        new Search("lucasarts-star-wars", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Variation one attr
        new Search("25594767", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Variation many attr
        new Search("73910532", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Option
        new Search("sony-kdl-40w4100", SearchOption.HITS).run();
        new ClickQuickView(false).run();
        new ConfigureProductFlow().run();
        new AddToCart().run();
    }
}
