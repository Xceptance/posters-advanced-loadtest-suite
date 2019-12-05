package posters.pages.components.plp;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;

public class ProductTile implements Component
{
    public static final ProductTile instance = new ProductTile();

    @Override
    public LookUpResult locate()
    {
        return ProductGrid.instance.locate().byCss(".thumbnails div.thumbnail");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Get us a quick view link of a product
     *
     * @param in
     *            where to look for
     * @return the link
     */
    public static LookUpResult getQuickviewLink(final HtmlElement in)
    {
        return HPU.find().in(in).byCss("a.quickview");
    }

    /**
     * Get us all quick view links
     *
     * @param result
     *            where to look for
     * @return the links
     */
    public static LookUpResult getQuickviewLinks(final LookUpResult result)
    {
        return result.byCss("a.quickview");
    }

    /**
     * Returns all links to products based on any input which is likely to be just a set of tiles
     *
     * @param result
     *            a list of tiles
     * @return all product links
     */
    public static LookUpResult getPDPLinks(final LookUpResult result)
    {
        return result.byCss("a[href]");
    }

    public static LookUpResult getPDPLink(final HtmlElement in)
    {
        // get us the first a of the image
        return HPU.find().in(in).byCss(".tile-body .pdp-link a.link");
    }
}
