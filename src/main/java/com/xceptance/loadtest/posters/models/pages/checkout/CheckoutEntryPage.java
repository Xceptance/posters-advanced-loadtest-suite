package com.xceptance.loadtest.posters.models.pages.checkout;

import com.xceptance.loadtest.posters.models.components.checkout.GuestCheckoutCard;
import com.xceptance.loadtest.posters.models.components.checkout.ReturningCustomerCard;

public class CheckoutEntryPage extends CheckoutPages
{
    public static final CheckoutEntryPage instance = new CheckoutEntryPage();

    /**
     * This block indicates all elements accessible for easier direct use, remove these if this is
     * not true any longer, hence the compiler will show you what area will break... mostly...
     */
    public final GuestCheckoutCard guestCheckoutCard = GuestCheckoutCard.instance;
    public final ReturningCustomerCard returningCustomerCard = ReturningCustomerCard.instance;

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

        // add additional test here if needed
        validate(has(guestCheckoutCard, returningCustomerCard));
    }

    @Override
    public boolean is()
    {
        // do additional test here if needed and combine them with the super
        // result that uses has and hasNot
        return super.is() && matches(has(guestCheckoutCard, returningCustomerCard));
    }
}
