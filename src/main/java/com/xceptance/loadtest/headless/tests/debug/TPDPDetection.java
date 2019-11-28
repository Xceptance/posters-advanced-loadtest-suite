package com.xceptance.loadtest.headless.tests.debug;

import com.xceptance.loadtest.api.data.SearchOption;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.headless.actions.catalog.ClickProductDetails;
import com.xceptance.loadtest.headless.actions.catalog.Search;
import com.xceptance.loadtest.headless.flows.VisitFlow;


/**
 * Simple test to get all possible add to cart operations tested easily
 */
public class TPDPDetection extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Set
        new Search("random selection", SearchOption.HITS).run();
        new ClickProductDetails(false).runAndGet().getType().getIfPresentSetPage().get();

        // Variation one attr
        new Search("25594767", SearchOption.HITS).run();
        // this will get us the variation type or throw an exception of not, using the
        // Optional.get(), fancy, isn't it?
        new ClickProductDetails(false).runAndGet().getType().getIfPresentVariationPage().get();

        // Variation two attr
        new Search("25604455", SearchOption.HITS).run();
        // this will get us the variation type or throw an exception of not, using the
        // Optional.get(), fancy, isn't it?
        new ClickProductDetails(false).runAndGet().getType().getIfPresentVariationPage().get();

        // Variation many attr
        new Search("73910532", SearchOption.HITS).run();
        new ClickProductDetails(false).runAndGet().getType().getIfPresentVariationPage().get();

        // Bundle
        new Search("sony-ps3-bundle", SearchOption.HITS).run();
        new ClickProductDetails(false).runAndGet().getType().getIfPresentBundlePage().get();

        // standard product
        new Search("lucasarts-star-wars", SearchOption.HITS).run();
        new ClickProductDetails(false).runAndGet().getType().getIfPresentStandardPage().get();

        // Option
        new Search("sony-kdl-40w4100", SearchOption.HITS).run();
        new ClickProductDetails(false).runAndGet().getType().getIfPresentOptionPage().get();
    }
}
