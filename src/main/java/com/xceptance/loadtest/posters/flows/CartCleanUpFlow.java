package com.xceptance.loadtest.posters.flows;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.data.PaymentLimitations;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.FlowStoppedException;
import com.xceptance.loadtest.api.util.SafetyBreak;
import com.xceptance.loadtest.posters.actions.cart.ChangeLineItemQuantity;
import com.xceptance.loadtest.posters.actions.cart.RemoveCartItem;
import com.xceptance.loadtest.posters.actions.cart.ViewCart;
import com.xceptance.loadtest.posters.models.components.cart.CartEmpty;
import com.xceptance.loadtest.posters.models.components.cart.CartPrices;
import com.xceptance.loadtest.posters.models.components.cart.CartTable;
import com.xceptance.loadtest.posters.models.components.cart.CartTable.ItemInformation;
import com.xceptance.loadtest.posters.models.pages.cart.CartPage;
import com.xceptance.xlt.api.engine.Session;


/**
 * Remove stale items from the cart
 *
 * @author rschwietzke
 *
 */
public class CartCleanUpFlow extends Flow
{
    /** Define how many attempts should be done at maximum to adjust the cart completely. */
    private final SafetyBreak validCartSafetyBreak = new SafetyBreak(4);

    /** Define how many attempts should be done at maximum to remove all unavailable items. */
    private final SafetyBreak outOfStockSafetyBreak = new SafetyBreak(4);

    /** Define how many attempts should be done at maximum to adjust the cart totals. */
    private final SafetyBreak priceLimitsSafetyBreak = new SafetyBreak(8);

    /**
     * The totals of the cart before the line items get updated.
     */
    private double cartTotals;

    /**
     * Reference to the cart table.
     */
    CartTable table = CartTable.instance;

    /**
     * Reference to the price.
     */
    CartPrices prices = CartPrices.instance;

    /**
     * Stored information from the most expensive item.
     */
    // private ItemInformation mostExpensiveItem;
    private PaymentLimitations pl;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        pl = Context.configuration().paymentLimitations.value;
        // Indicates if something was (or should have been) modified in the cart.
        boolean cartModificationsExecuted;

        // Repeat as long as something was modified by one or the other cart action (or both).
        do
        {
            // Check overall attempts.
            validCartSafetyBreak.check("Cart could not be put into a valid state in a reasonable amount of tries.");

            // Reset our flag.
            cartModificationsExecuted = false;
            // remove first to get a clean cart
            cartModificationsExecuted |= removeUnavailableProducts();

            cartModificationsExecuted |= adjustCartTotals();
        }
        while (cartModificationsExecuted == true);

        // do the cart again if not yet there
        if (CartPage.instance.is() == false)
        {
            new ViewCart().run();
        }

