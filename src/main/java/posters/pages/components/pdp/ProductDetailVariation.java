package posters.pages.components.pdp;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.render.HtmlRenderer;
import com.xceptance.loadtest.api.util.DOMUtils;

import posters.jsondata.ProductJSON;

public class ProductDetailVariation extends ProductDetail
{
    public final static ProductDetailVariation instance = new ProductDetailVariation();

    @Override
    public LookUpResult locate()
    {
        return locate(Page.getBody());
    }

    @Override
    public LookUpResult locate(final HtmlElement in)
    {
        // exclude sets, that has to be done because variations are not correctly marked
        final LookUpResult main = HPU.find().in(in).byCss(ProductDetail.TYPE_LOCATOR + "[data-pid]"
                                                            + ":not(" + ProductDetailSet.TYPE_LOCATOR + "):not(" + ProductDetailSet.ITEM_LOCATOR + ")"
                                                            + ":not(" + ProductDetailBundle.ITEM_LOCATOR + ")").hasNotCss(ProductDetailBundle.TYPE_LOCATOR);

        if (main.byCss(".attribute").exists())
        {
            return main;
        }
        else
        {
            return LookUpResult.DOESNOTEXIST;
        }
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
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
        return DOMUtils.hasClassLocators(element, ProductDetail.TYPE_LOCATOR, ProductDetailSet.ITEM_LOCATOR)
                        && element.hasAttribute("data-pid")
                        && HPU.find().in(element).byCss(".attribute").exists();
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        return locate().all();
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement item)
    {
        // ok, we have to rescue the add to cart url
        final String addToCartUrl = getAddToCartUrl(item);

        final HtmlRenderer renderer = Page.renderHtml()
                        .json(response, ProductJSON.class, "pv")
                        .template("/templates/pdp/product-detail-variation.ftlh");

        // Replaces all elements in the page (target = right side) with what we just rendered with
        // our template (source = left side).
        // When there is not left or right, both are the same!!!
        // The "xc-" part is just to make it easier for us to grab the data, is not used by the web
        // page itself.
        Page.mapHtml().html(renderer).in(item)
                        // the SFRA path is nuts
                        .byCSS(".xc-renderedVariations", ".row.justify-content-center > .col-md-10.col-12, .xc-renderedVariations")
                        .byCSS(".product-availability")
                        .byCSS(".promotions")
                        .byCSS(".product-number")
                        .byCSS(".prices-add-to-cart-actions, .prices")
                        .map();

        // put the add to cart url back, because this data is not in our json
        HPU.find().in(item).byCss("input.add-to-cart-url").asserted().first().setAttribute("value", addToCartUrl);

        // update PID
        updatePid(item, renderer.getJson(ProductJSON.class, "pv").product.id);

        return item;
    }

    /**
     * Get the variation update url
     *
     * @param element
     *            the element to get the url from
     * @return the url
     */
    public String getVariationUpdateUrl(final HtmlElement element)
    {
        // do we have an select?
        if (element.getNodeName().equals("option"))
        {
            // data-url first
            String url = element.getAttribute("data-url");
            if (url == null || url.length() == 0)
            {
                url = element.getAttribute("value");
            }
            return url;
        }
        else if (element.getNodeName().equals("a"))
        {
            return element.getAttribute("href");
        }
        else
        {
            // color like aka the surrounding element holds the data
            return ((DomElement) element.getParentNode()).getAttribute("href");
        }
    }

    /**
     * A list of all attributes or an empty list
     *
     * @return a list of all attribute elements in the DOM or empty if none
     */
    public LookUpResult getVariationAttributes(final HtmlElement item)
    {
        return HPU.find().in(item).byCss("div[data-attr]");
    }

    /**
     * A list of all attributes or an empty list
     *
     * @param productDetail
     *            the area to search in
     * @param name
     *            the attribute name to search for
     * @return returns the full element found
     */
    public LookUpResult getVariationAttributeByName(final HtmlElement item, final String name)
    {
        return HPU.find().in(item).byCss("div[data-attr='" + name + "']");
    }

    /**
     * Get us all selectable but unselected attributes
     *
     * @param attribute
     * @return
     */
    public LookUpResult getSelectableButUnselectedVariationAttributes(final HtmlElement item)
    {
        // .selected is not part of the size of instance, but we will render that in later on
        return HPU.find().in(item).byCss(".attribute .selectable:not(.selected):not([disabled]), .attribute option[data-attr-value]:not(.selected):not([disabled])");
    }

    /**
     * Get us all truly selected attributes, that is for add to cart
     *
     * @param attribute
     * @return
     */
    public LookUpResult getSelectedAttribute(final HtmlElement item)
    {
        return HPU.find().in(item).byCss(".selected");
    }
}
