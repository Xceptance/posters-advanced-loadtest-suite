package com.xceptance.loadtest.headless.jsondata.product;

import java.util.List;
import java.util.Map;

public class Product
{
    public String id;
    public String productName;

    public Pricing price;

    public String productType;

    public Map<String, List<Image>> images;

    public double rating;

    public List<VariationAttribute> variationAttributes;

    public boolean available;
    public String shortDescription;
    public String longDescription;
    public boolean online;
    public boolean searchable;
    public int minOrderQuantity;
    public int maxOrderQuantity;

    public boolean readyToOrder;

    public List<Promotion> promotions;

    // attributes ??

    public Availability availability;

    public String selectedProductUrl;
    public int selectedQuantity;

    public List<Option> options;

    public List<Quantity> quantities;

    // public String raw; ??
    public String attributesHtml;
}