        return true;
    }

    /**
     * Remove unavailable items from the cart, unavailable are all items without price or which are
     * not orderable.
     *
     * @return
     * @throws Throwable
     */
    private boolean removeUnavailableProducts() throws Throwable
    {
        boolean cartShouldBeModified = false;

        // ok, cleanup the cart until we do not have anything to cleanup anymore or
        // the cart is empty

        List<HtmlElement> unavailableItems = CartPage.instance.cartTable.getUnavailableLineItems();

        // Start with a clean safety break.
        outOfStockSafetyBreak.reset();

        while (!unavailableItems.isEmpty())
        {
            // Check number of attempts.
            outOfStockSafetyBreak.check("Unable to remove all unavailable products from cart.");

            // Indicate that the cart should be modified because it has out of stock items
            cartShouldBeModified = true;

            // remove the item, just start with the first one
            new RemoveCartItem(table.getItemInformation(unavailableItems.get(0))).run();

            // update, because a lot of things might have changed
            unavailableItems = CartPage.instance.cartTable.getUnavailableLineItems();

            // Stop if we removed all items from cart.
            assertCartEmpty("Cart was empty after clean up.");
        }

        return cartShouldBeModified;
    }

    /**
     * Adjust cart totals to be within configured limit.
     *
     * @throws Throwable
     */
    private boolean adjustCartTotals() throws Throwable
    {
        boolean cartShouldBeModified = false;
        cartTotals = prices.priceStringToNumber(prices.getCartTotals().asserted().first().getTextContent().trim());

        // Start with a clean limiter.
        priceLimitsSafetyBreak.reset();

        // As long as we are within disallowed price range adjust quantity of one item to modify
        // cart totals.
        while (rangeOfInterest())
        {
            // Check number of attempts.
            priceLimitsSafetyBreak.check("Unable to change quantity to leave payment limits.");

            // Indicate that the cart should be modified because we are not in the given price range

            cartShouldBeModified = check();

            // new ChangeLineItemQuantity(newQuantity, mostExpensiveItemQantityField).run();

            // Stop if we removed all items from cart.
            assertCartEmpty("Cart was empty after item quantity update.");
        }

        return cartShouldBeModified;
    }

    /**
     * Check if we are in a range were we should adjust the cart totals at all.
     *
     * @return true if we need to adjust the cart, false otherwise.
     */
    private boolean rangeOfInterest()
    {
        if (!pl.blocked.range.inside(cartTotals) &&
             pl.permitted.range.inside(cartTotals))
        {
            return false;
        }
        return true;
    }

    /**
     * Check if we needed to adjust the cart either increase or decrease the cart totals.
     *
     * @return true if we adjusted the cart, false otherwise.
     * @throws Throwable
     */
    private boolean check() throws Throwable
    {
        // Get the cart totals.
        cartTotals = prices.priceStringToNumber(prices.getCartTotals().asserted().first().getTextContent().trim());

        if (rangeOfInterest())
        {
            // Get most expensive item.
            final ItemInformation mostExpensiveItem = table.getMostExpensiveAvailableItem();

            // Get us a target value
            return calculateNewQuantity(cartTotals, mostExpensiveItem);
        }
        return false;
    }

    /**
     *
     * @param cartTotals
     * @param mostExpensiveItem
     * @return true if we had done anything to ensure we check again.
     * @throws Throwable
     */
    private boolean calculateNewQuantity(final double cartTotals, final ItemInformation mostExpensiveItem)
                    throws Throwable
    {
        // init newValue to catch the assertion
        int newQuantity = Integer.MIN_VALUE;

        // ok, the following code could be a little shaky due to many assumption
        // about the ranges permitted and blocked

        // are we too low?
        // this logic will kick in most of the time, because the permitted range
        // is easier to specify
        // and more logically to reach
        if (cartTotals < pl.permitted.min)
        {
            // Bump the price up above the min permitted
            newQuantity = determineBumbUpQuantity(mostExpensiveItem.price, cartTotals, mostExpensiveItem.quantity, pl.permitted.min, mostExpensiveItem.maxQuantity);
            new ChangeLineItemQuantity(newQuantity, mostExpensiveItem).run();
        }
        else if (cartTotals > pl.permitted.max)
        {
            // reduce, we are too high
            final ItemInformation mostExpensiveMaxItem = table.getMostExpensiveMaxItem();
            newQuantity = determinePlaneDownQuantity(mostExpensiveMaxItem.price, cartTotals, mostExpensiveMaxItem.quantity);
            // check if we need to adjust the amount again
            if (newQuantity != 0)
            {
                new ChangeLineItemQuantity(newQuantity, mostExpensiveMaxItem).run();
            }
            else
            {
                new RemoveCartItem(mostExpensiveMaxItem).run();
            }
        }
        else if (pl.blocked.range.inside(cartTotals))
        {
            // We are within permitted range and within blocked range, so ranges
            // do overlap. see what the larger range is, upper or lower.
            // If (permitted.min > blocked.min) or (permitted.max < blocked.max),
            // the result will be negative and therefore always smaller.

            final double lowerBound = (cartTotals - pl.blocked.min);
            final double upperBound = (pl.blocked.max - cartTotals);

            if (lowerBound < upperBound &&
                cartTotals - (table.getMostExpensiveMaxItem().price / table.getMostExpensiveMaxItem().quantity) >= pl.permitted.min)
            {
                // Go lower to get below the min blocked range
                final ItemInformation mostExpensiveMaxItem = table.getMostExpensiveMaxItem();
                newQuantity = determinePlaneDownQuantity(mostExpensiveMaxItem.price, cartTotals, mostExpensiveMaxItem.quantity);

                if (newQuantity == 0)
                {
                    new RemoveCartItem(mostExpensiveMaxItem).run();
                }
                else
                {
                    new ChangeLineItemQuantity(newQuantity, mostExpensiveMaxItem).run();
                }
            }
            else if (cartTotals + table.getMostExpensiveAvailableItem().price <= pl.permitted.max)
            {
                // Try to get above the max blocked price
                newQuantity = determineBumbUpQuantity(mostExpensiveItem.price, cartTotals, mostExpensiveItem.quantity, pl.blocked.max, mostExpensiveItem.maxQuantity);
                new ChangeLineItemQuantity(newQuantity, mostExpensiveItem).run();
            }
            else
            {
                Assert.fail("Adjusting the cart is not possible.");
            }
        }
        return rangeOfInterest();
    }

    /**
     * Determine the new quantity to reach the desired cart totals. If the new calculated value is
     * above the max possible value, we use the max value instead.
     *
     * @param mostExpensiveItemPrice
     * @param cartTotals
     * @param currentQuantity
     * @param minAmount
     * @param maxQuant
     * @return New quantity to increase cart totals or the max available quantity for the current
     *         item.
     */
    private int determineBumbUpQuantity(final double mostExpensiveItemPrice, final double cartTotals, final int currentQuantity, final int minAmount, final int maxQuant)
    {
        // On top of the current quantity, add as many items as necessary to get
        // close to the min. permitted limit. Add 1 to make sure we are above
        // the lower limit
        final int newQuantity = (int) (Math.floor((minAmount - cartTotals) / mostExpensiveItemPrice) + currentQuantity + 1);
        return newQuantity < maxQuant ? newQuantity : maxQuant;
    }

    /**
     * Determine the new quantity to reach the desired cart totals. To ensure we are in the desired limit.
     * @param mostExpensiveItemPrice
     * @param cartTotals
     * @param currentQuantity
     * @return New quantity to decrease cart totals.
     */
    private int determinePlaneDownQuantity(final double mostExpensiveItemPrice, final double cartTotals, final int currentQuantity)
    {
        // The current quantity get decreased, remove as many items as necessary to get
        // close to the permitted limit.
        final int newQuantity = currentQuantity - (int) Math.ceil((cartTotals - pl.blocked.min) / mostExpensiveItemPrice);
        return newQuantity <= 0 ? 0 : newQuantity;
    }

    /**
     * Asserts that cart is NOT empty.
     *
     * @param failMsg
     *            optional exception message
     * @throws FlowStoppedException
     *             if cart is empty
     */
    private void assertCartEmpty(final String failMsg) throws FlowStoppedException
    {
        // nothing left in cart
        if (CartEmpty.instance.exists())
        {
            EventLogger.DEFAULT.error("FlowStopped - " + Session.getCurrent().getUserName(), failMsg);
            throw new FlowStoppedException(failMsg);
        }
    }
}
