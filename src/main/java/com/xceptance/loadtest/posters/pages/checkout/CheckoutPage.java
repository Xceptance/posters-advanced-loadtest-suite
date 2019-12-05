package com.xceptance.loadtest.posters.pages.checkout;

import com.xceptance.loadtest.posters.pages.components.checkout.CartSummaryCard;
import com.xceptance.loadtest.posters.pages.components.checkout.NextButtons;
import com.xceptance.loadtest.posters.pages.components.checkout.OrderSummaryCard;
import com.xceptance.loadtest.posters.pages.components.checkout.PaymentCard;
import com.xceptance.loadtest.posters.pages.components.checkout.ShippingAddressCard;
import com.xceptance.loadtest.posters.pages.components.checkout.ShippingSummaryCard;

public class CheckoutPage extends CheckoutPages
{
    public static final CheckoutPage instance = new CheckoutPage();

    /**
     * This block indicates all elements accessible for easier direct use, remove these if this is
     * not true any longer, hence the compiler will show you what area will break... mostly...
     */
    public final ShippingAddressCard shippingAddressCard = ShippingAddressCard.instance;
    public final ShippingSummaryCard shippingSummaryCard = ShippingSummaryCard.instance;
    public final OrderSummaryCard orderSummaryCard = OrderSummaryCard.instance;
    public final CartSummaryCard cartSummaryCard = CartSummaryCard.instance;
    public final PaymentCard paymentCard = PaymentCard.instance;

    public final NextButtons nextButtons = NextButtons.instance;

    /**
     * Validates a common page. Checks for standard components and performs a
     * validateBasics() check.
     *
     * @throws Exception
     */
    @Override
    public void validate()
    {
        // no check for response code or content anymore because that is transport
        // as well as html end tag. The html parser will probably add that to the tree
        // if unbalanced, so taking the tree, turning it into HTML does not really work
        // rather go for the response stream instead when we know that we should have
        // a full page
        super.validate();

        // the shipping stuff is there but sometimes hidden, so we can check for it all the time
        validate(has(orderSummaryCard, cartSummaryCard, shippingSummaryCard, shippingAddressCard));
    }

    @Override
    public boolean is()
    {
        // do additional test here if needed and combine them with the super
        // result that uses has and hasNot
        return super.is() && matches(has(orderSummaryCard, cartSummaryCard, shippingSummaryCard, shippingAddressCard));
    }
}
