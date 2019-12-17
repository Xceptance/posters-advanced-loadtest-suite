package com.xceptance.loadtest.posters.actions.cart;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.jsondata.CartUpdateJSON;
import com.xceptance.loadtest.posters.pages.cart.CartPage;
import com.xceptance.loadtest.posters.pages.components.cart.CartTable.ItemInformation;
import com.xceptance.loadtest.posters.pages.components.general.MiniCart;

public class ChangeLineItemQuantity extends AjaxAction<ChangeLineItemQuantity>
{
    /**
     * The quantity which is set to reach the payment limit.
     */
    private final int newQuantity;

    private final ItemInformation info;

    public ChangeLineItemQuantity(final int newQuantity, final ItemInformation info)
    {
        this.newQuantity = newQuantity;
        this.info = info;
    }

    @Override
    protected void doExecute() throws Exception
    {
        // fire
        final WebResponse response = new HttpRequest().XHR().url(info.updateUrl).param("pid", info.pid).param("uuid", info.uuid).param("quantity", String.valueOf(newQuantity))
                        .assertStatusCode(200).GET().fire();

        // we got a full cart json back
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CartUpdateJSON.class, "data");

        // update item count
        // CartPage.instance.cartItemCount.renderAndUpdate(renderer);

        // update item list and update information cart
        CartPage.instance.cartTable.renderAndUpdateCleanUp(renderer);

        // update the mini cart
        final CartUpdateJSON json = renderer.getJson(CartUpdateJSON.class, "data");
        
        // TODO
        //MiniCart.instance.updateQuantity(json.numItems, json.items.size());
    }

    @Override
    protected void postValidate() throws Exception
    {
    }
}
