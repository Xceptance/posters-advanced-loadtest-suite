package com.xceptance.loadtest.posters.actions.catalog;

import org.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Selects a category from the navigation menu.
 * 
 * @author Xceptance Software Technologies
 */
public class ClickACategory extends PageAction<ClickACategory>
{
    private HtmlElement categoryLink;

    public ClickACategory()
    {
        // Intentionally empty
    }

    public ClickACategory(final HtmlElement link)
    {
        categoryLink = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        if (categoryLink == null)
        {
            categoryLink = GeneralPages.instance.navigation.getCategories().asserted("No categories found").random();
        }

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