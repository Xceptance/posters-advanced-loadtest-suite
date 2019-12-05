package com.xceptance.loadtest.posters.pages.components.checkout;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.render.HtmlRenderer;

public class OrderSummaryCard implements Component
{
    public final static OrderSummaryCard instance = new OrderSummaryCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".order-total-summary");
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
     * Renders the oder summary display
     *
     * @param response
     */
    public void render(final HtmlRenderer renderer)
    {
        renderer.template("/templates/checkout/order-summary-card.ftlh").replace(locate().asserted().single());
    }
}
