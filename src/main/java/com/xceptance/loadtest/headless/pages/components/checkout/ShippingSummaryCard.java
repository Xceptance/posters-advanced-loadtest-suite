package com.xceptance.loadtest.headless.pages.components.checkout;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.render.HtmlRenderer;

public class ShippingSummaryCard implements Component
{
    public final static ShippingSummaryCard instance = new ShippingSummaryCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".card.shipping-summary");
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

    public void render(final HtmlRenderer renderer)
    {
        // render
        renderer.template("/templates/checkout/shipping-summary-card.ftlh")
                        .replace(Page.find().byCss(".card.shipping-summary .card-body").asserted().single());
    }

    public void makeVisible()
    {
        // make visible or hide
        Page.find().byCss(".shipping-summary").asserted().single().setAttribute("style", "display: block;");
    }
}
