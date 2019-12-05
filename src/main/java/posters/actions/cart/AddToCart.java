package posters.actions.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.google.gson.Gson;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Format;
import com.xceptance.loadtest.api.util.HttpRequest;

import posters.jsondata.AddToCartJSON;
import posters.pages.catalog.ProductDetailPage;
import posters.pages.components.general.MiniCart;
import posters.pages.components.pdp.ProductDetail;
import posters.pages.components.pdp.ProductDetailBundle;
import posters.pages.components.pdp.ProductDetailOption;
import posters.pages.components.pdp.ProductDetailSet;
import posters.pages.general.GeneralPages;

/**
 * Adds the currently shown product or more specifically one of its variations to the cart.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class AddToCart extends AjaxAction<AddToCart>
{
    private AddToCartJSON cart;

    private int previousCartQuantity;

    /**
     * The page we are working on
     */
    private final ProductDetailPage<? extends ProductDetail> pdp;

    /**
     * Constructor
     */
    public AddToCart()
    {
        super();

        // ok, we have to know if this is quickview or PDP first, the later templating is very
        // different plus a couple of locators, yeah... have to do that again... pretty expensive
        // but this change has to wait until we have 2.0 of this suite... let's get it flying first
        pdp = ProductDetailPage.identify();

        // set the timername already plus one... might happen that we are not doing it
        if (Context.configuration().reportCartBySize)
        {
            // start simply with the qty, later find a way to capture and store the state better
            setTimerName(Format.timerName(getTimerName(), Integer.valueOf(Context.get().data.totalAddToCartCount + 1)));
        }
    }

    @Override
    public void precheck()
    {
        // current quantity
        previousCartQuantity = GeneralPages.instance.miniCart.getQuantity();

        if (pdp.isOrderable() == false || pdp.isAvailable() == false)
        {
            Assert.fail("Product not available or orderable");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        if (pdp.getIfPresentSetPage().isPresent())
        {
            addSetToCart(pdp.getIfPresentSetPage().get());
        }
        else if (pdp.getIfPresentBundlePage().isPresent())
        {
            addBundleToCart(pdp.getIfPresentBundlePage().get());
        }
        else
        {
            addToCart(pdp);
        }
    }

    private void addSetToCart(final ProductDetailPage<ProductDetailSet> pdp)
    {
        // get us the first product details, if there is more than one product on the page, we
        // assume a set,
        // if not, we don't care right now, that has to be done differently
        final HtmlElement productSet = pdp.productDetail.locate().asserted().single();

        // pid
        final String pid = pdp.productDetail.getPid(productSet);

        // qty, default 1, nothing else possible for an add all
        final int qty = 1;

        // url
        final String url = pdp.productDetail.getAddToCartUrl(productSet);

        // get us all product details of that bundle
        final List<Set> setProducts = new ArrayList<>(6);
        final List<HtmlElement> setItems = pdp.productDetail.getSetItems(productSet).asserted().all();

        for (final HtmlElement setItem : setItems)
        {
            final String productId = pdp.productDetail.getPid(setItem);
            final int quantity = pdp.productDetail.getQuantity(setItem);

            setProducts.add(new Set(productId, quantity));
        }

        addToCartCall(url, pid, qty, null, Collections.emptyList(), setProducts);
    }

    private void addBundleToCart(final ProductDetailPage<ProductDetailBundle> pdp) throws Exception
    {
        // we care only about the first one right now, if we have multple bundles, we have
        // to rethink this
        final HtmlElement bundle = pdp.productDetail.locate().asserted().first();

        // pid
        final String pid = pdp.productDetail.getPid(bundle);

        // qty
        final int qty = pdp.productDetail.getQuantity(bundle);

        // url
        final String url = pdp.productDetail.getAddToCartUrl(bundle);

        // get us all product details of that bundle
        final List<ChildProduct> childProducts = new ArrayList<>(6);
        final List<HtmlElement> bundledItems = pdp.productDetail.getBundledItems(bundle).all();

        for (final HtmlElement bundleItem : bundledItems)
        {
            final String productId = pdp.productDetail.bundleItem.getPid(bundleItem);
            final int quantity = pdp.productDetail.bundleItem.getQuantity(bundleItem);

            childProducts.add(new ChildProduct(productId, String.valueOf(quantity)));
        }

        addToCartCall(url, pid, qty, childProducts, null, null);
    }

    private void addToCart(final ProductDetailPage<? extends ProductDetail> pdp) throws Exception
    {
        // get us the item, should only be one... if not, we missed the identification of a set or
        // bundle
        final List<HtmlElement> items = pdp.getConfigurableItems();
        Assert.assertEquals(1, items.size());

        final HtmlElement item = items.get(0);

        // pid
        final String pid = pdp.productDetail.getPid(item);

        // qty
        final int qty = pdp.productDetail.getQuantity(item);

        // url
        final String url = pdp.productDetail.getAddToCartUrl(item);

        List<Option> options;
        if (pdp.getIfPresentOptionPage().isPresent())
        {
            final ProductDetailPage<ProductDetailOption> optionPdp = pdp.getIfPresentOptionPage().get();

            options = new ArrayList<>(5);

            // any options?
            final List<HtmlElement> optionsElements = optionPdp.productDetail.getOptionAttributes(item).all();

            for (final HtmlElement optionElement : optionsElements)
            {
                final String optionId = optionPdp.productDetail.getOptionId(optionElement);
                final String selectedOption = optionPdp.productDetail.getSelectedOptionValueId(optionElement);

                options.add(new Option(optionId, selectedOption));
            }
        }
        else
        {
            options = Collections.emptyList();
        }

        addToCartCall(url, pid, qty, null, options, null);
    }

    private void addToCartCall(
                    final String url,
                    final String pid,
                    final int qty,
                    final List<ChildProduct> childProducts,
                    final List<Option> options,
                    final List<Set> sets)
    {
        try
        {
            final HttpRequest httpRequest = new HttpRequest().XHR().url(url).POST();

            if (childProducts != null)
            {
                final Gson gson = Context.getGson();
                final String childProductJson = gson.toJson(childProducts);

                httpRequest.postParam("childProducts", childProductJson);
            }

            if (options != null)
            {
                final Gson gson = Context.getGson();
                final String optionsJson = gson.toJson(options);

                httpRequest.postParam("options", optionsJson);
            }

            if (sets != null)
            {
                final Gson gson = Context.getGson();
                final String setsJson = gson.toJson(sets);

                httpRequest.postParam("pidsObj", setsJson);
            }

            httpRequest.postParam("pid", pid).postParam("quantity", String.valueOf(qty));

            final WebResponse response = httpRequest.assertStatusCode(200)
                            .assertContent("Nothing came back from Add-to-Cart.", true, HttpRequest.NOT_BLANK)
                            .fire();

            // update mini cart state
            cart = updateMiniCart(response.getContentAsString());
        }
        catch (final Exception e)
        {
            // turn this into a runtime exception to make the code easier without less declaration
            throw new RuntimeException(e);
        }
    }

    private AddToCartJSON updateMiniCart(final String responseJson)
    {
        final Gson gson = Context.getGson();
        final AddToCartJSON cart = gson.fromJson(responseJson, AddToCartJSON.class);

        GeneralPages.instance.miniCart.updateQuantity(cart.quantityTotal, cart.cart.items.size());

        return cart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // error while adding
        Assert.assertFalse("Add to cart failed with message: " + cart.message, cart.error);

        // validate the response cart in addition to what we might have done already
        final int currentCartQuantity = MiniCart.instance.getQuantity();
        Assert.assertTrue("Cart quantity did not change", currentCartQuantity > previousCartQuantity);

        GeneralPages.instance.miniCart.exists();

        // increase total add to cart count if successful
        Context.get().data.totalAddToCartCount++;
    }

    class ChildProduct
    {
        public final String pid;
        public final String quantity;

        public ChildProduct(final String pid, final String quantity)
        {
            this.pid = pid;
            this.quantity = quantity;
        }
    }

    class Option
    {
        public final String optionId;
        public final String selectedValueId;

        public Option(final String optionId, final String selecedValueId)
        {
            this.optionId = optionId;
            this.selectedValueId = selecedValueId;
        }
    }

    class Set
    {
        public final String pid;
        public final String qty;
        public final String options;

        public Set(final String pid, final int qty)
        {
            this.pid = pid;
            this.qty = String.valueOf(qty);

            // that is probably also wrong, but as normal area, it does not work
            // when we have an option in a set for the first time, that might have to be changed
            this.options = "[]";
        }
    }
}
