package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Search query component.
 * 
 * @author Xceptance Software Technologies
 */
public class SearchQuery implements Component
{
	public static final SearchQuery instance = new SearchQuery();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("search-text-value");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public String getQuery()
    {
    	return locate().asserted("Expected existing search query element").single().getTextContent();
    }
}