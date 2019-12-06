package com.xceptance.loadtest.posters.pages.components.account;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.FormUtils;

public class CreateAccountCard implements Component
{
    public final static CreateAccountCard instance = new CreateAccountCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("formRegister");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public HtmlElement getCreateAccountButton()
    {
    	return locate().byCss("#btnRegister").asserted().single();
    }

    public void fillCreateAccountForm(final Account account)
    {
        final HtmlForm form = locate().asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("lastName"), account.lastname);
        FormUtils.setInputValue(HPU.find().in(form).byId("firstName"), account.firstname);
        
        FormUtils.setInputValue(HPU.find().in(form).byId("eMail"), account.email);

        FormUtils.setInputValue(HPU.find().in(form).byId("password"), account.password);
        FormUtils.setInputValue(HPU.find().in(form).byId("passwordAgain"), account.password);
    }
}
