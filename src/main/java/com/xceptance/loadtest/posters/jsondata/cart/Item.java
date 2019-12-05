package com.xceptance.loadtest.posters.jsondata.cart;

import java.util.List;
import java.util.Map;

import com.xceptance.loadtest.posters.jsondata.product.Availability;
import com.xceptance.loadtest.posters.jsondata.product.Image;
import com.xceptance.loadtest.posters.jsondata.product.Pricing;
import com.xceptance.loadtest.posters.jsondata.product.Promotion;

public class Item
{
    public String id;
    public String productName;

    public Pricing price;

    public String productType;

    public Map<String, List<Image>> images;

    public int rating;

    public List<com.xceptance.loadtest.posters.jsondata.cart.VariationAttribute> variationAttributes;

    public QuantityOptions quantityOptions;

    public PriceTotal priceTotal;

    public boolean isBonusProductLineItem;
    public boolean isGift;
    public String UUID;
    public int quantity;
    public boolean isOrderable;
    public List<Promotion> promotions;

    public String renderedPromotions;

    // public List<Attribute> attributes;
    public Availability availability;

    public List<Item> bundledProductLineItems;
}
