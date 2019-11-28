package com.xceptance.loadtest.api.render;

import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.google.gson.Gson;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DOMUtils;

import freemarker.template.Template;

public class HtmlRenderer
{
    private Template template;

    private final Map<String, Object> data = new HashMap<>();

    /**
     * Sets the initial context for the renderer. No page no fun.
     *
     * @return the current updated renderer
     */
    private HtmlRenderer()
    {
    }

    public static HtmlRenderer page()
    {
        return new HtmlRenderer();
    }

    public HtmlRenderer template(final String fullTemplateName)
    {
        template = Templates.template(fullTemplateName);
        return this;
    }

    public HtmlRenderer json(final String response, final Class<?> clazz)
    {
        return json(response, clazz, "data");
    }

    public HtmlRenderer data(final String key, final Object value)
    {
        data.put(key, value);
        return this;
    }

    public HtmlRenderer json(final String response, final Class<?> clazz, final String name)
    {
        // get us something easy to work with
        final Gson gson = Context.getGson();
        final Object parsedJson = gson.fromJson(response, clazz);

        data.put(name, parsedJson);

        return this;
    }

    /**
     * Get us the json back for further processing in other areas
     * @param element
     * @return the json for that name
     */
    @SuppressWarnings("unchecked")
    public <T> T getJson(final Class<T> clazz, final String name)
    {
        return (T) data.get(name);
    }

    public HtmlElement replace(final HtmlElement element)
    {
        final String newHtml = Templates.process(template, data);
        return DOMUtils.replaceElement(element, newHtml);
    }

    public HtmlRenderer appendTo(final HtmlElement element)
    {
        final String newHtml = Templates.process(template, data);
        DOMUtils.appendElement(element, newHtml);

        return this;
    }

    public HtmlRenderer replaceContentOf(final HtmlElement element)
    {
        final String newHtml = Templates.process(template, data);
        DOMUtils.replaceContent(element, newHtml);

        return this;
    }
}
