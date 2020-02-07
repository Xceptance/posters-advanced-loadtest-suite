package com.xceptance.loadtest.posters.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;

/**
 * Page state helper.
 * 
 * Utilities to access JSON data embedded in the current page. A page might store multiple elements associated with a
 * certain state, e.g. '#products-state' will contain the JSON for all products currently available at the page. Upon
 * page load the state data is lost. General access via embed*(), retrieve*(), has*() but also more specific access
 * (e.g. accessing certain child elements of a given JSON) is possible. Actions will typically create (embed) the state
 * data and components will consume/update it.      
 * 
 * @author Xceptance Software Technologies
 */
public class PageState
{
	private static String EMBEDDED_PRODUCTS_ID = "embedded-product-listing-page-state";
	
	private static void embed(String id, String jsonString)
	{
		embed(id, AjaxUtils.convertToJson(jsonString));
	}

	/**
	 * Embeds JSON data in the current page at the given identifier.
	 * 
	 * @param id The identifier where the data will be embedded.
	 * @param json The JSON data to embed.
	 */
	private static void embed(String id, JSONObject json)
	{
		HtmlElement embeddedStateElement = Page.getOrCreateByID(id, Page.find().byCss("body").asserted("Expected body element in page").single());
		embeddedStateElement.setTextContent(json.toString(4));
	}

	/**
	 * Retrieves the JSON data embedded at the given identifier.
	 * 
	 * @param id The identifier where the embedded JSON data is retrieved.
	 * @return The retrieved JSON data.
	 */
	private static JSONObject retrieve(String id)
	{
		LookUpResult embeddedStateElement = Page.find().byId(id);
		if(embeddedStateElement.exists())
		{
			return AjaxUtils.convertToJson(embeddedStateElement.single().getTextContent());
		}
		
		return null;
	}
	
	/**
	 * Removes the element and its embedded data.
	 * 
	 * @param id The identifier of the embedded data element.
	 */
//	private static void remove(String id)
//	{
//		LookUpResult embeddedStateElement = Page.find().byId(id);
//		if(embeddedStateElement.exists())
//		{
//			embeddedStateElement.asserted("Expected single embedded state element").single().remove();
//		}
//	}
	
	/**
	 * Checks if a certain identifier is contained in the page (and supposedly contains embedded state data).
	 * 
	 * @param id The identifier to check.
	 * @return true if the identifier exists, false otherwise.
	 */
	private static boolean has(String id)
	{
		return Page.find().byId(id).exists();
	}

	/**
	 * Checks if a certain identifier is contained in the page and if it has data embedded.
	 * 
	 * @param id The identifier to check.
	 * @return true if the identifier exists and data is embedded, false otherwise.
	 */
//	private static boolean hasData(String id)
//	{
//		return Page.find().byId(id).exists() && Page.find().byId(id).asserted("Expected single embedded state element").single().getTextContent().isBlank();
//	}

	/// Data specific access
	
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