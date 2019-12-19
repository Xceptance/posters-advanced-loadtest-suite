package com.xceptance.loadtest.posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.catalog.ConfigureProductVariation;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;

/**
 * Configures the product randomly until the page shows the product as in stock/available.
 * 
 * @author Xceptance Software Technologies
 */
public class ConfigureProductFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean execute() throws Throwable
    {
        final SafetyBreak safetyBreak = new SafetyBreak(5);
        do
        {
        	new ConfigureProductVariation().runIfPossible();
        }
        while(!safetyBreak.reached() && !ProductDetailPage.instance.isAvailable());

        return true;
    }
}