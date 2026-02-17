package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Error message component.
 * 
 * @author Xceptance Software Technologies
 * 
 * NOTE: The Demo Store triggers Error Messages through JavaScript,
 * thus this component will only be found with JS enabled (non-default)
*/
public class ErrorMessage implements HtmlComponent
{
    public static final ErrorMessage instance = new ErrorMessage();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("alert-placeholder").byCss(".alert");
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