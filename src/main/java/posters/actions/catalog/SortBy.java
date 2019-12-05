package posters.actions.catalog;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.util.FormUtils;
import com.xceptance.loadtest.api.util.HttpRequest;

import posters.pages.catalog.ProductListingPage;

/**
 * Changes sorting option.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class SortBy extends AjaxAction<SortBy>
{
    /**
     * The option's name.
     */
    private String originalOptionName;

    /**
     * The selected option.
     */
    private HtmlElement sortOption;

    /**
     * Checks if sorting is possible. If so, {@link #sortOption} will save a randomly chosen
     * unselected sort option and {@link #targetOptionName} contains the option's text. Can
     * <b>only</b> be performed on a product grid page.
     */
    @Override
    public void precheck()
    {
        if (!ProductListingPage.instance.is())
        {
            Assert.fail("Expected to be on product listing page");
        }

        // Get a random unselected sort option.
        sortOption = ProductListingPage.instance.gridSort.getUnselectedOptions().asserted("No sorting options found in page.").random();

        // Remember the option name.
        originalOptionName = sortOption.getTextContent().trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Retrieve the url
        final String url = sortOption.getAttribute("value");

        // Execute the sort by XHR call
        final HttpRequest sortRequest = new HttpRequest().XHR().url(url)
                        .param("selectedUrl", url)
                        .assertContent("Nothing came back from HttpRequest.", true, HttpRequest.NOT_BLANK)
                        .assertStatusCode(200)
                        .replaceContentOf(ProductListingPage.instance.productGrid.locate().single());

        // Fired twice at the SFRA template instance
        sortRequest.fire();
        sortRequest.fire();

        // Set the option as selected option
        FormUtils.selectOption((HtmlOption) sortOption);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Validate that we end up on PLP
        ProductListingPage.instance.validate();

        // Check that the desired sort option is the selected one.
        final String currentOptionName = ProductListingPage.instance.gridSort.getSelectedOption()
                        .asserted("No selected sort option found in page.").single()
                        .getTextContent()
                        .trim();

        Assert.assertEquals("Sorting has not been performed correctly, sort option is the same", originalOptionName, currentOptionName);
    }
}
