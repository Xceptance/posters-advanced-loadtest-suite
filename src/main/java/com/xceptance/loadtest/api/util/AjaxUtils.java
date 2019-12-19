package com.xceptance.loadtest.api.util;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.pages.Page;

/**
 * Ajax utility class.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public final class AjaxUtils
{
    /**
     * Default constructor. Declared private to prevent external instantiation.
     */
    private AjaxUtils()
    {
        // Nothing to do
    }

    /**
     * Transform the given parameter list to an URL conform parameter string.
     *
     * @param parameters
     *            parameters to transform
     * @return an URL parameter string
     */
    static String paramsToQueryString(final List<NameValuePair> parameters) throws Exception
    {
        final ArrayList<org.apache.http.NameValuePair> arr = new ArrayList<>();
        for (final NameValuePair param : parameters)
        {
            arr.add(new BasicNameValuePair(param.getName(), param.getValue()));
        }

        return URLEncodedUtils.format(arr, "UTF-8");
    }

    /**
     * Get the form's fields as list of name value pairs.
     *
     * @param form
     *            the form to parse
     * @return the form's fields as list of name value pairs.
     */
    public static List<NameValuePair> serializeForm(final HtmlForm form)
    {
        return serializeForm(form, true);
    }

    /**
     * Get the form's fields as list of name value pairs.
     *
     * @param form
     *            the form to parse
     * @param includeLostChildren
     *            whether or not to include the form's lost children
     * @return the form's fields as list of name value pairs.
     */
    public static List<NameValuePair> serializeForm(final HtmlForm form, final boolean includeLostChildren)
    {
        final List<NameValuePair> children = new ArrayList<>();

        for (final Object o : form.getByXPath(".//select|.//input|.//textarea"))
        {
            handleElement((HtmlElement) o, children);
        }

        if (includeLostChildren)
        {
            for (final HtmlElement e : form.getLostChildren())
            {
                handleElement(e, children);
            }
        }

        return children;
    }

    /**
     * Get the form elements wrapped by the given element as list of name-value pairs.
     *
     * @param element
     *            the element that holds the form elements
     * @return list of name-value pairs
     */
    public static List<NameValuePair> serialize(final HtmlElement element)
    {
        final List<NameValuePair> children = new ArrayList<>();

        for (final Object o : element.getByXPath(".//select|.//input"))
        {
            handleElement((HtmlElement) o, children);
        }

        return children;
    }

    /**
     * @param element
     * @param children
     */
    private static void handleElement(final HtmlElement element, final List<NameValuePair> children)
    {
        if (element instanceof HtmlInput)
        {
            final HtmlInput input = (HtmlInput) element;

            final String nameAtt = input.getNameAttribute();
            if (StringUtils.isEmpty(nameAtt))
            {
                return;
            }
            final String valueAtt = input.getValueAttribute();

            String typeAtt = input.getTypeAttribute().toLowerCase();
            // fall-back to HTML4 input type
            if (!RegExUtils.isMatching(typeAtt, "^text|password|checkbox|radio|submit|reset|file|hidden|image|button$"))
            {
                typeAtt = "text";
            }

            boolean add = false;
            if (("radio".equals(typeAtt) || "checkbox".equals(typeAtt)) && input.hasAttribute("checked"))
            {
                add = input.hasAttribute("checked");
            }
            else
            {
                add = "text".equals(typeAtt) || "hidden".equals(typeAtt) || "password".equals(typeAtt);
            }

            if (add)
            {
                children.add(new NameValuePair(nameAtt, valueAtt));
            }
        }
        else if (element instanceof HtmlSelect)
        {
            final HtmlSelect select = (HtmlSelect) element;
            final String nameAtt = select.getNameAttribute();
            if (StringUtils.isEmpty(nameAtt))
            {
                return;
            }

            for (final HtmlOption option : select.getSelectedOptions())
            {
                children.add(new NameValuePair(nameAtt, option.getValueAttribute()));
            }
        }
        else if (element instanceof HtmlTextArea)
        {
            final HtmlTextArea textarea = (HtmlTextArea) element;
            final String nameAtt = textarea.getNameAttribute();
            if (StringUtils.isEmpty(nameAtt))
            {
                return;
            }

            children.add(new NameValuePair(nameAtt, textarea.getText()));
        }
    }

    public static String getFormValue(final HtmlPage page, final HtmlElement e)
    {
        if (e instanceof HtmlInput)
        {
            return ((HtmlInput) e).getValueAttribute();
        }
        else if (e instanceof HtmlSelect)
        {
            return ((HtmlSelect) e).getSelectedOptions().get(0).getValueAttribute();
        }

        return "undefined";
    }

    /**
     * Transform parameter string to linked hash map of key-value pairs. Keep in mind that adding a
     * key duplicate will overwrite the initial value.
     *
     * @param params
     *            hash/query parameters
     * @return parameter map
     * @see {@link #getUrlParamsAsList}
     */
    public static Map<String, String> parseUrlParamsToMap(final String url) throws URISyntaxException
    {
        if (StringUtils.isBlank(url))
        {
            return new HashMap<>();
        }

        final List<org.apache.http.NameValuePair> queryParams = new URIBuilder(formatUrl(url)).getQueryParams();
        final Map<String, String> map = new LinkedHashMap<>();
        for (final org.apache.http.NameValuePair pair : queryParams)
        {
            final String value = pair.getValue();
            map.put(pair.getName(), StringUtils.isBlank(value) ? "" : value);
        }

        return map;
    }

    /**
     * Transform url string to list of value pairs. Keep in mind that this representation of
     * parameters allows key duplicates by default.
     *
     * @param params
     *            hash/query parameters
     * @return parameter list
     * @throws URISyntaxException
     * @see {@link #parseUrlParamsToMap(String)}
     */
    public static List<NameValuePair> parseUrlParamsToList(final String url) throws URISyntaxException
    {
        if (StringUtils.isBlank(url))
        {
            return new ArrayList<>();
        }

        return new URIBuilder(formatUrl(url)).getQueryParams()
                                             .stream()
                                             .map(s -> new NameValuePair(s.getName(),
                                                                         (s.getValue() != null ?
                                                                          s.getValue() : StringUtils.EMPTY)))
                                             .collect(Collectors.toList());
    }

    /**
     * Tries to build a valid URL for the URIBuilder out of the passed String
     *
     * @param url
     *            the String which should be preformatted for the URIBuilder
     * @return the preformatted URL
     */
    private static String formatUrl(final String url)
    {
        String trimmedUrl = "";
        if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("?") || url.startsWith("/"))
        {
            // don't touch it. The URIBuilder will deal with it perfectly
            trimmedUrl = url;
        }
        else if (url.startsWith("#"))
        {
            // assumed is to get parameters hidden in the URL reference, so we replace the hash by a
            // question mark and pass it to the URI parser
            trimmedUrl = "?" + url.substring(1);
        }
        else if (url.contains("?") || url.contains("#") || url.contains("/"))
        {
            // seems to be an relative URL, so we keep it as it is
            trimmedUrl = url;
        }
        else
        {
            // that one is for strings like `foo=bar&abc=xyz`
            trimmedUrl = "?" + url;
        }

        return trimmedUrl;
    }


    /**
     * Get the text content of the script that contains the given search phrase
     * (typically a part of a wanted URL or a key). The scripted is looked up in the
     * whole document.
     *
     * @param searchPhrase
     *            to identify the script element
     */
    public static String getScript(final String searchPhrase)
    {
        return getScript(searchPhrase, Page.find().byXPath("/html"));
    }

    /**
     * Get the text content of the script that contains the given search phrase
     * (typically a part of a wanted URL or a key). The script is expected to be a
     * direct child of the given parent element.
     *
     * @param searchPhrase
     *            to identify the script element
     * @param scriptParent
     *            parent node that contains the script
     */
    public static String getScript(final String searchPhrase, final HtmlElement scriptParent)
    {
        return getScript(searchPhrase, HPU.find().in(scriptParent).byXPath("."));
    }

    /**
     * Get the text content of the script that contains the given search phrase
     * (typically a part of a wanted URL or a key). The script is expected to be a
     * direct child of the described parent element.
     *
     * @param searchPhrase
     *            to identify the script element
     * @param scriptParent
     *            parent node that contains the script
     */
    public static String getScript(final String searchPhrase, final LookUpResult scriptParent)
    {
        LookUpResult scriptLocator = scriptParent.byXPath("./script[contains(.,'" + searchPhrase + "')]");

        if (!scriptLocator.exists())
        {
            final String failMessage = "Script at non expected position. Check for defective HTML. Search phrase : '" + searchPhrase + "'" + ", Script parent: '" + scriptParent
                            + "'";
            if (Context.isLoadTest)
            {
                EventLogger.DEFAULT.warn(failMessage, Context.getPage().getUrl().toExternalForm());
            }
            else
            {
                Assert.fail(failMessage);
            }
            // If this is not the case, search in the whole document (header+body)
            scriptLocator = Page.find().byXPath("/html//script[contains(.,'" + searchPhrase + "')]");
        }

        // get script content
        return scriptLocator.asserted("Script not found for search phrase: '" + searchPhrase + "'").first().getTextContent();
    }

    /**
     * Converts the given WebResponse to a JSONObject.
     * 
     * @param webResponse The WebResponse that should be converted to a JSONObject.
     * @return The resulting JSONObject.
     */
	public static JSONObject convertToJson(WebResponse webResponse)
	{
		return convertToJson(webResponse.getContentAsString());
	}

	/**
     * Converts the given response string to a JSONObject.
     * 
     * Will break on JSON parse error.
     * 
     * @param responseString The response string that should be converted to a JSONObject.
     * @return The resulting JSONObject.
     */
	public static JSONObject convertToJson(String responseString)
	{
		try
		{
			return new JSONObject(responseString);
		}
		catch(Exception e)
		{
			Assert.fail("Failed to convert response to JSON");
			return null;
		}
	}
	
    /**
     * Checks if a given JSONArray is empty.
     */
    public static Function<JSONArray, Boolean> ARRAY_NOT_EMPTY = json -> json.length() > 0;

    /**
     * Checks if a given JSONObject contains one or more keys.
     */
    public static Function<JSONObject, Boolean> OBJECT_NOT_EMPTY = json -> json.length() > 0;
}
