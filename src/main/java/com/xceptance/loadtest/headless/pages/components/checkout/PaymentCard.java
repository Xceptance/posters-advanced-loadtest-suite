package com.xceptance.loadtest.headless.pages.components.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.FormUtils;

public class PaymentCard implements Component
{
    public final static PaymentCard instance = new PaymentCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".card.payment-form");
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
     * Make the form visible
     */
    public void makeVisible()
    {
        locate().asserted().first().setAttribute("style", "display: block; visibility: initial;");
    }

    public HtmlForm getBillingForm()
    {
        return locate().byCss("#dwfrm_billing").asserted().first();
    }

    public void fillBillingAddressIn(final Account account)
    {
        final HtmlForm form = getBillingForm();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingFirstName"), account.firstname);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingLastName"), account.lastname);

        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingAddressOne"), account.billingAddress.addressLine1);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingAddressTwo"), account.billingAddress.addressLine2);

        FormUtils.select(HPU.find().in(form).byCss("#billingCountry"), account.billingAddress.countryCode);

        // state is country dependent
        switch (Context.get().data.site.id)
        {
            case "UK":
                // we have input of text and non-mandatory
                FormUtils.setInputValue(HPU.find().in(form).byCss("#billingState"), account.billingAddress.stateCode);
                break;
            case "China":
                // we have input of text and non-mandatory
                FormUtils.setInputValue(HPU.find().in(form).byCss("#billingState"), account.billingAddress.state);
                break;
            case "France":
                // no states in France
                break;
            default:
                // we got a states drop down
                FormUtils.select(HPU.find().in(form).byCss("#billingState"), account.billingAddress.stateCode);
        }

        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingAddressCity"), account.billingAddress.city);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#billingZipCode"), account.billingAddress.zip);
    }

    public void fillCreditCardIn(final Account account, final CreditCard cc)
    {
        final HtmlForm form = getBillingForm();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#cardType"), cc.type);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#cardNumber"), cc.unformattedNumber);

        FormUtils.select(HPU.find().in(form).byCss("#expirationYear"), cc.expirationYear);
        FormUtils.select(HPU.find().in(form).byCss("#expirationMonth"), cc.expirationMonth);

        FormUtils.setInputValue(HPU.find().in(form).byCss("#securityCode"), cc.cvc);

        FormUtils.setInputValue(HPU.find().in(form).byCss("#phoneNumber"), account.billingAddress.phone);
        // that is set automatically in the browser, we have to mimic that
        FormUtils.setInputValue(HPU.find().in(form).byCss("#email"), account.email);
    }

    public String getSubmitPaymentUrl()
    {
        return getBillingForm().getAttribute("action");
    }

    /**
     * Get the submitted addresses displayed
     *
     * @param renderer
     */
    public void render(final HtmlRenderer renderer)
    {
        renderer.template("/templates/checkout/payment-card.ftlh")
                        .replaceContentOf(locate().byCss(".addressSelector").asserted().single());
    }

    public void renderSummary(final HtmlRenderer renderer)
    {
        // render
        renderer.template("/templates/checkout/payment-summary-card.ftlh")
                        .replace(Page.find().byCss(".payment-summary .card-body").asserted().single());

        // make visible or hide
        Page.find().byCss(".payment-summary").asserted().single().setAttribute("style", "display: block;");
        Page.find().byCss(".payment-form").asserted().single().setAttribute("style", "display: none;");
    }
}
