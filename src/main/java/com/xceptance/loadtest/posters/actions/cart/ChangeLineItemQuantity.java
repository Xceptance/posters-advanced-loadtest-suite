package com.xceptance.loadtest.posters.actions.cart;

import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.posters.models.components.cart.CartTable.ItemInformation;

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
        // TODO
    	
        //final WebResponse response = new HttpRequest().XHR().url(info.updateUrl).param("pid", info.pid).param("uuid", info.uuid).param("quantity", String.valueOf(newQuantity)).assertStatusCode(200).GET().fire();

        //MiniCart.instance.updateQuantity(json.numItems, json.items.size());
    }

    @Override
    protected void postValidate() throws Exception
    {
    }
}
