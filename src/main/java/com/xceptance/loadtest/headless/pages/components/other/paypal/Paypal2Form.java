package com.xceptance.loadtest.headless.pages.components.other.paypal;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.FormUtils;

/**
 * The paypal form (variant 2) component.
 *
 * Notes: Handles the login step of paypal.
 */
public class Paypal2Form implements Component
{
    public final static Paypal2Form instance = new Paypal2Form();

    /**
     * {@inheritDoc}
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("loginForm");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Fills the form with given login details (login step).
     *
     * @param email The email to provide
     * @param password The password to provide
     */
    public void setLoginDetails(final String email, final String password)
    {
        FormUtils.setInputValueByID("email", email);
        FormUtils.setInputValueByID("password", password);
    }

    /**
     * Reset the form.
     */
    public void resetForm()
    {
        FormUtils.setInputValueByID("email", "");

        Page.find().byXPath("//input[@name='incontext']").asserted("Failed to find incontext input in paypal login form (variant 2).").single().remove();
        Page.find().byXPath("//input[@name='password']").asserted("Failed to find password input in paypal login form (variant 2).").single().remove();
    }

    /**
     * Retrieve parameters of form.
     *
     * @return A list with all parameters of the form
     */
    public List<NameValuePair> getParameters()
    {
        return AjaxUtils.serializeForm(locate().asserted("Failed to find single paypal login form (variant 2).").single());
    }

    /**
     * Retrieves the request URL stored in the action attribute of the form.
     *
     * @return The URL string.
     */
    public String getUrl()
    {
        return ((HtmlForm) locate().asserted("Failed to find single paypal login form (variant 2).").single()).getActionAttribute();
    }
}
