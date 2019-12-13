package com.xceptance.loadtest.posters.pages.catalog;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class ProductDetailPage extends GeneralPages
{
	public static final ProductDetailPage instance = new ProductDetailPage();
	
    @Override
    public void validate()
    {
        super.validate();
        
    	// TODO
        //validate(has(productDetail), hasNot(quickview));
    }

    @Override
    public boolean is()
    {
    	// TODO
        //return super.is() && matches(has(productDetail), hasNot(quickview));
    	return true;
    }
    
    public List<HtmlElement> getConfigurableProductItems()
    {
    	// TODO
    	
    	// Returns all the configurable product items, can be one in case of normal product detail page or multiple in case of product set/bundle 
    	return Page.find().byCss("TODO").all();
    }
    
    public List<HtmlElement> getVariationAttributes(HtmlElement productItem)
    {
    	// TODO
    	
    	// Returns all configurable variation attributes
    	return HPU.find().in(productItem).byCss("TODO").all();
    }
}