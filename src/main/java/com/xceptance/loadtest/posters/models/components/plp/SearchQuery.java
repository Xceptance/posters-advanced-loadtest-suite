package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.posters.util.Context;

/**
 * Search query component.
 * 
 * @author Xceptance Software Technologies
 */
public class SearchQuery implements HtmlComponent
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