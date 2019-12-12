package com.xceptance.loadtest.posters.actions.catalog;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.posters.pages.components.plp.ProductTile;

/**
 * Opens a product detail page of a randomly chosen product. 
 */
public class ClickProductDetails extends PageAction<ClickProductDetails>
{
    private HtmlElement productLink;

    @Override
    public void precheck()
    {
        // Get a product
        final LookUpResult productTilesResult = ProductListingPage.instance.productGrid.getProducts().asserted("No product tiles found");

        // Apply product link filter
        final List<HtmlElement> productTiles = ProductTile.getPDPLinks(productTilesResult)
									                        .discard(Context.configuration().filterProductUrls.unweightedList()
									                                        .stream().filter(s -> StringUtils.isNotBlank(s))
									                                        .collect(Collectors.toList()),
									                                        e -> e.getAttribute("href"))
									                        .asserted("No product link in tiles found").all();

        productLink = RandomUtils.randomEntry(productTiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(productLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();
        
        // TODO validate PDP
    }
}
