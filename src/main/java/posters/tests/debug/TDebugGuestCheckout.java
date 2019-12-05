package posters.tests.debug;

import com.xceptance.loadtest.api.actions.debug.DebugUrl;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.account.Logout;
import posters.actions.cart.AddToCart;
import posters.actions.cart.ShowMiniCart;
import posters.actions.cart.ViewCart;
import posters.actions.checkout.Checkout;
import posters.actions.checkout.CheckoutGuest;
import posters.actions.checkout.CheckoutPlaceOrder;
import posters.actions.checkout.CheckoutSaveMyInformation;
import posters.actions.checkout.CheckoutSelectShipping;
import posters.actions.checkout.CheckoutShippingAddress;
import posters.actions.checkout.CheckoutSubmitBilling;
import posters.actions.checkout.CheckoutSubmitShipping;
import posters.flows.ConfigureProductFlow;
import posters.flows.VisitFlow;

/**
 * Simple test to get all possible add to cart operations tested easily
 */
public class TDebugGuestCheckout extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Set only variations
        new DebugUrl("/s/MobileFirst/womens/clothing/outfits/mix-and-match.html?cgid=womens-outfits&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Set variations and standard product
        new DebugUrl("/s/MobileFirst/womens/clothing/outfits/random.html?cgid=womens-outfits&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Bundle
        new DebugUrl("/s/MobileFirst/electronics/gaming/sony-ps3-bundle.html?cgid=electronics-gaming&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Variation many attr
        new DebugUrl("/s/MobileFirst/scoop-neck-shell/25493587.html?cgid=womens-clothing-tops&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Variation one attr
        new DebugUrl("/s/MobileFirst/checked-silk-tie/25752235.html?cgid=mens-accessories-ties&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Option
        new DebugUrl("/s/MobileFirst/electronics/televisions/sony-kdl-40w4100.html?cgid=electronics-televisions&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // standard product
        new DebugUrl("/s/MobileFirst/electronics/gaming/lucasarts-star-wars-the-force-unleashed-psp.html?cgid=electronics-gaming&lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        new ViewCart().run();

        Context.get().data.attachAccount();

        new Checkout().run();

        new CheckoutGuest().run();

        new CheckoutShippingAddress().run();
        new CheckoutSelectShipping().run();
        new CheckoutSubmitShipping().run();

        new CheckoutSubmitBilling().run();

        new CheckoutPlaceOrder().run();

        new CheckoutSaveMyInformation().run();

        new Logout().run();
    }
}
