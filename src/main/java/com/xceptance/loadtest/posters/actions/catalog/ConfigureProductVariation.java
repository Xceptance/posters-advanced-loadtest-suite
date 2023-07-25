package com.xceptance.loadtest.posters.actions.catalog;

import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;

import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlRadioButtonInput;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.FormUtils;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Chooses a random product configuration.
 * 
 * @author Xceptance Software Technologies
 */
public class ConfigureProductVariation extends AjaxAction<ConfigureProductVariation>
{
	private Hashtable<HtmlElement, List<HtmlElement>> selectVariationAttributes = new Hashtable<>();
	
	private Hashtable<HtmlElement, List<HtmlElement>> inputVariationAttributes = new Hashtable<>();
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void precheck()
    {
    	// Get all variation attributes
    	List<HtmlElement> variationAttributes = ProductDetailPage.instance.getVariationAttributes();
    	Assert.assertTrue("Expected at least one variation attribute", variationAttributes.size() > 0);
    	
    	// Retrieve select variation attributes that are not yet configured
    	extractUnconfiguredSelectVariationAttributes(variationAttributes);

    	// Retrieve input variation attributes that are not yet configured    	
    	extractUnconfiguredInputVariationAttributes(variationAttributes);
    	
    	// Sanity check that we have at least a single configurable variation attribute
    	Assert.assertFalse("Expected at least one configurable variation attribute", selectVariationAttributes.isEmpty() && inputVariationAttributes.isEmpty());
    }
    
    private void extractUnconfiguredSelectVariationAttributes(List<HtmlElement> variationAttributes)
    {
    	for(HtmlElement attribute : variationAttributes)
    	{
    		List<HtmlElement> options = HPU.find().in(attribute).byCss("select > option[title]:not([selected])").all();
    		if(!options.isEmpty())
    		{
    			selectVariationAttributes.put(attribute, options);
    		}
    	}
    }

    private void extractUnconfiguredInputVariationAttributes(List<HtmlElement> variationAttributes)
    {
    	for(HtmlElement attribute : variationAttributes)
    	{
    		List<HtmlElement> inputs = HPU.find().in(attribute).byCss("input:not(.selected):not(.checked)").all();
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
    	for(HtmlElement attribute : selectVariationAttributes.keySet())
    	{
    		List<HtmlElement> options = selectVariationAttributes.get(attribute);

    		// Randomly select an option
    		HtmlOption selectedOption = (HtmlOption)options.get(XltRandom.nextInt(options.size()));
    		FormUtils.selectOption(selectedOption);
    		
    		// Send product update request for the selected option
    		executeProductPriceUpdate(selectedOption);
    	}
    }
    
    private void executeProductPriceUpdate(HtmlElement optionElement) throws Exception
    {
    	String onChange = ((HtmlElement)optionElement.getParentNode()).getAttribute("onchange");
    	
    	// Extract PID from 'onchange' attribute
    	String pid = RegExUtils.getFirstMatch(onChange, "updatePrice\\(this,\\s*(\\d+)\\)", 1);
    	Assert.assertTrue("Expected PID to be contained in onchange attribute", !StringUtils.isBlank(pid));
    	
    	// Get size of selected option
    	String selectedSize = optionElement.getAttribute("title");
    	Assert.assertTrue("Expected title attribute containing size", !StringUtils.isBlank(selectedSize));
    	
    	// Set the selected option text as value of the select element
    	((HtmlElement)optionElement.getParentNode()).setAttribute("value", selectedSize);
    	
    	// Request the updated price
    	WebResponse response = new HttpRequest()
    							.XHR()
    							.POST()
    							.url("/posters/updatePrice")
    							.param("size", selectedSize)
    							.param("productId", pid)
    							.assertJSONObject("Expected price update in response'", true, json -> json.has("newPrice")) 
    							.fire();
    	
    	// Update returned price information in page
    	Page.find().byId("prodPrice").asserted().single().setTextContent(new JSONObject(response.getContentAsString()).getString("newPrice"));
    }

    private void configureInputVariationAttributes()
    {
    	for(HtmlElement attribute : inputVariationAttributes.keySet())
    	{
    		List<HtmlElement> inputs = inputVariationAttributes.get(attribute);
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