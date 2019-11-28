package com.xceptance.loadtest.headless.pages.components.pdp;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.SubComponent;
import com.xceptance.loadtest.api.util.DataUtils;

public class ProductDetailBundleItem implements SubComponent
{
    public final static ProductDetailBundleItem instance = new ProductDetailBundleItem();

    @Override
    public LookUpResult locate(final HtmlElement in)
    {
        return HPU.find().in(in).byCss(".product-detail.bundle-item");
    }

    @Override
    public boolean exists(final HtmlElement in)
    {
        return locate(in).exists();
    }

    public int getQuantity(final HtmlElement item)
    {
        final String value = HPU.find().in(item).byCss(".quantity[data-quantity]").asserted().single().getAttribute("data-quantity");
        return DataUtils.toInt(value);
    }

    public String getPid(final HtmlElement item)
    {
        return item.getAttribute("data-pid");
    }
}
