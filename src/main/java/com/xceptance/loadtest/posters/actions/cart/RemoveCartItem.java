package com.xceptance.loadtest.posters.actions.cart;

import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.posters.models.components.cart.CartTable.ItemInformation;

public class RemoveCartItem extends AjaxAction<RemoveCartItem>
{
    private final ItemInformation info;

    /**
     * Constructor
     *
     * @param item
     *            the item to remove
     */
    public RemoveCartItem(final ItemInformation info)
    {
        this.info = info;
    }

    @Override
    public void precheck()
    {
        // nothing to do yet
    }

    @Override
    protected void doExecute() throws Exception
    {
        // TODO
    	
        //final WebResponse response = new HttpRequest().XHR().url(info.removeUrl).param("pid", info.pid).param("uuid", info.uuid).assertStatusCode(200).GET().fire();

        //MiniCart.instance.updateQuantity(json.basket.numItems, json.basket.items.size());
    }

    @Override
    protected void postValidate() throws Exception
    {
    }
}
