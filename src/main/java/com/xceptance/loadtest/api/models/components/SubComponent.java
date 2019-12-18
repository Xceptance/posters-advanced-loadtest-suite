package com.xceptance.loadtest.api.models.components;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;

public interface SubComponent
{
    /**
     * Find this component within another element
     *
     * @return
     */
    public LookUpResult locate(HtmlElement in);

    /**
     * Check if this component exists within another component
     *
     * @return true if this component exists
     */
    public boolean exists(HtmlElement in);
}
