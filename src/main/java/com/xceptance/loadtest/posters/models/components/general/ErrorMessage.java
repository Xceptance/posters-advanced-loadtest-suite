package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Error message component.
 * 
 * @author Xceptance Software Technologies
 */
public class ErrorMessage implements Component
{
	public static final ErrorMessage instance = new ErrorMessage();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("errorMessage");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public String getMessage()
    {
    	return locate().byCss("strong").asserted("Expected error message to be available").single().getTextContent();
    }
}