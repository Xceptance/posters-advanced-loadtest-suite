package posters.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;

import posters.pages.catalog.CategoryLandingPage;
import posters.pages.catalog.ProductListingPage;

/**
 * Selects a category from the top navigation menu.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class ClickACategorySlot extends PageAction<ClickACategorySlot>
{
    private HtmlElement categoryLink;

    /**
     * Check that we can do this and should do it. You can already collect data here that you save
     * as state, because this won't be called twice by the framework.
     */
    @Override
    public void precheck()
    {
        categoryLink = CategoryLandingPage.instance.getCategorySlotsLinks().asserted("No top categories found").random();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        loadDebugUrlOrElse("Add full or relative url here").loadPageByClick(categoryLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        ProductListingPage.instance.validate();
    }
}
