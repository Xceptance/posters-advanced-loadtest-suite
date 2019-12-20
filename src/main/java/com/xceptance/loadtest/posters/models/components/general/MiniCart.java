package com.xceptance.loadtest.posters.models.components.general;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DataUtils;

/**
 * Mini cart component.
 * 
 * @author Xceptance Software Technologies
 */
public class MiniCart implements Component
{
	public static final MiniCart instance = new MiniCart();

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

    public int getLineItemCount()
    {
        return Context.get().data.cartLineItemCount;
    }

    public LookUpResult getQuantityElement()
    {
        return locate().byCss(".cartMiniProductCounter > .value");
    }

    public int getQuantity()
    {
        return DataUtils.toInt(getQuantityElement().asserted().first().asText());
    }

    public void updateQuantity(final int newCartQuantity)
    {
        final HtmlElement qty = getQuantityElement().first();
        qty.setTextContent(String.valueOf(newCartQuantity));

        Context.get().data.cartQuantityCount = newCartQuantity;
    }

    public LookUpResult getViewCartLink()
    {
        return locate().byCss("a.goToCart");
    }
}