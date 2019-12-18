package com.xceptance.loadtest.posters.models.components.cart;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class CartError implements Component
{
    public final static CartError instance = new CartError();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".page > .cart-error");
    }

    /**
     * Indicates if this component exists
     *
     * @return
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Do we have a message?
     *
     * @return true if yes, false otherwise
     */
    public boolean hasMessage()
    {
        // problem is, that the error cart thing is always there
        return locate().byCss("div.alert").exists();
    }
}
