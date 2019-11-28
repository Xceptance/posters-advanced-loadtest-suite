package com.xceptance.loadtest.headless.pages.components.other.paypal;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.FormUtils;

/**
 * The paypal form (variant 1) component.
 *
 * Note: This class handles the three steps done in the same form, login, pay now and agree step.
 */
public class Paypal1Form implements Component
{
    public final static Paypal1Form instance = new Paypal1Form();

    /**
     * {@inheritDoc}
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("parentForm");
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
        FormUtils.setInputValueByID("login_email", email);
        FormUtils.setInputValueByID("login_password", password);
    }

    /**
     * Retrieves the submit button (login step).
     *
     * @return The LookUpResult of the submit button
     */
    public LookUpResult getSubmitButton()
    {
        return HPU.find().in(locate().single()).byId("submitLogin");
    }

    /**
     * Removes the input with the given name (pay now step).
     *
     * @param inputName The name of the input to remove
     */
    public void removeInputByName(final String inputName)
    {
        ((HtmlForm) locate().single()).getInputByName(inputName).remove();
    }

    /**
     * Retrieves the continue button (pay now step).
     *
     * @return The LookUpResult of the continue button
     */
    public LookUpResult getContinueButton()
    {
        return HPU.find().in(locate().single()).byId("continue_abovefold");
    }

    /**
     * Retrieves the accept button (agree step).
     *
     * @return The LookUpResult of the accept button
     */
    public LookUpResult getAcceptButton()
    {
        return HPU.find().in(locate().single()).byId("accept.x");
    }
}
