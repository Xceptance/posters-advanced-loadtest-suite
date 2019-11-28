package com.xceptance.loadtest.headless.actions.cart;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

public class ShowMiniCart extends AjaxAction<ShowMiniCart>
{
    private String url;

    @Override
    public void precheck()
    {
        Assert.assertTrue(GeneralPages.instance.miniCart.getQuantity() > 0);
        url = GeneralPages.instance.miniCart.getShowUrl();
    }

    @Override
    protected void doExecute() throws Exception
    {
        // where to add
        final HtmlElement popover = GeneralPages.instance.miniCart.getPopover().asserted("No location for minicart popover found").first();

        if (Context.isLoadTest == false)
        {
            popover.setAttribute("style", "display: block; z-index: 1000;");
        }

        new HttpRequest().XHR().replaceContentOf(popover).url(url).assertStatusCode(200).GET().fire();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // we got a popover
        GeneralPages.instance.miniCart.getPopoverCart().asserted("No popover cart found").exists();

        // ok, we should now have an item quantity > 0
        Assert.assertTrue("Item count not available from minicart popover", GeneralPages.instance.miniCart.getLineItemCount() > 0);
    }
}
