package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.DataUtils;
import com.xceptance.loadtest.posters.util.PageState;

/**
 * Product listing page item count.
 * 
 * @author Xceptance Software Technologies
 */
public class ItemCount implements HtmlComponent
{
	public static final ItemCount instance = new ItemCount();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("total-product-count");
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