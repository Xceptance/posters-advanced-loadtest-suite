package com.xceptance.loadtest.headless.pages.components.cart;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;

public class CartPrices implements Component
{
    public final static CartPrices instance = new CartPrices();

    /** Regular expression to match a price such as "Cart (1) Item, Total: $899.99" or "899,99€" */
    public static final String PRICE_REGEXP = "\\d{1,3}([,\\.]\\d{3})*[,\\.]\\d{2}";

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        return CartTable.instance.locate().byCss(".totals");
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
    public LookUpResult getCartTotals()
    {
        return locate().byCss(".grand-total");
    }

    /**
     * Get the single item price of the given row
     *
     * @param row
     * @return item price
     */
    public double getItemPrice(final HtmlElement item)
    {
        return priceStringToNumber(HPU.find().in(item).byCss(".pricing").asserted().last().getTextContent());
    }

    /**
     * Convert a string containing a price into a double number.<br>
     * Input might be:
     * <ul>
     * <li>$1,000.00</li>
     * <li>1.000,00€</li>
     * <li>1000</li>
     * <li>...</li>
     * </ul>
     *
     * @param priceString
     * @return converted price
     */
    public double priceStringToNumber(final String priceString)
    {
        // Strip currency.
        String cleanPrice = RegExUtils.getFirstMatch(priceString.trim(), PRICE_REGEXP);

        // Remove any whitespaces.
        cleanPrice = RegExUtils.removeAll(cleanPrice, "\\s");

        final double result;

        if (RegExUtils.isMatching(cleanPrice, "^\\d$"))
        {
            // It's plain number (1000)
            result = Double.parseDouble(cleanPrice);
        }
        else
        {
            // Remember if price has decimal part.
            final boolean hasDecimalSeparator = RegExUtils.isMatching(cleanPrice, "[\\.,]\\d{2}$");

            // Keep the digits only, remove all separators.
            cleanPrice = RegExUtils.removeAll(cleanPrice, ",");
            cleanPrice = RegExUtils.removeAll(cleanPrice, "\\.");

            // Parse to double.
            final double d = Double.parseDouble(cleanPrice);

            // Fix decimal if necessary.
            result = hasDecimalSeparator ? (d / 100) : d;
        }

        return result;
    }
}
