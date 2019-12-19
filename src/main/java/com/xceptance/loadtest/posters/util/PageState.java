package com.xceptance.loadtest.posters.util;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;

public class PageState
{
	private static String EMBEDDED_PRODUCTS_ID = "embedded-product-listing-page-state";
	
	private static void embed(String id, String jsonString)
	{
		embed(id, AjaxUtils.convertToJson(jsonString));
	}

	private static void embed(String id, JSONObject json)
	{
		// Embed response JSON in the page
		HtmlElement embeddedStateElement = Page.getOrCreateByID(id, Page.find().byCss("body").asserted("Expected body element in page").single());
		embeddedStateElement.setTextContent(json.toString(4));
	}

	private static JSONObject retrieve(String id)
	{
		LookUpResult products = Page.find().byId(id);
		if(products.exists())
		{
			return AjaxUtils.convertToJson(products.single().getTextContent());
		}
		
		return null;
	}
	
	private static boolean has(String id)
	{
		return Page.find().byId(id).exists();
	}

	public static void embedProducts(String productsJsonString)
	{
		embed(EMBEDDED_PRODUCTS_ID, productsJsonString);
	}

	public static void embedProducts(JSONObject productsJson)
	{
		embed(EMBEDDED_PRODUCTS_ID, productsJson);
	}
	
	public static JSONObject retrieveProducts()
	{
		return retrieve(EMBEDDED_PRODUCTS_ID);
	}
	
	public static Integer getProductCount()
	{
		JSONArray products = retrieveProducts().optJSONArray("products");
		if(products != null)
		{
			return products.length();
		}
		
		return 0;
	}
	
	public static boolean hasProducts()
	{
		return has(EMBEDDED_PRODUCTS_ID);
	}
}