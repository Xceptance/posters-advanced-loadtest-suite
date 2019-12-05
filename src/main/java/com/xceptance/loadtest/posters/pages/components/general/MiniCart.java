package com.xceptance.loadtest.posters.pages.components.general;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DataUtils;

public class MiniCart implements Component
{
    public final static MiniCart instance = new MiniCart();

    @Override
    public LookUpResult locate()
    {
        return Header.instance.locate().byCss("#miniCartMenu");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Returns whether or not the mini cart is empty.
     *
     * @return <code>true</code> if the mini cart is empty, <code>false</code>
     *         otherwise
     */
    public boolean isEmpty()
    {
        return getQuantity() == 0;
    }

    public LookUpResult getQuantityElement()
    {
        return locate().byCss(".cartMiniProductCounter .value");
    }

    public int getQuantity()
    {
        // might exist more than once
        final HtmlElement qty = getQuantityElement().asserted().first();
        return DataUtils.toInt(qty.asText());
    }

    public int getLineItemCount()
    {
        // because we do not know when we are asked that, we rely
        // on the previously stored data
        return Context.get().data.cartLineItemCount;
    }

    public void updateQuantity(final int newQuantity, final int itemCount)
    {
        final HtmlElement qty = getQuantityElement().first();
        qty.setTextContent(String.valueOf(newQuantity));

        // store line item count
        Context.get().data.cartLineItemCount = itemCount;

        // store quantity
        Context.get().data.cartQuantityCount = newQuantity;
    }

    public String getShowUrl()
    {
        final HtmlElement element = locate().first();
        return element.getAttribute("data-action-url");
    }

    public LookUpResult getPopover()
    {
        return locate().byCss(".popover");
    }

    public LookUpResult getPopoverCart()
    {
        return locate().byCss(".popover .container.cart");
    }

    public LookUpResult getViewCartLink()
    {
        return locate().byCss("a.goToCart");
    }
}
