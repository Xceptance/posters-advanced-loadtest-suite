package posters.pages.components.pdp;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.DataUtils;

public class ProductDetailBundle extends ProductDetail
{
    public static final String TYPE_LOCATOR = ".product-bundle";

    public static final String ITEM_LOCATOR = ".bundle-item";

    public static final String FOOTER_LOCATOR = ".bundle-footer";

    public static final ProductDetailBundle instance = new ProductDetailBundle();
    public final ProductDetailBundleItem bundleItem = ProductDetailBundleItem.instance;

    @Override
    public LookUpResult locate()
    {
        return locate(Page.getBody());
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    @Override
    public LookUpResult locate(final HtmlElement in)
    {
        final LookUpResult main = HPU.find().in(in).byCss(".container" + ProductDetail.TYPE_LOCATOR + "[data-pid]:not(" + ITEM_LOCATOR + ")");

        if (main.byCss(TYPE_LOCATOR).exists())
        {
            return main;
        }
        else
        {
            return LookUpResult.DOESNOTEXIST;
        }
    }

    @Override
    public boolean exists(final HtmlElement in)
    {
        return locate(in).exists();
    }

    /**
     * Check me
     */
    @Override
    public boolean isSetItem(final HtmlElement element)
    {
        return false;
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        // we cannot configure anything right now in a bundle, except the outer quantity
        return locate().all();
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement item)
    {
        return item;
    }

    @Override
    public LookUpResult getUnselectedQuantities(final HtmlElement bundle)
    {
        return HPU.find().in(bundle).byCss(FOOTER_LOCATOR + " .quantity-select option:not([selected])");
    }

    @Override
    public LookUpResult getSelectedQuantity(final HtmlElement bundle)
    {
        return HPU.find().in(bundle).byCss(FOOTER_LOCATOR + " .quantity-select option[selected]");
    }

    @Override
    public int getQuantity(final HtmlElement bundle)
    {
        // bundles miss a set selected by default, so if we have not done a configure of the
        // quantity, we will fail here
        final HtmlElement element = getSelectedQuantity(bundle).asserted("Quantity of bundle not set").first();
        final String value = element.getAttribute("value");
        return DataUtils.toInt(value);
    }

    public LookUpResult getBundledItems(final HtmlElement bundle)
    {
        return bundleItem.locate(bundle);
    }

    @Override
    public boolean isOrderable(final HtmlElement bundle)
    {
        final HtmlElement availability = HPU.find()
                        .in(bundle).byCss(".global-availability[data-ready-to-order]")
                        .asserted("Ready to order information not available")
                        .single();
        final String attribute = availability.getAttribute("data-ready-to-order");

        return Boolean.valueOf(attribute);
    }

    @Override
    public boolean isAvailable(final HtmlElement bundle)
    {
        // not implemented in MFRA, but have to use the cart state instead, filed a defect ... darn
        final HtmlElement button = HPU.find()
                        .in(bundle).byCss(".prices-add-to-cart-actions button.add-to-cart-global").asserted("Cannot find add to cart button").single();

        // return the negated result... if we have disabled, it is not available
        return !button.hasAttribute("disabled");
    }
}
