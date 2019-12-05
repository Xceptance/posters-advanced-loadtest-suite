package posters.actions.cart;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.HttpRequest;

import posters.jsondata.CartJSON;
import posters.pages.cart.CartPage;
import posters.pages.components.cart.CartTable.ItemInformation;
import posters.pages.components.general.MiniCart;

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
        // fire
        final WebResponse response = new HttpRequest().XHR().url(info.removeUrl).param("pid", info.pid).param("uuid", info.uuid).assertStatusCode(200).GET().fire();

        // we got a full cart json back
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CartJSON.class, "data");

        // update item count
        CartPage.instance.cartItemCount.renderAndUpdate(renderer);

        // update item list and update information cart
        CartPage.instance.cartTable.renderAndUpdate(renderer);

        // update the mini cart
        final CartJSON json = renderer.getJson(CartJSON.class, "data");
        MiniCart.instance.updateQuantity(json.basket.numItems, json.basket.items.size());
    }

    @Override
    protected void postValidate() throws Exception
    {
    }
}
