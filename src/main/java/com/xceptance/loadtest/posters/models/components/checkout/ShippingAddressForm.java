package com.xceptance.loadtest.posters.models.components.checkout;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;

/**
 * Shipping address form component.
 *  
 * @author Xceptance Software Technologies
 */
public class ShippingAddressForm implements Component
{
    public final static ShippingAddressForm instance = new ShippingAddressForm();
    
    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("form-add-del-addr");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public void fillForm(final Account account)
    {
        final HtmlForm form = locate().asserted("Expected single shipping address form").single();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-last-name"), account.firstname);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-first-name"), account.lastname);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-company"), "");
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-address-line"), account.shippingAddress.addressLine1);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-city"), account.shippingAddress.city);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-state"), account.shippingAddress.state);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#address-zip"), account.shippingAddress.zip);
        FormUtils.select(HPU.find().in(form).byCss("#address-country"), account.shippingAddress.country);
        FormUtils.checkRadioButton(HPU.find().in(form).byCss("#bill-equal-shipp"));
    }
    
    public HtmlElement getContinueButton()
    {
    	return HPU.find().in(locate().asserted("Expected single shipping address form").single()).byId("button-add-shipping-address").asserted("Expected single continue button").single();
    }
}