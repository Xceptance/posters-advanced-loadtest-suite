package com.xceptance.loadtest.posters.models.components.homepage;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.HtmlComponent;
import com.xceptance.loadtest.api.pages.Page;

/**
 * Promoted products component.
 * 
 * @author Xceptance Software Technologies
 */
public class PromotedProducts implements HtmlComponent
{
	public static final PromotedProducts instance = new PromotedProducts();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".product-display-heading");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}