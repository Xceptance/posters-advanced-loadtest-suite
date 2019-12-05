package posters.tests.debug;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.debug.DebugUrl;
import com.xceptance.loadtest.api.tests.LoadTestCase;

import posters.actions.cart.AddToCart;
import posters.actions.cart.ShowMiniCart;
import posters.actions.cart.ViewCart;
import posters.flows.CartCleanUpFlow;
import posters.flows.CartRemoveAllItemsFlow;
import posters.flows.ConfigureProductFlow;
import posters.flows.VisitFlow;
import posters.pages.cart.CartPage;


/**
 * Simple test to get all possible add to cart operations tested easily
 */
public class TCartFillAndClean extends LoadTestCase
{
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Bundle with preselected variations
        new DebugUrl("/s/RefArch/womens/jewelry/earrings/womens-jewelry-bundleM.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        // Set variations and standard product
        new DebugUrl("/s/RefArch/womens/clothing/outfits/in-store-setM.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Set only variations
        new DebugUrl("/s/RefArch/womens/clothing/outfits/Spring-look-2M.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Bundle
        new DebugUrl("/s/RefArch/electronics/gaming/game consoles/sony-ps3-bundleM.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Variation many attr
        new DebugUrl("/s/RefArch/roll-up-cargo-pant/25564782M.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Variation one attr
        new DebugUrl("/s/RefArch/checked-silk-tie/25752235M.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // Option
        new DebugUrl("/s/RefArch/electronics/televisions/projection/samsung-hl61a650M.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();

        // standard product
        new DebugUrl("/s/RefArch/electronics/gaming/games/namco-we-ski-wiiM.html?lang=en_US").run();
        new ConfigureProductFlow().run();
        new AddToCart().run();

        new ShowMiniCart().run();
        new ViewCart().run();

        // anything to remove
        new CartCleanUpFlow().run();

        // remove all
        new CartRemoveAllItemsFlow().run();

        Assert.assertTrue("Cart not empty", CartPage.instance.cartEmpty.exists());
        Assert.assertTrue("Mini cart not empty", CartPage.instance.miniCart.isEmpty());
    }
}
