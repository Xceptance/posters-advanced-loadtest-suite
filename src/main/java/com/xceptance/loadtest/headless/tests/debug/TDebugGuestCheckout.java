package com.xceptance.loadtest.headless.tests.debug;

import com.xceptance.loadtest.api.actions.debug.DebugUrl;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.actions.account.Logout;
import com.xceptance.loadtest.headless.actions.cart.AddToCart;
import com.xceptance.loadtest.headless.actions.cart.ShowMiniCart;
import com.xceptance.loadtest.headless.actions.cart.ViewCart;
import com.xceptance.loadtest.headless.actions.checkout.Checkout;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutGuest;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutPlaceOrder;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSaveMyInformation;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSelectShipping;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutShippingAddress;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitBilling;
import com.xceptance.loadtest.headless.actions.checkout.CheckoutSubmitShipping;
import com.xceptance.loadtest.headless.flows.ConfigureProductFlow;
import com.xceptance.loadtest.headless.flows.VisitFlow;

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
