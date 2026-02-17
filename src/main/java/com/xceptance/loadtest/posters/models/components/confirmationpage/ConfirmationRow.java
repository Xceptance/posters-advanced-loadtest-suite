package com.xceptance.loadtest.posters.models.components.confirmationpage;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Confirmation row component.
 * 
 * @author Xceptance Software Technologies
 */
public class ConfirmationRow implements HtmlComponent 
{
    public static final ConfirmationRow instance = new ConfirmationRow();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#confirmation-row");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
