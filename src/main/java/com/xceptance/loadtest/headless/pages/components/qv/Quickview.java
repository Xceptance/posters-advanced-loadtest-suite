package com.xceptance.loadtest.headless.pages.components.qv;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DataUtils;

public class Quickview implements Component
{
    public final static Quickview instance = new Quickview();

    @Override
    public LookUpResult locate()
    {
        // very precise to make sure we do not hit anything that accidentally
        // used .product-detail
        return Page.find().byCss("#quickViewModal .quick-view-dialog");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Be careful when using close, it removes this from the page and so later actions on quickview
     * cannot run. Use hide instead when debugging.
     *
     * @return true if it could be closed, false otherwise
     */
    public boolean closeQuickview()
    {
        if (exists())
        {
            // removing this element is not quite right but in the first place
            // there is none, so first view opens it, later it becomes invisible
            // only but we cannot emulate that properly
            Page.find().byCss("#quickViewModal").first().remove();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Just hide it for better debugging without killing functionality on it
     *
     * @return true if it was hidden, false otherwise
     *
     */
    public boolean hideQuickview()
    {
        if (Context.isLoadTest == false)
        {
            if (exists())
            {
                // hide out quickview during debugging
                Page.find().byCss("#quickViewModal").first().setAttribute("style", "display: none;");
                return true;
            }
        }

        return false;
    }

    public LookUpResult getAllProductDetails()
    {
        return locate().byCss(".product-detail");
    }

    public LookUpResult getFooter()
    {
        return locate().byCss(".modal-footer");
    }

    public List<HtmlElement> getAllConfigurableProductDetails()
    {
        // in quick view, we do not have the detail around the entire set, darn, hence this
        // is quick view specific
        return locate().byCss(".product-detail").all();
    }

    public boolean isSet()
    {
        // quick view is different again
        return locate().byCss(".product-set").exists();
    }

    public boolean isBundle()
    {
        // quick view is different again
        return locate().byCss(".product-bundle").exists();
    }

    public LookUpResult getProductBundles()
    {
        return locate().byCss(".product-bundle.product-detail");
    }

    public LookUpResult getGlobalAddToCartButton()
    {
        return getFooter().byCss("button.add-to-cart-global");
    }

    public String getAddToCartUrlOfProductSet(final HtmlElement productSet)
    {
        return getFooter().byCss(".add-to-cart-url").single().getAttribute("value");
    }

    public String getAddToCartUrlOfBundle(final HtmlElement productBundle)
    {
        return getFooter().byCss(".add-to-cart-url").single().getAttribute("value");
    }

    public LookUpResult getQuantityField(final HtmlElement productDetail)
    {
        return getFooter().byCss(".quantity-select");
    }

    public int getQuantity(final HtmlElement productDetail)
    {
        final HtmlElement element = getQuantityField(productDetail).byCss("option[selected]").first();

        // well, we assume one when nothing is selected, that select is not an attr right now, but
        // JS magic
        return element == null ? 1 : DataUtils.toInt(element.getAttribute("value"));
    }
}
