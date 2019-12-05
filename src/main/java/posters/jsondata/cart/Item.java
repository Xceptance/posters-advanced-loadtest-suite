package posters.jsondata.cart;

import java.util.List;
import java.util.Map;

import posters.jsondata.product.Availability;
import posters.jsondata.product.Image;
import posters.jsondata.product.Pricing;
import posters.jsondata.product.Promotion;

public class Item
{
    public String id;
    public String productName;

    public Pricing price;

    public String productType;

    public Map<String, List<Image>> images;

    public int rating;

    public List<posters.jsondata.cart.VariationAttribute> variationAttributes;

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
