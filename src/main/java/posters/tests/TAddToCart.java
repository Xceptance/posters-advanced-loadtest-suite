package posters.tests;

import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.cart.ViewCart;
import posters.flows.AddToCartFlow;
import posters.flows.VisitFlow;
import posters.pages.cart.CartPage;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class TAddToCart extends LoadTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();//

        // Fill cart
        new AddToCartFlow(Context.configuration().addToCartCount.value).run();

        // View the cart if not just done
        if (!CartPage.instance.is())
        {
            new ViewCart().run();
        }
    }
}
