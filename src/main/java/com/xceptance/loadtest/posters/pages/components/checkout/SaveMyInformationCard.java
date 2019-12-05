package com.xceptance.loadtest.posters.pages.components.checkout;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.FormUtils;

public class SaveMyInformationCard implements Component
{
    public final static SaveMyInformationCard instance = new SaveMyInformationCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".card.order-confirm-create-account");
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

    public HtmlForm getForm()
    {
        return locate().byCss("form").asserted().single();
    }

    public void fillForm(final Account account)
    {
        FormUtils.setInputValueByID("newPassword", account.password);
        FormUtils.setInputValueByID("newPasswordConfirm", account.password);
    }
}
