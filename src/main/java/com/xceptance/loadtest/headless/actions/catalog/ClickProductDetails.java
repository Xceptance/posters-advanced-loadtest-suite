package com.xceptance.loadtest.headless.actions.catalog;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.headless.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.headless.pages.catalog.QuickviewPage;
import com.xceptance.loadtest.headless.pages.components.pdp.ProductDetail;
import com.xceptance.loadtest.headless.pages.components.plp.ProductTile;


/**
 * Open the product detail page for a randomly chosen product.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ClickProductDetails extends PageAction<ClickProductDetails>
{
    /**
     * Product link, randomly chosen from PLP.
     */
    private HtmlElement productLink;

    /**
     * Get us the type for later, mainly to save time or debug
     */
    private ProductDetailPage<? extends ProductDetail> pdpType;

    // true if we want to reduce the amount of products considered for opening
    private final boolean reduceProductTileScope;

    /**
     * Constructor
     *
     * @param reduceProductTileScope
     *            when true, focus on the later loaded products, false will focus on all equally
     */
    public ClickProductDetails(final boolean reduceProductTileScope)
    {
        this.reduceProductTileScope = reduceProductTileScope;
    }

    @Override
    public void precheck()
    {
        // close existing quick view, this is safe, just to make sure we do not find later on
        // links in the quick view
        QuickviewPage.instance.quickview.closeQuickview();

        // get a product
        final LookUpResult productTilesResult = ProductListingPage.instance.productGrid.getProducts().asserted("No product tiles found");

        final List<HtmlElement> productTiles = ProductTile.getPDPLinks(productTilesResult)
                        .discard(Context.configuration().filterProductUrls.unweightedList()
                                        .stream().filter(s -> StringUtils.isNotBlank(s))
                                        .collect(Collectors.toList()),
                                        e -> e.getAttribute("href"))
                        .asserted("No product link in tiles found").all();

        // make sure after displaymore, we do not focus on all products, rather on the latest
        // products added
        if (reduceProductTileScope)
        {
            productLink = RandomUtils.weightedRandomEntry(productTiles, Context.configuration().numberOfPLPTiles);
        }
        else
        {
            productLink = RandomUtils.randomEntry(productTiles);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Open the product page, optionally permit debugging, when enabled and
        // also works only in dev
        loadDebugUrlOrElse("url goes here").loadPageByClick(productLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        pdpType = ProductDetailPage.identify();
    }

    /**
     * Return us the product type identified
     */
    public ProductDetailPage<? extends ProductDetail> getType()
    {
        return pdpType;
    }
}
