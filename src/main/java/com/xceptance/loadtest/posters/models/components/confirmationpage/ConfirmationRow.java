package com.xceptance.loadtest.posters.models.components.confirmationpage;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Confirmation row component.
 * 
 * @author Xceptance Software Technologies
 */
public class ConfirmationRow implements Component 
{
    public static final ConfirmationRow instance = new ConfirmationRow();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#confirmationRow");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
