package com.xceptance.loadtest.posters.actions.catalog;

import com.xceptance.loadtest.api.actions.HtmlUnitAjaxAction;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.xlt.api.util.XltRandom;

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.Hashtable;
import java.util.List;

/**
 * Chooses a random product configuration.
 * 
 * @author Xceptance Software Technologies
 */
public class ConfigureProductVariation extends HtmlUnitAjaxAction<ConfigureProductVariation>
{
    private final Hashtable<HtmlElement, List<HtmlElement>> selectVariationAttributes = new Hashtable<>();

    private final Hashtable<HtmlElement, List<HtmlElement>> inputVariationAttributes = new Hashtable<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
        // Get all variation attributes
        final List<HtmlElement> variationAttributes = ProductDetailPage.instance.getVariationAttributes();
        Assert.assertTrue("Expected at least one variation attribute", variationAttributes.size() > 0);

        // Retrieve select variation attributes that are not yet configured
        extractUnconfiguredSelectVariationAttributes(variationAttributes);

        // Retrieve input variation attributes that are not yet configured    	
        extractUnconfiguredInputVariationAttributes(variationAttributes);

        // Sanity check that we have at least a single configurable variation attribute
        Assert.assertFalse("Expected at least one configurable variation attribute", selectVariationAttributes.isEmpty() && inputVariationAttributes.isEmpty());
    }

    private void extractUnconfiguredSelectVariationAttributes(final List<HtmlElement> variationAttributes)
    {
        for(final HtmlElement attribute : variationAttributes)
        {
            final List<HtmlElement> options = HPU.find().in(attribute).byCss("select > option[title]:not([selected])").all();
            if(!options.isEmpty())
            {
                selectVariationAttributes.put(attribute, options);
            }
        }
    }

    private void extractUnconfiguredInputVariationAttributes(final List<HtmlElement> variationAttributes)
    {
        for(final HtmlElement attribute : variationAttributes)
        {
            final List<HtmlElement> inputs = HPU.find().in(attribute).byCss("input:not(.selected):not(.checked)").all();
            if(!inputs.isEmpty())
            {
                inputVariationAttributes.put(attribute, inputs);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Handle all configurations with select attribute
        configureSelectVariationAttributes();

        // Handle all configurations with input elements
        configureInputVariationAttributes();
    }

    private void configureSelectVariationAttributes() throws Exception
    {
        for(final HtmlElement attribute : selectVariationAttributes.keySet())
        {
            final List<HtmlElement> options = selectVariationAttributes.get(attribute);

            // Randomly select an option
            final HtmlOption selectedOption = (HtmlOption)options.get(XltRandom.nextInt(options.size()));
            FormUtils.selectOption(selectedOption);

            // Send product update request for the selected option
            executeProductPriceUpdate(selectedOption);
        }
    }

    private void executeProductPriceUpdate(final HtmlElement optionElement) throws Exception
    {	
        // read product ID
        final String pid = Page.find().byId("product-detail-form-product-id").asserted().single().getTextContent();

        Assert.assertTrue("Expected PID to be contained in onchange attribute", !StringUtils.isBlank(pid));

        // Get size of selected option
        final String selectedSize = optionElement.getAttribute("title");
        Assert.assertTrue("Expected title attribute containing size", !StringUtils.isBlank(selectedSize));

        // Set the selected option text as value of the select element
        ((HtmlElement)optionElement.getParentNode()).setAttribute("value", selectedSize);

        // Request the updated price
        final HttpRequest xhr = new HttpRequest()
                .XHR()
                .POST()
                .url("/en-US/updatePrice")
                .param("size", selectedSize)
                .param("productId", pid)
                .assertJSONObject("Expected price update in response'", true, json -> json.has("newPrice")) 
                .send();

        // Update returned price information in page
        Page.find().byId("product-detail-form-price").asserted().single()
                .setTextContent(new JSONObject(xhr.getResponseContentAsString()).getString("newPrice"));
    }

    private void configureInputVariationAttributes()
    {
        for(final HtmlElement attribute : inputVariationAttributes.keySet())
        {
            final List<HtmlElement> inputs = inputVariationAttributes.get(attribute);
            FormUtils.checkRadioButton((HtmlRadioButtonInput)inputs.get(XltRandom.nextInt(inputs.size())));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Nothing to validate
    }
}