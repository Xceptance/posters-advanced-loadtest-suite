package posters.actions.catalog;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.XltRandom;

import posters.pages.catalog.ProductDetailPage;
import posters.pages.components.pdp.ProductDetailOption;

/**
 * Chooses a random product configuration.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductOption extends AbstractConfigureProduct<ConfigureProductOption>
{
    /**
     * The attribute to work on
     */
    private final HtmlElement optionAttribute;

    /**
     * The page we are on
     */
    private final ProductDetailPage<ProductDetailOption> pdp;

    /**
     * Constructor
     *
     * @param quantity
     *            the quantity to select later if needed
     */
    public ConfigureProductOption(final ProductDetailPage<ProductDetailOption> pdp, final HtmlElement item, final HtmlElement optionAttribute)
    {
        super(item);
        this.pdp = pdp;
        this.optionAttribute = optionAttribute;
    }

    @Override
    public void precheck()
    {
        // check that there is stuff to do
        pdp.productDetail.getUnselectedOptions(optionAttribute).asserted("No unselected options").exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final List<HtmlElement> unSelectedElements = pdp.productDetail.getUnselectedOptions(optionAttribute).all();

        // ok, take one from our list and select it
        final HtmlElement newlySelectedElement = unSelectedElements.get(XltRandom.nextInt(unSelectedElements.size()));
        final String url = pdp.productDetail.getToBeSelectedOptionUrl(newlySelectedElement);

        // get the data
        final WebResponse response = call(url);

        // prepare the DOM
        item = pdp.productDetail.render(response.getContentAsString(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        pdp.validate();
    }
}
