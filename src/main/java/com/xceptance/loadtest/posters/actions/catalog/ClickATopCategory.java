package com.xceptance.loadtest.posters.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Selects a top category from the navigation menu.
 * 
 * @author Xceptance Software Technologies
 */
public class ClickATopCategory extends PageAction<ClickATopCategory>
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
    	HtmlElement categoryLink = GeneralPages.instance.navigation.getTopCategories().asserted("No top categories found").random();

        loadPageByClick(categoryLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        GeneralPages.instance.validate();
    }
}
