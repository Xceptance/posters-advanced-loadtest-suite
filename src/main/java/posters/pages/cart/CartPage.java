package posters.pages.cart;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;

import posters.pages.components.cart.CartBanner;
import posters.pages.components.cart.CartEmpty;
import posters.pages.components.cart.CartError;
import posters.pages.components.cart.CartItemCount;
import posters.pages.components.cart.CartPromo;
import posters.pages.components.cart.CartTable;
import posters.pages.general.GeneralPages;

public class CartPage extends GeneralPages
{
    public static final CartPage instance = new CartPage();

    public final CartBanner banner = CartBanner.instance;
    public final CartTable cartTable = CartTable.instance;
    public final CartEmpty cartEmpty = CartEmpty.instance;
    public final CartItemCount cartItemCount = CartItemCount.instance;
    public final CartPromo cartPromo = CartPromo.instance;
    public final CartError cartError = CartError.instance;

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

        // add additional test here if needed
        super.validate();

        // cart can have a cart table or be empty, not both, so you hasOneOf
        validate(has(banner, cartItemCount), hasOneOf(cartEmpty, cartTable));
    }

    @Override
    public boolean is()
    {
        // cart can have a cart table or be empty, not both, so you hasOneOf
        return super.is() &&
                        matches(has(banner, cartItemCount), hasOneOf(cartEmpty, cartTable));
    }

    /**
     * Return the general checkout button
     * @return the result of the lookup
     */
    public LookUpResult getCheckoutButton()
    {
        return HPU.find().in(cartTable.locate().first()).byCss(".checkout-continue a.checkout-btn");
    }


    /**
     * Determine if this cart is in a state that can be checked out
     */
    public boolean isOrderable()
    {
        // do we have the error message?
        if (cartError.hasMessage())
        {
            return false;
        }

        // in case we do not have a message, check the availability
        final List<HtmlElement> unavailableItems = cartTable.getUnavailableLineItems();
        if (unavailableItems.isEmpty() == false)
        {
            return false;
        }

        // check the prices
        // currently not implemented

        return true;
    }
}
