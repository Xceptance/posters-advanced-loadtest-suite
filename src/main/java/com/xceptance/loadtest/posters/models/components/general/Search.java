package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;

/**
 * Search component.
 * 
 * @author Xceptance Software Technologies
 */
public class Search implements Component
{
	public static final Search instance = new Search();

    @Override
    public LookUpResult locate()
    {
        return Header.instance.locate().byCss("#header-search-form");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
    
    public LookUpResult getSearchForm()
    {
    	return locate();
    }

    public LookUpResult getSearchField()
    {
        return getSearchForm().byCss("input#s");
    }
    
    public LookUpResult getSearchButton()
    {
        return getSearchForm().byCss("#header-search-button");
    }
}