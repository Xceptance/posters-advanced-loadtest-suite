package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Footer component.
 * 
 * @author Xceptance Software Technologies
 */
public class Footer implements HtmlComponent
{
	public static final Footer instance = new Footer();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("footer");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}