package com.xceptance.loadtest.headless.pages.components.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.jsondata.CartJSON;
import com.xceptance.loadtest.headless.jsondata.CartUpdateJSON;

public class CartTable implements Component
{
    public final static CartTable instance = new CartTable();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss(".cart.container");
    }

    /**
     * Indicates if this component exists
     *
     * @return
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Get all product line items lines
     */
    public LookUpResult getLineItems()
    {
        return locate().byCss(".card.product-info");
    }

    /**
     * Get all product line items lines
     */
    public HtmlElement getLineItemQuantitySelect(final HtmlElement item)
    {
        return HPU.find().in(item).byCss(".line-item-quantity .quantity").asserted().single();
    }

    /**
     * Get the current selected item quantity.
     *
     * @param item
     * @return Quantity of the item.
     */
    public int getSelectedLineItemQuantity(final HtmlElement item)
    {
        return Integer.valueOf(HPU.find().in(item).byCss(".line-item-quantity .quantity option[selected]").asserted().single().getTextContent().trim());
    }

    /**
     * Get the current item max quantity.
     *
     * @param item
     * @return Max possible orderable value of the item.
     */
    public int getLineItemMaxQuantity(final HtmlElement item)
    {
        return Integer.valueOf(HPU.find().in(item).byCss(".line-item-quantity .quantity option").asserted().last().getTextContent().trim());
    }

    /**
     * Get the remove url.
     *
     * @param item
     * @return Url information of the item.
     */
    public HtmlElement getRemoveInfo(final HtmlElement item)
    {
        return HPU.find().in(item).byCss(".remove-product").asserted().first();
    }

    /**
     * What items are available
     *
     * @return a list of available items or an empty list otherwise
     */
    public List<HtmlElement> getAvailableLineItems()
    {
        final List<HtmlElement> result = new ArrayList<>();

        final List<HtmlElement> lineItems = getLineItems().all();
        for (final HtmlElement lineItem : lineItems)
        {
            if (isItemAvailable(lineItem))
            {
                result.add(lineItem);
            }
        }

        return result;
    }

    /**
     * Which items are not available
     *
     * @return a list of unavailable items or an empty list otherwise
     */
    public List<HtmlElement> getUnavailableLineItems()
    {
        final List<HtmlElement> unavailableItems = getLineItems().all().stream()
                        .filter(lineItem -> isItemAvailable(lineItem) == false)
                        .collect(Collectors.toList());

        return unavailableItems;
    }

    /**
     * Verify if this line item is available
     *
     * @param lineItem
     *            the line item to check
     *
     * @return true if available, false in all other cases
     */
    public static boolean isItemAvailable(final HtmlElement lineItem)
    {
        final LookUpResult element = HPU.find().in(lineItem).byCss(".line-item-availability");

        if (element.exists())
        {
            // ok, we got something, turn it into text, because we lack markers, darn
            final String text = element.first().asText();

            // check against the current msg, let it fail with an NPE in case this message is not
            // set, easier to notice when compiling the tests
            if (text.contains(Context.configuration().localizedText("cart.lineitem.availability.instock.message")))
            {
                return true;
            }
        }

        // no idea, hence not available
        return false;
    }

    /**
     * Render the cart site after removing an item from the cart table.
     */
    public void renderAndUpdate(final HtmlRenderer renderer)
    {
        final String csrfLocator = ".totals form.promo-code-form input[name='csrf_token']";
        final String token = locate().byCss(csrfLocator).asserted().single().getAttribute("value");

        final String checkOutLocator = ".checkout-continue a.checkout-btn";
        final String checkoutUrl = locate().byCss(checkOutLocator).asserted().single().getAttribute("href");

        // items
        renderer.template("/templates/cart/cart-table.ftlh").replace(locate().asserted().single());

        // rescue the csrf token but only if we still have a cart
        // rescue the checkout url, because it is not part of the json returned
        if (renderer.getJson(CartJSON.class, "data").basket.numItems > 0)
        {
            locate().byCss(csrfLocator).asserted().single().setAttribute("value", token);
            locate().byCss(checkOutLocator).asserted().single().setAttribute("href", checkoutUrl);
        }
    }

    /**
     * Render the cart site after adjusting the quantity of an cart table item.
     */
    public void renderAndUpdateCleanUp(final HtmlRenderer renderer)
    {
        final String csrfLocator = ".totals form.promo-code-form input[name='csrf_token']";
        final String token = locate().byCss(csrfLocator).asserted().single().getAttribute("value");

        final String checkOutLocator = ".checkout-continue a.checkout-btn";
        final String checkoutUrl = locate().byCss(checkOutLocator).asserted().single().getAttribute("href");

        // items
        renderer.template("/templates/cart/cart-table-update.ftlh");

        Page.mapHtml().html(renderer).byCSS(".cart.container").byCSS(".number-of-items").map();

        // rescue the csrf token but only if we still have a cart
        // rescue the checkout url, because it is not part of the json returned
        if (renderer.getJson(CartUpdateJSON.class, "data").numItems > 0)
        {
            locate().byCss(csrfLocator).asserted().single().setAttribute("value", token);
            locate().byCss(checkOutLocator).asserted().single().setAttribute("href", checkoutUrl);
        }
    }

    /**
     * Determine the information for a cart item. Breaks if not such information exists.
     *
     * @param item
     *            the item to check
     * @return the removal information
     */
    public ItemInformation getItemInformation(final HtmlElement item)
    {
        final double itemPrice = CartPrices.instance.getItemPrice(item);
        final HtmlElement lineItem = getLineItemQuantitySelect(item);
        final HtmlElement removeInfo = getRemoveInfo(item);
        final int value = getSelectedLineItemQuantity(item);
        final int maxValue = getLineItemMaxQuantity(item);

        return new ItemInformation(lineItem.getAttribute("data-action"),
                                   removeInfo.getAttribute("data-action"),
                                   lineItem.getAttribute("data-pid"),
                                   lineItem.getAttribute("data-uuid"),
                                   itemPrice,
                                   value,
                                   maxValue);
    }

    /**
     * Determine the information for all cart item. Breaks if not such information exists.
     *
     * @param item
     *            the item to check
     * @return the removal information
     */
    public List<ItemInformation> getAllItemInformation()
    {
        final List<HtmlElement> availableLineItems = getAvailableLineItems();
        final List<ItemInformation> data = new ArrayList<>();

        for (final HtmlElement item : availableLineItems)
        {
            data.add(getItemInformation(item));
        }

        Collections.sort(data, (a, b) -> a.price > b.price ? -1 : a.price == b.price ? 0 : 1);
        return data;
    }

    /**
     * Iterates over the list of line items and returns the row element of the most expensive item.
     *
     * @return row element of most expensive item
     */
    public ItemInformation getMostExpensiveAvailableItem()
    {
        List<ItemInformation> allItemInformation = getAllItemInformation();
        allItemInformation = allItemInformation.stream().filter(p -> p != null).filter(p -> p.quantity != p.maxQuantity).collect(Collectors.toList());

        return allItemInformation.size() > 0 ? allItemInformation.get(0) : null;
    }

    /**
     * Get the first expensive item which is at its maximum amount.
     * 
     * @return item information
     */
    public ItemInformation getMostExpensiveMaxItem()
    {
        List<ItemInformation> allItemInformation = getAllItemInformation();
        allItemInformation = allItemInformation.stream().filter(p -> p != null).collect(Collectors.toList());

        return allItemInformation.size() > 0 ? allItemInformation.get(0) : null;
    }

    /**
     * Get the most expensive item of all cart items.
     *
     * @return The most expensive cart item.
     */
    public HtmlElement getMostExpensiveItem()
    {
        double maxPrice = Double.MIN_VALUE;
        HtmlElement maxPriceRow = null;

        final List<HtmlElement> allProducts = getLineItems().all();
        for (final HtmlElement row : allProducts)
        {
            final double itemPrice = CartPrices.instance.getItemPrice(row);

            if (maxPrice < itemPrice)
            {
                maxPrice = itemPrice;
                maxPriceRow = row;
            }
        }
        return maxPriceRow;
    }

    /**
     * Nested helper class which holds all needed information of cart items.
     */
    public class ItemInformation
    {
        public String updateUrl;
        public String removeUrl;
        public String pid;
        public String uuid;
        public double price;
        public int quantity;
        public int maxQuantity;

        public ItemInformation(final String updateUrl, final String removeUrl, final String pid, final String uuid, final double price, final int quantity, final int maxQuantity)
        {
            this.updateUrl = updateUrl;
            this.removeUrl = removeUrl;
            this.pid = pid;
            this.uuid = uuid;
            this.price = price;
            this.quantity = quantity;
            this.maxQuantity = maxQuantity;
        }
    }
}
