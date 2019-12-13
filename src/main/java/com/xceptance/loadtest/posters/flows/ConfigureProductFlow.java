package com.xceptance.loadtest.posters.flows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.catalog.ConfigureProductQuantity;
import com.xceptance.loadtest.posters.actions.catalog.ConfigureProductVariation;
import com.xceptance.loadtest.posters.pages.catalog.ProductDetailPage;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Configures all product items randomly accoring to the contained attributes and quantities.
 */
public class ConfigureProductFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean execute() throws Throwable
    {
// TODO change from ProductDetailPage.instance to specific ProductPage implementation
    	
        // Get all configurable product items of the current product detail page
        final List<HtmlElement> items = ProductDetailPage.instance.getConfigurableProductItems();

        for (HtmlElement item : items)
        {
            final SafetyBreak safetyBreak = new SafetyBreak(5);

            while (!safetyBreak.reached())
            {
            	// Variation Attributes
                configureVariation(ProductDetailPage.instance, item);

                // Quantity
                new ConfigureProductQuantity(ProductDetailPage.instance, item).runIfPossible();
            }
        }

        return true;
    }
    
    private void configureVariation(final ProductDetailPage page, final HtmlElement productItem) throws Throwable
    {
        final List<HtmlElement> variationAttributes = page.getVariationAttributes(productItem);
        
        if (variationAttributes.isEmpty())
        {
        	// No variation attributes found
        	return;
        }
        
// TODO        
        // Create a list of attribute names from variation attributes
        final List<String> variationAttributeNames = variationAttributes.stream().map(s -> s.getAttribute("data-attr")).collect(Collectors.toList());

        // Randomize the attribute names
        if (variationAttributeNames.size() > 1)
        {
            Collections.shuffle(variationAttributeNames, XltRandom.getRandom());
        }

        // Configure each variation attribute via its name
        for (final String variationAttributeName : variationAttributeNames)
        {
            new ConfigureProductVariation(page, productItem, variationAttributeName).runIfPossible();
        }
    }
}
