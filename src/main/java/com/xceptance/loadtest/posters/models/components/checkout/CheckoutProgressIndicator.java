package com.xceptance.loadtest.posters.models.components.checkout;

import org.htmlunit.javascript.host.html.HTMLElement;

import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Indicator component for checkout progress (a bread crumb).
 *  
 * @author Xceptance Software Technologies
 */
public class CheckoutProgressIndicator implements Component
{
    public final static CheckoutProgressIndicator instance = new CheckoutProgressIndicator();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("checkout-progress-indicator").byCss(".progress-indicator");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public boolean isStepAvailable(Integer StepNumber)
    {
        // try to find matching progress-bubble-caption text
        // find nearest progress-bubble text
        // check whether progress-indicator has matching progress-[the number found as the bubbles text] class
    	return locate().single().matches(".progress-"+StepNumber);
    }
}