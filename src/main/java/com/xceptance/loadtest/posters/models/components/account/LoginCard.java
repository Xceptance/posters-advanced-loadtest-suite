package com.xceptance.loadtest.posters.models.components.account;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;

public class LoginCard implements Component
{
    public final static LoginCard instance = new LoginCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("formLogin");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public HtmlElement getSignInButton()
    {
    	return locate().byCss("#btnSignIn").asserted().single();
    }

    public HtmlForm fillLoginForm(final Account account)
    {
        final HtmlForm form = locate().asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("email"), account.email);
        FormUtils.setInputValue(HPU.find().in(form).byId("password"), account.password);

        return form;
    }
}
