package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Search query component.
 * 
 * @author Xceptance Software Technologies
 */
public class NotFoundMessage implements Component
{
	public static final SearchQuery instance = new SearchQuery();

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
