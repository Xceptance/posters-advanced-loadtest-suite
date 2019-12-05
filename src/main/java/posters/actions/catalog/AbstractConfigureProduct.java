package posters.actions.catalog;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.actions.AjaxAction;
import com.xceptance.loadtest.api.util.HttpRequest;

/**
 * Helper for the product configuration
 */
public abstract class AbstractConfigureProduct<T> extends AjaxAction<T>
{
    /**
     * Our product
     */
    protected HtmlElement item;


    public AbstractConfigureProduct(final HtmlElement item)
    {
        this.item = item;
    }

    /**
     * It can happen that we replaced the entire element, so return the new one if needed
     *
     * @return the new or last product detail
     */
    public HtmlElement getItem()
    {
        return item;
    }


    /**
     * perform the variation call updates the dom accordingly
     *
     * @param url
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws Exception
     */
    protected WebResponse call(final String url) throws MalformedURLException, URISyntaxException, Exception
    {
        final WebResponse response = new HttpRequest().XHR().url(url)
                        .assertContent("Nothing came back from product variation.", true, HttpRequest.NOT_BLANK)
                        .assertJSONObject("ProductVariation request call was not successful", true, j -> j.has("product"))
                        .fire();

        return response;
    }
}
