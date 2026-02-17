package com.xceptance.loadtest.posters.models.components.checkout;

import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.util.FormUtils;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;

/**
 * Payment form component.
 *  
 * @author Xceptance Software Technologies
 */
public class PaymentForm implements HtmlComponent
{
    public final static PaymentForm instance = new PaymentForm();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("form-add-payment");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public void fillForm(final Account account, final CreditCard creditCard)
    {
        final HtmlForm form = locate().asserted("Expected single payment form").single();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#creditcard-number"), creditCard.number);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#name"), account.firstname + " " + account.lastname);
        FormUtils.select(HPU.find().in(form).byCss("#expiration-date-month"), creditCard.expirationMonth);
        FormUtils.select(HPU.find().in(form).byCss("#expiration-date-year"), creditCard.expirationYear);
    }

    public HtmlElement getContinueButton()
    {
        return HPU.find().in(locate().asserted("Expected single payment form").single()).byId("btn-add-payment").asserted("Expected single continue button").single();
    }
}