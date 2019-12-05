package posters.pages.components.pdp;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlMapper;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.DOMUtils;

import posters.jsondata.ProductJSON;

public class ProductDetailStandard extends ProductDetail
{
    public static final ProductDetailStandard instance = new ProductDetailStandard();

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

    /**
     * Locating a standard product is expensive
     *
     * @param in
     *            where to search for
     * @return the result or an empty result
     */
    @Override
    public LookUpResult locate(final HtmlElement in)
    {
        // preserve the start
        final LookUpResult mainLookUpResult = HPU.find().in(in).byCss(".container" + ProductDetail.TYPE_LOCATOR + "[data-pid]");

        if (mainLookUpResult.exists())
        {
            final HtmlElement main = mainLookUpResult.first();

            // non wanted sub-classes
            if (hasUnwantedSubClasses(main))
            {
                return LookUpResult.DOESNOTEXIST;
            }
            else
            {
                return mainLookUpResult;
            }
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
        return DOMUtils.hasClassLocators(element, ".container", ProductDetail.TYPE_LOCATOR, ProductDetailSet.ITEM_LOCATOR)
                        && element.hasAttribute("data-pid")
                        && !hasUnwantedSubClasses(element);
    }

    private boolean hasUnwantedSubClasses(final HtmlElement main)
    {
        // ok, we should not have .set-item, attribute, bundle-item or option
        if (HPU.find().in(main).byCss(".attribute").exists())
        {
            return true;
        }
        if (HPU.find().in(main).byCss(ProductDetailBundle.ITEM_LOCATOR).exists())
        {
            return true;
        }
        if (HPU.find().in(main).byCss(ProductDetailSet.ITEM_LOCATOR).exists())
        {
            return true;
        }
        if (HPU.find().in(main).byCss(ProductDetailOption.ITEM_LOCATOR).exists())
        {
            return true;
        }

        return false;
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        // standard product is easy, we have only one
        // the lookup is a little more expensive, because we have to
        // exclude so many other things
        return locate().all();
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement item)
    {
        // ok, we have to rescue the add to cart url
        final String addToCartUrl = getAddToCartUrl(item);

        final HtmlRenderer renderer = Page.renderHtml().json(response, ProductJSON.class, "pv");
        renderer.template("/templates/pdp/product-detail-standard.ftlh");

        final HtmlMapper mapper = Page.mapHtml().html(renderer).in(item)
                        .byCSS(".simple-quantity")
                        .byCSS(".product-availability")
                        .byCSS(".promotions")
                        .byCSS(".prices-add-to-cart-actions, .prices");

        // do it
        mapper.map();

        // put the add to cart url back, because this data is not in our json
        HPU.find().in(item).byCss("input.add-to-cart-url").asserted().first().setAttribute("value", addToCartUrl);

        return item;
    }
}
