package com.xceptance.loadtest.headless.flows;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.headless.actions.cart.RemoveCartItem;
import com.xceptance.loadtest.headless.pages.cart.CartPage;
import com.xceptance.loadtest.headless.pages.components.cart.CartTable;

/**
 * Remove stale items from the cart
 *
 * @author rschwietzke
 *
 */
public class CartRemoveAllItemsFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // ok, cleanup the cart until we do not have anything to cleanup anymore or
        // the cart is empty

        // final List<HtmlElement> unavailableItems =
        // CartPage.instance.cartTable.getUnavailableLineItems();
        List<HtmlElement> items = CartPage.instance.cartTable.getLineItems().all();
        while (!items.isEmpty())
        {
            // remove the item, just start with the first one
            new RemoveCartItem(CartTable.instance.getItemInformation(items.get(0))).run();

            // update, because a lot of things might have changed
            items = CartPage.instance.cartTable.getLineItems().all();
        }

        return true;
    }
}
