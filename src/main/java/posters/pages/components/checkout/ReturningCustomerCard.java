package posters.pages.components.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.FormUtils;

public class ReturningCustomerCard implements Component
{
    public final static ReturningCustomerCard instance = new ReturningCustomerCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad, inefficient
        return Page.find().byCss(".card").hasCss("form.login");
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

    public HtmlForm fillLoginForm(final Account account)
    {
        final HtmlForm form = locate().byCss("form.login").asserted().single();

        FormUtils.setInputValue(HPU.find().in(form).byId("login-form-email"), account.email);
        FormUtils.setInputValue(HPU.find().in(form).byId("login-form-password"), account.password);

        return form;
    }
}
