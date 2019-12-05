package posters.pages.components.pdp;

import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;

public class ProductDetailPlaceHolder extends ProductDetail
{
    public static final ProductDetailPlaceHolder instance = new ProductDetailPlaceHolder();

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
        return HPU.find().in(in).byCss(".container.product-detail[data-pid]");
    }

    @Override
    public boolean exists(final HtmlElement in)
    {
        return locate(in).exists();
    }

    @Override
    public boolean isSetItem(final HtmlElement element)
    {
        return false;
    }

    @Override
    public HtmlElement render(final String response, final HtmlElement productDetail)
    {
        return null;
    }

    @Override
    public List<HtmlElement> getConfigurableItems()
    {
        return Collections.emptyList();
    }
}
