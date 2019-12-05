package com.xceptance.loadtest.posters.actions.checkout;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.Gson;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.jsondata.CheckoutJSON;
import com.xceptance.loadtest.posters.jsondata.CheckoutJSON_StringValued;
import com.xceptance.loadtest.posters.jsondata.OrderJSON;
import com.xceptance.loadtest.posters.jsondata.SaveMyInformationJSON;
import com.xceptance.loadtest.posters.pages.checkout.CheckoutPage;
import com.xceptance.loadtest.posters.pages.checkout.OrderConfirmationPage;

public abstract class AbstractCheckout<T> extends PageAction<T>
{
    protected void updateShippingMethod() throws Exception
    {
        final String url = CheckoutPage.instance.shippingAddressCard.getUpdateShippingMethodUrl();
        final HtmlForm form = CheckoutPage.instance.shippingAddressCard.getShippingAddressForm();

        // remove the detailed start of the parameters, aka simplify them
        // so dwfrm_shipping_shippingAddress_addressFields_address1 becomes address1
        final List<NameValuePair> serializedForm = AjaxUtils.serializeForm(form)
                        .stream().map(pair ->
                        {
                            String name = pair.getName();
                            final int pos = name.lastIndexOf("_");
                            name = name.substring(pos + 1);

                            if (name.equals("country"))
                            {
                                name = "countryCode";
                            }

                            return new NameValuePair(name, pair.getValue());
                        }).collect(Collectors.toList());

        final WebResponse response = new HttpRequest()
                        .XHR()
                        .url(url)
                        .postParams(serializedForm)
                        .removePostParam("shippingMethodID")
                        .removePostParam("token")
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CheckoutJSON.class, "data");

        CheckoutPage.instance.shippingAddressCard.renderShippingMethodList(renderer);
        CheckoutPage.instance.orderSummaryCard.render(renderer);
    }

    protected void selectShippingMethod() throws Exception
    {
        final String url = CheckoutPage.instance.shippingAddressCard.getSelectShippingMethodUrl();
        final HtmlForm form = CheckoutPage.instance.shippingAddressCard.getShippingAddressForm();

        // remove the detailed start of the parameters, aka simplify them
        // so dwfrm_shipping_shippingAddress_addressFields_address1 becomes address1
        final List<NameValuePair> serializedForm = AjaxUtils.serializeForm(form)
                        .stream().map(pair ->
                        {
                            String name = pair.getName();
                            final int pos = name.lastIndexOf("_");
                            name = name.substring(pos + 1);

                            if (name.equals("country"))
                            {
                                name = "countryCode";
                            }

                            return new NameValuePair(name, pair.getValue());
                        }).collect(Collectors.toList());

        final String selectedShippingMethodValue = CheckoutPage.instance.shippingAddressCard
                        .getUnselectedShippingMethods().random().getAttribute("value");

        final WebResponse response = new HttpRequest()
                        .XHR()
                        .url(url)
                        .postParams(serializedForm)
                        .postParam("methodID", selectedShippingMethodValue)
                        .removePostParam("shippingMethodID")
                        .removePostParam("token")
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CheckoutJSON.class, "data");

        CheckoutPage.instance.shippingAddressCard.renderShippingMethodList(renderer);
        CheckoutPage.instance.orderSummaryCard.render(renderer);
    }

    protected void submitShipping() throws Exception
    {
        final String url = CheckoutPage.instance.shippingAddressCard.getSubmitShippingUrl();
        final HtmlForm form = CheckoutPage.instance.shippingAddressCard.getShippingAddressForm();

        final List<NameValuePair> serializedForm = AjaxUtils.serializeForm(form);

        final WebResponse response = new HttpRequest()
                        .url(url)
                        .postParams(serializedForm)
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CheckoutJSON.class, "data");

        CheckoutPage.instance.paymentCard.render(renderer);
        CheckoutPage.instance.shippingSummaryCard.render(renderer);

        // just for easier debugging
        CheckoutPage.instance.paymentCard.makeVisible();
        CheckoutPage.instance.shippingSummaryCard.makeVisible();
        CheckoutPage.instance.shippingAddressCard.hide();
    }

    protected void submitPayment() throws Exception
    {
        final String url = CheckoutPage.instance.paymentCard.getSubmitPaymentUrl();
        final HtmlForm form = CheckoutPage.instance.paymentCard.getBillingForm();

        final List<NameValuePair> serializedForm = AjaxUtils.serializeForm(form);

        final WebResponse response = new HttpRequest()
                        .url(url)
                        .postParams(serializedForm)
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        // the address value thing is a design mistake in MFRA
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), CheckoutJSON_StringValued.class, "data");

        CheckoutPage.instance.shippingSummaryCard.render(renderer);
        CheckoutPage.instance.paymentCard.renderSummary(renderer);
        CheckoutPage.instance.orderSummaryCard.render(renderer);
    }

    protected void submitOrder() throws Exception
    {
        final String url = CheckoutPage.instance.nextButtons.getPlaceOrderButtonUrl();

        final WebResponse response = new HttpRequest()
                        .XHR()
                        .url(url)
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final HtmlRenderer renderer = Page.renderHtml().json(response.getContentAsString(), OrderJSON.class, "order");

        // don't render, redirect to the url in the json
        final OrderJSON data = renderer.getJson(OrderJSON.class, "order");

        Assert.assertFalse(data.errorMessage, data.error);

        this.loadPage(Page.makeFullyQualifiedUrl(data.continueUrl) + "?" + "ID=" + data.orderID + "&token=" + data.orderToken);
    }

    protected void submitSaveMyInformation() throws Exception
    {
        final HtmlForm form = OrderConfirmationPage.instance.saveMyInformationCard.getForm();
        final String url = form.getAttribute("action");

        final WebResponse response = new HttpRequest()
                        .XHR()
                        .url(url)
                        .postParams(AjaxUtils.serializeForm(form))
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final Gson gson = Context.getGson();
        final SaveMyInformationJSON data = gson.fromJson(response.getContentAsString(), SaveMyInformationJSON.class);
        Assert.assertTrue("Failed submitting account creation request", data.success);

        this.loadPage(Page.makeFullyQualifiedUrl(data.redirectUrl));
    }
}
