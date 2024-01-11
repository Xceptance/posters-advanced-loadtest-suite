package com.xceptance.loadtest.posters.models.components.account;

import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;

/**
 * Create account form component.
 * 
 * @author Xceptance Software Technologies
 */
public class CreateAccountForm implements Component
{
	public static final CreateAccountForm instance = new CreateAccountForm();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("form-register");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public HtmlElement getCreateAccountButton()
    {
    	return locate().byCss("#btn-register").asserted().single();
    }

    public void fillCreateAccountForm(final Account account)
    {
        final HtmlForm form = locate().asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-last-name"), account.lastname);
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-first-name"), account.firstname);
        
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-e-mail"), account.email);

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-password"), account.password);
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-password-repeat"), account.password);
    }
}