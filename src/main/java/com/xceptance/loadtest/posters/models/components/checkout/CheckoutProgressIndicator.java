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
        return Page.find().byCss(".progress-indicator");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    // WIP currently not working, adjust when required
    public boolean isStepAvailable(String stepText)
    {
        // try to find matching progress-bubble-caption with StepText
        HTMLElement caption = locate().single().getFirstByXPath(".//"+stepText);
        // find nearest progress-bubble text and read it as an int
        int StepNumber = Integer.parseInt(caption.getParentHTMLElement().querySelector(".progress-bubble").getTextContent());
        // check whether progress-indicator has matching progress-[the number found as the bubbles text] class
        return isStepAvailable(StepNumber);
    	// return HPU.find().in(locate().single()).byXPath("./ul/li/a[contains(text(), '" + stepText + "')]").exists();
    }

    public boolean isStepAvailable(Integer StepNumber)
    {
        // try to find matching progress-bubble-caption text
        // find nearest progress-bubble text
        // check whether progress-indicator has matching progress-[the number found as the bubbles text] class
    	return locate().single().matches(".progress-"+StepNumber);
    }
}