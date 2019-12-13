package com.xceptance.loadtest.posters.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.posters.pages.catalog.ProductDetailPage;

/**
 * Chooses a random product configuration.
 */
public class ConfigureProductVariation extends AbstractConfigureProduct<ConfigureProductVariation>
{
    /**
     * The current product detail page. 
     */
    private final ProductDetailPage page;

    /**
     * The variation attribute to configure. 
     */
    private HtmlElement variationAttribute;

    /**
     * The name of the variation attribute.
     */
    private final String variationAttributeName;

    /**
     * Constructor
     *
     * @param quantity
     *            the quantity to select later if needed
     */
    public ConfigureProductVariation(final ProductDetailPage page, final HtmlElement item, final String variationAttributeName)
    {
        super(item);

        this.variationAttributeName = variationAttributeName;
        this.page = page;
    }

    @Override
    public void precheck()
    {
    	/*
        variationAttribute = productDetail.getVariationAttributeByName(item, variationAttributeName).asserted().single();

        // check that there is stuff to do
        productDetail.getSelectableButUnselectedVariationAttributes(variationAttribute)
                        .asserted(
                                        "No unselected attributes found for '" + variationAttributeName
                                                        + "', maybe just one there that is already selected or the structure is different")
                        .all();
    	 */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
    	/*
        final List<HtmlElement> unSelectedElements = productDetail.getSelectableButUnselectedVariationAttributes(variationAttribute).all();

        // ok, take one from our list and select it
        final HtmlElement newlySelectedElement = unSelectedElements.get(XltRandom.nextInt(unSelectedElements.size()));

        final String url = productDetail.getVariationUpdateUrl(newlySelectedElement);

        // get the data
        final WebResponse response = call(url);

        // update item in case we replaced it
        item = productDetail.render(response.getContentAsString(), item);
        */
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