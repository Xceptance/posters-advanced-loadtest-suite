package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Search query component.
 * 
 * @author Xceptance Software Technologies
 */
public class NotFoundMessage implements HtmlComponent
{
    public static final NotFoundMessage instance = new NotFoundMessage();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("not-found-message");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}