package com.xceptance.loadtest.posters.flows;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.catalog.ConfigureProductVariation;
import com.xceptance.loadtest.posters.pages.catalog.ProductDetailPage;

/**
 * Configures all product items randomly according to the contained attributes and quantities.
 */
public class ConfigureProductFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean execute() throws Throwable
    {
        // Get all product items of the current product detail page
        final List<HtmlElement> productItems = ProductDetailPage.instance.getProductItems();
        for (HtmlElement productItem : productItems)
        {
            final SafetyBreak safetyBreak = new SafetyBreak(5);
            while (!safetyBreak.reached() && true /* TODO check for product availability */)
            {
            	if(new ConfigureProductVariation(productItem).runIfPossible().isPresent())
            	{
            		break;
            	}
            }
        }

        return true;
    }
}
