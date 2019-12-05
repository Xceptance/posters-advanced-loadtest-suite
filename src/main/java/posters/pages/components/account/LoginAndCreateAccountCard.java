package posters.pages.components.account;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.FormUtils;

public class LoginAndCreateAccountCard implements Component
{
    public final static LoginAndCreateAccountCard instance = new LoginAndCreateAccountCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".login-page .card").hasCss(".tab-content #login");
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

    public HtmlForm fillAndGetCreateAccountForm(final Account account)
    {
        final HtmlForm form = locate().byCss("form.registration").asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-fname"), account.firstname);
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-lname"), account.lastname);

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-phone"), account.billingAddress.phone);

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-email"), account.email);
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-email-confirm"), account.email);

        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-password"), account.password);
        FormUtils.setInputValue(HPU.find().in(form).byId("registration-form-password-confirm"), account.password);

        FormUtils.checkCheckbox(HPU.find().in(form).byCss("input[name='dwfrm_profile_customer_addtoemaillist']"), false);

        return form;
    }

    public HtmlForm fillLoginForm(final Account account)
    {
        final HtmlForm form = locate().byCss("form.login").asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("login-form-email"), account.email);
        FormUtils.setInputValue(HPU.find().in(form).byId("login-form-password"), account.password);

        return form;
    }
}
