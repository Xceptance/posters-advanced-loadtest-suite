package posters.pages.components.pdp;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.DOMUtils;

public class ProductDetailSet extends ProductDetail
{
    public static final String TYPE_LOCATOR = ".product-set-detail";

    public static final String ITEM_LOCATOR = ".set-item";

    public static final ProductDetailSet instance = new ProductDetailSet();

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
        return HPU.find().in(in).byCss(TYPE_LOCATOR + "[data-pid]");
    }

    @Override
    public boolean exists(final HtmlElement in)
    {
        return locate(in).exists();
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        // ok, set is different, because it can have many different subitems
        return locate().byCss(ProductDetail.TYPE_LOCATOR + ITEM_LOCATOR + "[data-pid]").all();
    }

    /**
     * Check me
     */
    @Override
    public boolean isSetItem(final HtmlElement element)
    {
        return false;
    }

    /**
     * Are we a set?
     */
    public boolean isSet(final HtmlElement element)
    {
        return DOMUtils.hasClassLocators(element, ProductDetail.TYPE_LOCATOR, ProductDetailSet.TYPE_LOCATOR)
                        && element.hasAttribute("data-pid")
                        && getSetItems(element).exists();
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement item)
    {
        // ok, find the type of the element to call the correct renderer of variations, options
        // or standard products, sets in sets and bundles in sets are not possible right now
        if (ProductDetailOption.instance.isSetItem(item))
        {
            return ProductDetailOption.instance.render(response, item);
        }
        else if (ProductDetailVariation.instance.isSetItem(item))
        {
            return ProductDetailVariation.instance.render(response, item);
        }
        else if (ProductDetailStandard.instance.isSetItem(item))
        {
            return ProductDetailStandard.instance.render(response, item);
        }

        Assert.fail("Incorrect type for rendering of a set item");
        return item;
    }

    @Override
    public String getAddToCartUrl(final HtmlElement productDetail)
    {
        // bundle footer is a bug in SG
        return HPU.find().in(productDetail).byCss(ProductDetailBundle.FOOTER_LOCATOR + " .add-to-cart-url").single().getAttribute("value");
    }

    @Override
    public int getQuantity(final HtmlElement item)
    {
        if (ProductDetailOption.instance.isSetItem(item))
        {
            return ProductDetailOption.instance.getQuantity(item);
        }
        else if (ProductDetailVariation.instance.isSetItem(item))
        {
            return ProductDetailVariation.instance.getQuantity(item);
        }
        else if (ProductDetailStandard.instance.isSetItem(item))
        {
            return ProductDetailStandard.instance.getQuantity(item);
        }

        Assert.fail("Incorrect type for getQuantity() of a set item");
        return -1;
    }

    @Override
    public String getPid(final HtmlElement item)
    {
        if (ProductDetailOption.instance.isSetItem(item))
        {
            return ProductDetailOption.instance.getPid(item);
        }
        else if (ProductDetailVariation.instance.isSetItem(item))
        {
            return ProductDetailVariation.instance.getPid(item);
        }
        else if (ProductDetailStandard.instance.isSetItem(item))
        {
            return ProductDetailStandard.instance.getPid(item);
        }
        else if (ProductDetailSet.instance.isSet(item))
        {
            final StringBuilder pidBuilder = new StringBuilder(64);
            HPU.find().in(item).byCss(".product-id").all().forEach(currentItem -> pidBuilder.append(currentItem.getTextContent()));

            final String pid = pidBuilder.toString();
            return StringUtils.isBlank(pid) ? item.getAttribute("data-pid") : pid;
        }

        Assert.fail("Incorrect type for getPid(item) of a set item");
        return null;
    }

    public LookUpResult getSetItems(final HtmlElement set)
    {
        return HPU.find().in(set).byCss(ProductDetailSet.ITEM_LOCATOR);
    }

    public LookUpResult getGlobalAddToCartButton()
    {
        return Page.find().byCss("button.add-to-cart-global");
    }

    public void updateCartButtonState()
    {
        // that includes availability too, not clear yet how MFRA sets that
        boolean isOrderable = true;

        // get all product details
        for (final HtmlElement item : getSetItems(locate().first()).all())
        {
            if (ProductDetailOption.instance.isSetItem(item))
            {
                isOrderable = ProductDetailOption.instance.isOrderable(item) && ProductDetailOption.instance.isAvailable(item);
            }
            else if (ProductDetailVariation.instance.isSetItem(item))
            {
                isOrderable = ProductDetailVariation.instance.isOrderable(item) && ProductDetailVariation.instance.isAvailable(item);
            }
            else if (ProductDetailStandard.instance.isSetItem(item))
            {
                isOrderable = ProductDetailStandard.instance.isOrderable(item) && ProductDetailStandard.instance.isAvailable(item);
            }
            else
            {
                Assert.fail("Unidenfifiable item in set found");
            }

            if (!isOrderable)
            {
                break;
            }
        }

        // set state of the set global button
        final HtmlElement button = getGlobalAddToCartButton().asserted().first();

        if (!isOrderable)
        {
            // disable it
            button.setAttribute("disabled", "disabled");
        }
        else
        {
            // enable it
            button.removeAttribute("disabled");
        }
    }

    @Override
    public boolean isOrderable(final HtmlElement item)
    {
        if (ProductDetailOption.instance.isSetItem(item))
        {
            return ProductDetailOption.instance.isOrderable(item);
        }
        else if (ProductDetailVariation.instance.isSetItem(item))
        {
            return ProductDetailVariation.instance.isOrderable(item);
        }
        else if (ProductDetailStandard.instance.isSetItem(item))
        {
            return ProductDetailStandard.instance.isOrderable(item);
        }
        else if (ProductDetailSet.instance.isSet(item))
        {
            return ProductDetailSet.instance.isOrderable();
        }
        else
        {
            Assert.fail("Unidenfifiable item in set found");
        }

        return false;
    }

    @Override
    public boolean isAvailable(final HtmlElement item)
    {
        if (ProductDetailOption.instance.isSetItem(item))
        {
            return ProductDetailOption.instance.isAvailable(item);
        }
        else if (ProductDetailVariation.instance.isSetItem(item))
        {
            return ProductDetailVariation.instance.isAvailable(item);
        }
        else if (ProductDetailStandard.instance.isSetItem(item))
        {
            return ProductDetailStandard.instance.isAvailable(item);
        }
        else if (ProductDetailSet.instance.isSet(item))
        {
            // does not exist yet for Sets
            return true;
        }
        else
        {
            Assert.fail("Unidenfifiable item in set found");
        }

        return false;
    }

    public boolean isOrderable()
    {
        // when the cart button is enabled, we give that a go
        final HtmlElement button = getGlobalAddToCartButton().asserted().single();

        return !button.hasAttribute("disabled");
    }

    public boolean isAvailable()
    {
        // we don't have that, hence all true for the entire set
        return true;
    }
}
