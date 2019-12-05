package posters.flows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.xlt.api.util.XltRandom;

import posters.actions.catalog.ConfigureBundleQuantity;
import posters.actions.catalog.ConfigureProductOption;
import posters.actions.catalog.ConfigureProductQuantity;
import posters.actions.catalog.ConfigureProductVariation;
import posters.pages.catalog.ProductDetailPage;
import posters.pages.catalog.QuickviewPage;
import posters.pages.components.pdp.ProductDetail;
import posters.pages.components.pdp.ProductDetailOption;
import posters.pages.components.pdp.ProductDetailSet;
import posters.pages.components.pdp.ProductDetailVariation;

/**
 * Selects a random available product variation.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean execute() throws Throwable
    {
        // not yet possible
        if (QuickviewPage.instance.is())
        {
            // we cannot do quick view right now, sorry
            return false;
        }

        // we have to be on a detail page
        if (ProductDetailPage.instance.is() == false)
        {
            return false;
        }

        // ok, we are on a PDP, now let's find out what PDP
        final ProductDetailPage<? extends ProductDetail> pdp = ProductDetailPage.identify();

        // get all product details on that page, might be set details as well
        final List<HtmlElement> items = pdp.getConfigurableItems();

        // do all configurable product items on that page
        for (HtmlElement item : items)
        {
            final SafetyBreak safetyBreak = new SafetyBreak(5);

            while (!safetyBreak.reached())
            {
                // do variations if we have a variation page
                if (pdp.getIfPresentVariationPage().isPresent())
                {
                    // variation
                    // get it as fix type
                    final ProductDetailPage<ProductDetailVariation> page = pdp.getIfPresentVariationPage().get();
                    item = configureVariation(page, page.productDetail, item);
                }
                else if (pdp.getIfPresentOptionPage().isPresent()) // do options
                {
                    // variation
                    // get it as fix type
                    item = configureOption(item);
                }
                else if (pdp.getIfPresentSetPage().isPresent())  // set page
                {
                    // the trick is now that each item is a regular product and will be configured
                    // like one
                    final ProductDetailPage<ProductDetailSet> page = pdp.getIfPresentSetPage().get();

                    if (ProductDetailOption.instance.isSetItem(item))
                    {
                        item = configureOption(item);
                    }
                    else if (ProductDetailVariation.instance.isSetItem(item))
                    {
                        item = configureVariation(page, ProductDetailVariation.instance, item);
                    }
                    else
                    {
                        // nothing to do, if we got a bundle or set here, we will fail
                    }
                }

                // ok, fun fact, bundles are currently without any configuration, hence we do qty
                // only
                if (pdp.getIfPresentBundlePage().isPresent())
                {
                    // product bundle qty is currently a noop because it is not implemented with stock
                    // checks
                    // or other stuff, hence we do a select here only
                    new ConfigureBundleQuantity(pdp.getIfPresentBundlePage().get(), item).runIfPossible();
                }
                else
                {
                    // quantity
                    final ConfigureProductQuantity confQty = new ConfigureProductQuantity(pdp, item);
                    if (confQty.runIfPossible().isPresent())
                    {
                        // it can happen that the product detail was replaced during rendering and not
                        // just a little bit of content in it, so get the potentially new element
                        item = confQty.getItem();
                    }
                }

                // // ok, check orderable status and availability if we have to do that all again
                if (pdp.productDetail.isOrderable(item) && pdp.productDetail.isAvailable(item))
                {
                    // stop the configuration of this item and jump to the next one
                    break;
                }
            }
        }

        // ok, in case we are a set page, we set the summed up state
        // if this is a set page, we have to "sum up" all states for the global add to cart button
        if (pdp.getIfPresentSetPage().isPresent())
        {
            // this is not an action, nothing happens here from the user point of view,
            // that is just emulated JS stuff
            // Attention: This happens after any action, so you are not going to see that in the
            // result browser output, except when the next action will write out a new state.
            pdp.getIfPresentSetPage().get().productDetail.updateCartButtonState();
        }

        return true;
    }
    
    private HtmlElement configureVariation(
            final ProductDetailPage<? extends ProductDetail> page,
            final ProductDetailVariation productDetail,
            final HtmlElement itemElement) throws Throwable
    {
        final List<HtmlElement> variationAttributes = productDetail.getVariationAttributes(itemElement).all();
        
        // we have to change that later possibly
        HtmlElement item = itemElement;
        
        // don't do anything if we don't have variations
        if (variationAttributes.isEmpty() == false)
        {
            // get the values of them, because we have to reselect these to stay current
            // with the DOM
            final List<String> variationAttributeNames = variationAttributes.stream()
                            .map(s -> s.getAttribute("data-attr"))
                            .collect(Collectors.toList());
        
            // make sure we do not do the same order again and again
            if (variationAttributeNames.size() > 1)
            {
                Collections.shuffle(variationAttributeNames, XltRandom.getRandom());
            }
        
            for (final String variationAttributeName : variationAttributeNames)
            {
                final ConfigureProductVariation configure = new ConfigureProductVariation(page, productDetail, item, variationAttributeName);
                configure.runIfPossible();
        
                // it can happen that the product detail was replaced during rendering
                // and
                // not just a little bit of content in it, so
                // get the potentially new element
                item = configure.getItem();
        
                // we need to identify the page if we are still on a pdp, just in case
                final ProductDetailPage<? extends ProductDetail> pdp = ProductDetailPage.identify();
        
                if (pdp.productDetail.isOrderable(item) && pdp.productDetail.isAvailable(item))
                {
                    // stop the configuration of this item and jump to the next one
                    break;
                }
            }
        }
        else
        {
            // does not make sense
            Assert.fail("No variation attributes on variation page found... fishy!");
        }
        
        return item;
    }

    private HtmlElement configureOption(final HtmlElement itemElement) throws Throwable
    {
        final ProductDetailPage<ProductDetailOption> pdp = ProductDetailPage.getProductDetailOptionPage();
        
        final List<HtmlElement> optionAttributes = pdp.productDetail.getOptionAttributes(itemElement).all();
        
        HtmlElement item = itemElement;
        
        for (final HtmlElement optionAttribute : optionAttributes)
        {
            final ConfigureProductOption configure = new ConfigureProductOption(pdp, item, optionAttribute);
            configure.runIfPossible();
        
            // it can happen that the product detail was replaced during rendering and not
            // just a little bit of content in it, so get the potentially new element
            item = configure.getItem();
        }
        
        return item;
    }
}
