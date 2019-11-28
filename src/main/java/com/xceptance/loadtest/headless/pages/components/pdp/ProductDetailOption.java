package com.xceptance.loadtest.headless.pages.components.pdp;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlMapper;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.DOMUtils;
import com.xceptance.loadtest.headless.jsondata.ProductJSON;

public class ProductDetailOption extends ProductDetail
{
    public static final String TYPE_LOCATOR = ".product-options";

    public static final String ITEM_LOCATOR = ".product-option";

    public static final ProductDetailOption instance = new ProductDetailOption();

    @Override
    public LookUpResult locate()
    {
        return locate(Page.getBody());
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    @Override
    public LookUpResult locate(final HtmlElement in)
    {
        final LookUpResult main = HPU.find().in(in).byCss(".container" + ProductDetail.TYPE_LOCATOR + "[data-pid]");

        if (main.byCss(TYPE_LOCATOR).exists())
        {
            return main;
        }
        else
        {
            return LookUpResult.DOESNOTEXIST;
        }
    }

    @Override
    public boolean exists(final HtmlElement in)
    {
        return locate(in).exists();
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        return locate().all();
    }

    /**
     * Check me
     */
    @Override
    public boolean isSetItem(final HtmlElement element)
    {
        return DOMUtils.hasClassLocators(element, ProductDetail.TYPE_LOCATOR, ProductDetailSet.ITEM_LOCATOR)
                        && element.hasAttribute("data-pid")
                        // don't check for product-options, the S makes the difference, because
                        // we might have that in the html accidentally aka in sets
                        && HPU.find().in(element).byCss(ITEM_LOCATOR).exists();
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement item)
    {
        // ok, we have to rescue the add to cart url
        final String addToCartUrl = getAddToCartUrl(item);

        final HtmlRenderer renderer = Page.renderHtml().json(response, ProductJSON.class, "pv");
        renderer.template("/templates/pdp/product-detail-option.ftlh");

        final HtmlMapper mapper = Page.mapHtml().html(renderer).in(item)
                        .byCSS(".product-options")
                        .byCSS(".product-availability")
                        .byCSS(".promotions")
                        .byCSS(".prices-add-to-cart-actions");

        // do it
        mapper.map();

        // put the add to cart url back, because this data is not in our json
        HPU.find().in(item).byCss("input.add-to-cart-url").asserted().first().setAttribute("value", addToCartUrl);

        return item;
    }

    /**
     * A list of all attributes or an empty list
     *
     * @return a list of all attribute elements in the DOM or empty if none
     */
    public LookUpResult getOptionAttributes(final HtmlElement productDetail)
    {
        return HPU.find().in(productDetail).byCss("div" + ITEM_LOCATOR + "[data-option-id]");
    }

    public String getOptionId(final HtmlElement option)
    {
        return option.getAttribute("data-option-id");
    }

    public String getToBeSelectedOptionUrl(final HtmlElement option)
    {
        return option.getAttribute("value");
    }

    public LookUpResult getUnselectedOptions(final HtmlElement optionAttribute)
    {
        return HPU.find().in(optionAttribute).byCss("select.options-select option:not([selected])");
    }

    public LookUpResult getSelectedOption(final HtmlElement optionAttribute)
    {
        return HPU.find().in(optionAttribute).byCss("select.options-select option[selected]");
    }

    public String getSelectedOptionValueId(final HtmlElement optionAttribute)
    {
        return getSelectedOption(optionAttribute).single().getAttribute("data-value-id");
    }
}
