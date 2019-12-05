package posters.actions.catalog;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.xlt.api.util.XltRandom;

import posters.pages.catalog.ProductDetailPage;
import posters.pages.components.pdp.ProductDetail;
import posters.pages.components.pdp.ProductDetailVariation;

/**
 * Chooses a random product configuration.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigureProductVariation extends AbstractConfigureProduct<ConfigureProductVariation>
{
    /**
     * The attribute to work on
     */
    private HtmlElement variationAttribute;

    /**
     * The name of the attribute
     */
    private final String variationAttributeName;

    private final ProductDetailPage<? extends ProductDetail> page;
    private final ProductDetailVariation productDetail;

    /**
     * Constructor
     *
     * @param quantity
     *            the quantity to select later if needed
     */
    public ConfigureProductVariation(final ProductDetailPage<? extends ProductDetail> page,
                    final ProductDetailVariation productDetail,
                    final HtmlElement item, final String variationAttributeName)
    {
        super(item);

        this.variationAttributeName = variationAttributeName;
        this.page = page;
        this.productDetail = productDetail;
    }

    @Override
    public void precheck()
    {
        variationAttribute = productDetail.getVariationAttributeByName(item, variationAttributeName).asserted().single();

        // check that there is stuff to do
        productDetail.getSelectableButUnselectedVariationAttributes(variationAttribute)
                        .asserted(
                                        "No unselected attributes found for '" + variationAttributeName
                                                        + "', maybe just one there that is already selected or the structure is different")
                        .all();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final List<HtmlElement> unSelectedElements = productDetail.getSelectableButUnselectedVariationAttributes(variationAttribute).all();

        // ok, take one from our list and select it
        final HtmlElement newlySelectedElement = unSelectedElements.get(XltRandom.nextInt(unSelectedElements.size()));

        final String url = productDetail.getVariationUpdateUrl(newlySelectedElement);

        // get the data
        final WebResponse response = call(url);

        // update item in case we replaced it
        item = productDetail.render(response.getContentAsString(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        page.validate();
    }
}
