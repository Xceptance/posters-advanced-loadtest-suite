package com.xceptance.loadtest.posters.actions.cart;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Format;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.pages.cart.CartPage;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

/**
 * Access the cart page.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ViewCart extends PageAction<ViewCart>
{
    public ViewCart()
    {
        super();

        // set the timername to include the amount of product roughly
        if (Context.configuration().reportCartBySize)
        {
            setTimerName(Format.timerName(getTimerName(), GeneralPages.instance.miniCart.getQuantity()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get mini cart link.
        final HtmlElement cartLink = GeneralPages.instance.miniCart.getViewCartLink().asserted().single();

        // Click it.
        loadPageByClick(cartLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        // basic checks for the cart
        CartPage.instance.validate();
    }
}
