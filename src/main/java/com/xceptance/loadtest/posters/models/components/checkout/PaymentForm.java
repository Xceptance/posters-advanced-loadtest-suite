package com.xceptance.loadtest.posters.models.components.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;

/**
 * Payment form component.
 *  
 * @author Xceptance Software Technologies
 */
public class PaymentForm implements Component
{
    public final static PaymentForm instance = new PaymentForm();
    
    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("formAddPayment");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public void fillForm(final Account account, CreditCard creditCard)
    {
        final HtmlForm form = locate().asserted("Expected single payment form").single();

        FormUtils.setInputValue(HPU.find().in(form).byCss("#creditCardNumber"), creditCard.number);
        FormUtils.setInputValue(HPU.find().in(form).byCss("#name"), account.getFullName());
        FormUtils.select(HPU.find().in(form).byCss("#expirationDateMonth"), creditCard.expirationMonth);
        FormUtils.select(HPU.find().in(form).byCss("#expirationDateYear"), creditCard.expirationYear);
    }
    
    public HtmlElement getContinueButton()
    {
    	return HPU.find().in(locate().asserted("Expected single payment form").single()).byId("btnAddPayment").asserted("Expected single continue button").single();
    }
}