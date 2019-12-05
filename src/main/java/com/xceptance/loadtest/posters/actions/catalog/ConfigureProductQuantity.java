package com.xceptance.loadtest.posters.actions.catalog;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DataUtils;
import com.xceptance.loadtest.posters.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.pages.components.pdp.ProductDetail;

/**
 * Chooses a random product configuration.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductQuantity extends AbstractConfigureProduct<ConfigureProductQuantity>
{
    /**
     * What quantity to select
     */
    private int quantityToSelect;

    /**
     * Where are we
     */
    private final ProductDetailPage<? extends ProductDetail> pdp;

    /**
     * Constructor
     *
     * @param quantity
     *            the quantity to select later if needed
     */
    public ConfigureProductQuantity(final ProductDetailPage<? extends ProductDetail> pdp, final HtmlElement item)
    {
        super(item);
        this.pdp = pdp;
    }

    @Override
    public void precheck()
    {
        // what we want
        this.quantityToSelect = Context.configuration().cartProductQuantity.random();

        // ok, we need the current qty element first
        final HtmlElement currentSelectedQuantity = pdp.productDetail.getSelectedQuantity(item).asserted().single();

        // it there anything selected?
        if (currentSelectedQuantity != null)
        {
            // get currently selected quantity
            final int quantitySelected = pdp.productDetail.getQuantity(item);
            Assert.assertFalse("Quantity to select is the one we already have", quantityToSelect == quantitySelected);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final List<HtmlElement> unSelectedQuantities = pdp.productDetail.getUnselectedQuantities(item).asserted("No unselected quantities found").all();

        HtmlElement quantityToSelectElement = null;

        // ok, get us the right element for the update
        // that matches our desired quantity
        for (final HtmlElement q : unSelectedQuantities)
        {
            final int value = DataUtils.toInt(q.getAttribute("value"));
            if (value == this.quantityToSelect)
            {
                quantityToSelectElement = q;
                break;
            }
        }

        // did we get one?
        if (quantityToSelectElement == null)
        {
            // darn
            Assert.fail("Failed finding the right quantity to select");
        }

        final String url = pdp.productDetail.getQuantityUrl(quantityToSelectElement);

        // fetch the data
        final WebResponse response = call(url);

        // prepare the DOM
        item = pdp.productDetail.render(response.getContentAsString(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        pdp.validate();
    }
}
