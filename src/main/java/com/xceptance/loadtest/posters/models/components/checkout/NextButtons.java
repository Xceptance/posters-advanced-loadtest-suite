package com.xceptance.loadtest.posters.models.components.checkout;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class NextButtons implements Component
{
    public final static NextButtons instance = new NextButtons();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".next-step-button");
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
     * Get Next: Payment button
     */
    public LookUpResult getNextPaymentButton()
    {
        return locate().byCss(".submit-shipping");
    }

    /**
     * Get Next: Place Order button
     */
    public LookUpResult getNextPlaceOrderButton()
    {
        return locate().byCss(".submit-payment");
    }

    /**
     * Get Next: Place Order button url
     */
    public String getPlaceOrderButtonUrl()
    {
        return locate().byCss(".place-order").asserted().first().getAttribute("data-action");
    }

    /**
     * Get Place Order button
     */
    public LookUpResult getPlaceOrderButton()
    {
        return locate().byCss(".place-order");
    }
}
