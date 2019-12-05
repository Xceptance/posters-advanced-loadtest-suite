package com.xceptance.loadtest.posters.pages.components.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.FormUtils;

public class ShippingAddressCard implements Component
{
    public final static ShippingAddressCard instance = new ShippingAddressCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".shipping-section > .single-shipping");
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

    public LookUpResult getShippingMethodList()
    {
        return locate().byCss(".shipping-method-list[data-action-url]");
    }

    public String getSelectShippingMethodUrl()
    {
        return getShippingMethodList().asserted().single().getAttribute("data-select-shipping-method-url");
    }

    public String getUpdateShippingMethodUrl()
    {
        return getShippingMethodList().asserted().single().getAttribute("data-action-url");
    }

    public HtmlForm getShippingAddressForm()
    {
        return locate().byCss("#dwfrm_shipping").asserted().first();
    }

    public String getShipmentUUID()
    {
        return locate().byCss("#dwfrm_shipping > input[name='shipmentUUID']").asserted().single().getAttribute("value");
    }

    public LookUpResult getUnselectedShippingMethods()
    {
        return getShippingMethodList().byCss("input[name=dwfrm_shipping_shippingAddress_shippingMethodID]:not([checked])").asserted();
    }

    public void fillForm(final Account account)
    {
        final HtmlForm form = getShippingAddressForm();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingFirstName"), account.firstname);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingLastName"), account.lastname);

        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingAddressOne"), account.shippingAddress.addressLine1);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingAddressTwo"), account.shippingAddress.addressLine2);

        FormUtils.select(HPU.find().in(form).byCss("#shippingCountry"), account.shippingAddress.countryCode);

        // state is country dependent
        switch (Context.get().data.site.id)
        {
            case "UK":
                // we have input of text and non-mandatory
                FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingState"), account.shippingAddress.stateCode);
                break;
            case "China":
                // we have input of text and non-mandatory
                FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingState"), account.shippingAddress.state);
                break;
            case "France":
                // no states in France
                break;
            default:
                // we got a states drop down
                FormUtils.select(HPU.find().in(form).byCss("#shippingState"), account.shippingAddress.stateCode);
        }

        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingAddressCity"), account.shippingAddress.city);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingZipCode"), account.shippingAddress.zip);

        FormUtils.setInputValue(HPU.find().in(form).byCss("#shippingPhoneNumber"), account.shippingAddress.phone);
    }

    public String getSubmitShippingUrl()
    {
        return getShippingAddressForm().getActionAttribute();
    }

    /**
     * Renders a new shipping method list and puts it into place
     * @param response
     */
    public void renderShippingMethodList(final HtmlRenderer renderer)
    {
        renderer.template("/templates/checkout/shipping-method-list.ftlh").replaceContentOf(getShippingMethodList().asserted().single());
    }

    /**
     * Just hide the form when needed, that is for debugging mostly to stay consistent in look and
     * feel
     */
    public void hide()
    {
        locate().asserted().single().setAttribute("style", "display: none;");
    }
}
