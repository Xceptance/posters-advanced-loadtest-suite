package posters.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;

import posters.pages.general.GeneralPages;

/**
 * Selects a category from the top navigation menu.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class ClickATopCategory extends PageAction<ClickATopCategory>
{
    /**
     * Chosen random category link.
     */
    private HtmlElement categoryLink;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Get the link and click it
        categoryLink = GeneralPages.instance.navigation.getTopCategories().asserted("No top categories found").random();

        loadDebugUrlOrElse("/s/SiteGenesis/new arrivals/?lang=en_US").loadPageByClick(categoryLink);
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
