package com.xceptance.loadtest.headless.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.headless.pages.general.GeneralPages;

/**
 * Selects a category from the top navigation menu.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class ClickACategory extends PageAction<ClickACategory>
{
    private HtmlElement categoryLink;

    public ClickACategory()
    {
        // empty intentional
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
            // Get the link and click it
            categoryLink = GeneralPages.instance.navigation.getCategories().asserted("No categories found").random();
        }

        loadDebugUrlOrElse("/s/SiteGenesis/womens/clothing/tops/?lang=en_US").loadPageByClick(categoryLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        GeneralPages.instance.validate();
    }
}
