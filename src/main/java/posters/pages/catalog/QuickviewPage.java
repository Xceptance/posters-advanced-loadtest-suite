package posters.pages.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.pages.Page;

import posters.pages.components.qv.Quickview;

public class QuickviewPage extends ProductListingPage
{
    public static final QuickviewPage instance = new QuickviewPage();
    public final Quickview quickview = Quickview.instance;

    @Override
    public void validate()
    {
        validate(has(quickview, searchResult, refinementBar, productGrid, itemCount));
    }

    @Override
    public boolean is()
    {
        return matches(has(quickview, searchResult, refinementBar, productGrid, itemCount));
    }

    public HtmlElement getQuickviewContainer()
    {
        return Page.getOrCreateByID("quickViewModal");
    }
}
