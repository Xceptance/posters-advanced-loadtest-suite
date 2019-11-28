package com.xceptance.loadtest.headless.actions.catalog;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.api.util.RandomUtils;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.headless.pages.catalog.QuickviewPage;
import com.xceptance.loadtest.headless.pages.components.plp.ProductTile;

/**
 * Open the quick view page for a randomly chosen product.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ClickQuickView extends AjaxAction<ClickQuickView>
{
    private HtmlElement singleProductTile;

    // true if we want to reduce the amount of products considered for opening
    private final boolean reduceProductTileScope;

    /**
     * Constructor
     *
     * @param reduceProductTileScope
     *            when true, focus on the later loaded products, false will focus on all equally
     */
    public ClickQuickView(final boolean reduceProductTileScope)
    {
        this.reduceProductTileScope = reduceProductTileScope;
    }

    @Override
    public void precheck()
    {
        // close existing quick view, this is safe
        QuickviewPage.instance.quickview.closeQuickview();

        final LookUpResult productTilesResult = ProductListingPage.instance.productGrid.getProducts().asserted("No product tiles found");

        final List<HtmlElement> productTiles = ProductTile.getQuickviewLinks(productTilesResult)
                        .discard(Context.configuration().filterProductUrls.unweightedList()
                                        .stream().filter(s -> StringUtils.isNotBlank(s))
                                        .collect(Collectors.toList()),
                                        e -> e.getAttribute("href"))
                        .asserted("No quickview link in tiles found").all();

        // make sure after displaymore, we do not focus on all products, rather on the latest
        // products added
        if (reduceProductTileScope)
        {
            singleProductTile = RandomUtils.weightedRandomEntry(productTiles, Context.configuration().numberOfPLPTiles);
        }
        else
        {
            singleProductTile = RandomUtils.randomEntry(productTiles);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Ensure that a quick view section is present.
        final HtmlElement quickviewContainer = QuickviewPage.instance.getQuickviewContainer();

        // Quick view URLs have attached certain parameters. Prepare the URL and request the quick view data to update
        // the page.
        final WebResponse response = new HttpRequest().XHR()
                        .url(singleProductTile.getAttribute("href"))
                        .assertContent("Nothing came back from QuickView.", true, HttpRequest.NOT_BLANK)
                        .fire();

        // get the parent node of the quick view element and from there the product tile
        final HtmlElement parentNode = singleProductTile.getEnclosingElement("div");
        final HtmlElement productTile = parentNode.getEnclosingElement("div");

        Page.renderHtmlAndMap()
                        .template("/templates/quickview.ftlh")
                        .data("link", ProductTile.getPDPLink(productTile).single().getAttribute("href"))
                        .html(response.getContentAsString())
                        .replace(quickviewContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this is not a page load, hence we only check that change in the page

        // check we are still on a grid view page
        Assert.assertTrue("Quickview not found", QuickviewPage.instance.is());
    }
}
