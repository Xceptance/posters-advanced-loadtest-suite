package com.xceptance.loadtest.posters.models.components.general;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
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

    public boolean isEmpty()
    {
        return getQuantity() == 0;
    }

    public LookUpResult getQuantityElement()
    {
        return locate().byCss(".cartMiniProductCounter > .value");
    }

    public int getQuantity()
    {
        final HtmlElement qty = getQuantityElement().asserted().first();
        
        return DataUtils.toInt(qty.asText());
    }

    public void updateQuantity(final int newCartQuantity)
    {
        final HtmlElement qty = getQuantityElement().first();
        qty.setTextContent(String.valueOf(newCartQuantity));

        Context.get().data.cartQuantityCount = newCartQuantity;
    }


    public int getLineItemCount()
    {
        return Context.get().data.cartLineItemCount;
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