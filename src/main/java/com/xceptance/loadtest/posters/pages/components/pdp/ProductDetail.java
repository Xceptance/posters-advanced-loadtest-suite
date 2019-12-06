package com.xceptance.loadtest.posters.pages.components.pdp;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.pages.components.SubComponent;
import com.xceptance.loadtest.api.util.DataUtils;

public abstract class ProductDetail implements Component, SubComponent
{
    public static final String TYPE_LOCATOR = "#addToCartForm";

    public abstract HtmlElement render(final String response, final HtmlElement productDetail);

    /**
     * Indicates if this product detail is a set item.
     *
     * @param element
     *            The product detail element to check
     * @return true if this element is a set item, false otherwise.
     */
    public abstract boolean isSetItem(HtmlElement element);

    /**
     * Return the update url for quanity elements, this is still pretty generic, so this code fits
     * here. If you need something specific, overload it later.
     *
     * @param qtyElement
     *            the elemtent to query
     * @return the url to fire an quantity update with
     */
    public String getQuantityUrl(final HtmlElement qtyElement)
    {
        // data-url
        final String url = qtyElement.getAttribute("data-url");

        if (url == null || url.length() == 0)
        {
            Assert.fail("No quantity update url of product detail found");
        }

        return url;
    }

    public String getAddToCartUrl(final HtmlElement item)
    {
        return HPU.find().in(item).byCss("input.add-to-cart-url").asserted().first().getAttribute("value");
    }

    /**
     * Return everything that we can configure. Location per product type varies.
     *
     * @return a list of configurable products, a lookup result might or might not be hard to handle
     *         due to several dependencies of set, bundle, variation and so forth, the list is empty
     *         if nothing could be found
     */
    public abstract List<HtmlElement> getConfigurableItems();

    public boolean isOrderable(final HtmlElement productDetail)
    {
        final HtmlElement availability = HPU.find()
                        .in(productDetail).byCss(".availability[data-ready-to-order]")
                        .asserted("Ready to order information not available")
                        .single();
        final String attribute = availability.getAttribute("data-ready-to-order");

        return Boolean.valueOf(attribute);
    }

    public boolean isAvailable(final HtmlElement productDetail)
    {
        final HtmlElement availability = HPU.find()
                        .in(productDetail).byCss(".availability[data-available]")
                        .asserted("Availability information not available")
                        .single();
        final String attribute = availability.getAttribute("data-available");

        return Boolean.valueOf(attribute);
    }

    public LookUpResult getUnselectedQuantities(final HtmlElement item)
    {
        return HPU.find().in(item).byCss("select.quantity-select option:not([selected])");
    }

    public LookUpResult getSelectedQuantity(final HtmlElement item)
    {
        return HPU.find().in(item).byCss("select.quantity-select option[selected]");
    }

    public int getQuantity(final HtmlElement item)
    {
        final HtmlElement element = getSelectedQuantity(item).asserted().first();
        return DataUtils.toInt(element.getAttribute("value"));
    }

    public String getPid(final HtmlElement item)
    {
        return item.getAttribute("data-pid");
    }

    public void updatePid(final HtmlElement item, final String newValue)
    {
        item.setAttribute("data-pid", newValue);
    }
}