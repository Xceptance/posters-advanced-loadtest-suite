package com.xceptance.loadtest.posters.actions.cart;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Opens the cart page.
 *
 * @author Xceptance Software Technologies
 */
public class ViewCart extends PageAction<ViewCart>
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        loadPageByClick(GeneralPages.instance.miniCart.getViewCartLink().asserted().single());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        // Validate the cart page
        CartPage.instance.validate();
    }
}