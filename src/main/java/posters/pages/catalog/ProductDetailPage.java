package posters.pages.catalog;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

import posters.pages.components.pdp.ProductDetail;
import posters.pages.components.pdp.ProductDetailBundle;
import posters.pages.components.pdp.ProductDetailOption;
import posters.pages.components.pdp.ProductDetailPlaceHolder;
import posters.pages.components.pdp.ProductDetailSet;
import posters.pages.components.pdp.ProductDetailStandard;
import posters.pages.components.pdp.ProductDetailVariation;
import posters.pages.components.qv.Quickview;
import posters.pages.general.GeneralPages;

public class ProductDetailPage<T extends ProductDetail> extends GeneralPages
{
    public final static ProductDetailPage<ProductDetailPlaceHolder> instance = new ProductDetailPage<>(ProductDetailPlaceHolder.instance);
    public final Quickview quickview = Quickview.instance;

    public final T productDetail;

    /**
     * Don't let the world do that
     */
    private ProductDetailPage(final T productDetail)
    {
        this.productDetail = productDetail;
    }

    @Override
    public void validate()
    {
        super.validate();

        validate(has(productDetail), hasNot(quickview));
    }

    @Override
    public boolean is()
    {
        return super.is()
                        && matches(has(productDetail), hasNot(quickview));
    }

    public static ProductDetailPage<ProductDetailBundle> getProductDetailBundlePage()
    {
        return new ProductDetailPage<>(ProductDetailBundle.instance);
    }

    public static ProductDetailPage<ProductDetailVariation> getProductDetailVariationPage()
    {
        return new ProductDetailPage<>(ProductDetailVariation.instance);
    }

    public static ProductDetailPage<ProductDetailOption> getProductDetailOptionPage()
    {
        return new ProductDetailPage<>(ProductDetailOption.instance);
    }

    public static ProductDetailPage<ProductDetailSet> getProductDetailSetPage()
    {
        return new ProductDetailPage<>(ProductDetailSet.instance);
    }

    public static ProductDetailPage<ProductDetailStandard> getProductDetailStandardPage()
    {
        return new ProductDetailPage<>(ProductDetailStandard.instance);
    }

    public static ProductDetailPage<? extends ProductDetail> identify()
    {
        if (ProductDetailVariation.instance.exists())
        {
            return getProductDetailVariationPage();
        }
        else if (ProductDetailStandard.instance.exists())
        {
            return getProductDetailStandardPage();
        }
        else if (ProductDetailBundle.instance.exists())
        {
            return getProductDetailBundlePage();
        }
        else if (ProductDetailSet.instance.exists())
        {
            return getProductDetailSetPage();
        }
        else if (ProductDetailOption.instance.exists())
        {
            return getProductDetailOptionPage();
        }
        else
        {
            Assert.fail("Page not identifiable as any product detail page");
        }

        // we don't hit that here
        return null;
    }

    @SuppressWarnings("unchecked")
    public Optional<ProductDetailPage<ProductDetailVariation>> getIfPresentVariationPage()
    {
        if (productDetail instanceof ProductDetailVariation)
        {
            return Optional.of((ProductDetailPage<ProductDetailVariation>) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<ProductDetailPage<ProductDetailStandard>> getIfPresentStandardPage()
    {
        if (productDetail instanceof ProductDetailStandard)
        {
            return Optional.of((ProductDetailPage<ProductDetailStandard>) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<ProductDetailPage<ProductDetailBundle>> getIfPresentBundlePage()
    {
        if (productDetail instanceof ProductDetailBundle)
        {
            return Optional.of((ProductDetailPage<ProductDetailBundle>) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<ProductDetailPage<ProductDetailOption>> getIfPresentOptionPage()
    {
        if (productDetail instanceof ProductDetailOption)
        {
            return Optional.of((ProductDetailPage<ProductDetailOption>) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<ProductDetailPage<ProductDetailSet>> getIfPresentSetPage()
    {
        if (productDetail instanceof ProductDetailSet)
        {
            return Optional.of((ProductDetailPage<ProductDetailSet>) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    public List<HtmlElement> getConfigurableItems()
    {
        return productDetail.getConfigurableItems();
    }

    public boolean isOrderable()
    {
        return productDetail.isOrderable(productDetail.locate().asserted().first());
    }

    public boolean isAvailable()
    {
        return productDetail.isAvailable(productDetail.locate().asserted().first());
    }
}
