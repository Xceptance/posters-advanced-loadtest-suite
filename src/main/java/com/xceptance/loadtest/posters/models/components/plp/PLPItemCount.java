package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.DataUtils;
import com.xceptance.loadtest.posters.util.PageState;

/**
 * Product listing page item count.
 * 
 * @author Xceptance Software Technologies
 */
public enum PLPItemCount implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#totalProductCount");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public int getItemCount()
    {
    	if(PageState.hasProducts())
    	{
    		return PageState.getProductCount();
    	}
    	else if (exists())
        {
            final String content = locate().first().getTextContent();
            if (content != null)
            {
                return DataUtils.toInt(content);
            }
        }
        
        return 0;
    }
}
