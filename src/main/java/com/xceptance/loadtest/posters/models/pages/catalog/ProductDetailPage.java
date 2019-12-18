package com.xceptance.loadtest.posters.models.pages.catalog;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

public class ProductDetailPage extends GeneralPages
{
	public static final ProductDetailPage instance = new ProductDetailPage();
	
    @Override
    public void validate()
    {
        super.validate();

        Assert.assertTrue("Product detail page validation failed", Page.find().byId("addToCartForm").exists());
        Assert.assertTrue("Product detail page validation failed", Page.find().byId("titleProductName").exists());
    }

    @Override
    public boolean is()
    {
    	return super.is() &&
    			Page.find().byId("addToCartForm").exists() &&
    			Page.find().byId("titleProductName").exists(); 
    }
    
    public List<HtmlElement> getProductItems()
    {
    	// Returns all the configurable product items, can be one in case of normal product detail page or multiple in case of product set/bundle 
    	return Page.find().byId("main").all();
    }
    
    public List<HtmlElement> getVariationAttributes(HtmlElement productItem)
    {
    	// Returns all configurable variation attributes
    	return HPU.find().in(productItem).byXPath(".//div[contains(@class, 'form-group') and contains(@class, 'row') and not(./p[@id='prodDescriptionDetail'])]").all();
    }
    
    public HtmlElement getAddToCartButton(HtmlElement productItem)
    {
    	// Returns add to cart button
    	return HPU.find().in(productItem).byId("btnAddToCart").asserted("Failed to find add to cart button for given product item").single();
    }
}