package com.xceptance.loadtest.posters.jsondata;

import com.google.gson.annotations.SerializedName;
import com.xceptance.loadtest.posters.jsondata.cart.Product;

public class AddToCartJSON
{
	public Product product;
	
	@SerializedName("headerCartOverview")
	public int itemsInCart;
	
    public boolean error;
    
    public String message;
}