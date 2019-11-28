package com.xceptance.loadtest.api.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.DOMUtils;

import freemarker.template.Template;

/**
 * Render a template with data and put html children elements from another html fragments into
 * certain marked positions.
 *
 * @author rschwietzke
 */
public class HtmlMapRenderer
{
    private Template template;
    private String htmlFragment;

    private final Map<String, Object> data = new HashMap<>();

    /**
     * Sets the initial context for the renderer. No page no fun.
     *
     * @return the current updated renderer
     */
    private HtmlMapRenderer()
    {
    }

    public static HtmlMapRenderer page()
    {
        return new HtmlMapRenderer();
    }

    public HtmlMapRenderer data(final String key, final Object value)
    {
        data.put(key, value);
        return this;
    }

    public HtmlMapRenderer template(final String fullTemplateName)
    {
        template = Templates.template(fullTemplateName);
        return this;
    }

    public HtmlMapRenderer html(final String htmlFragment)
    {
        this.htmlFragment = htmlFragment;
        return this;
    }

    private HtmlElement process()
    {
        // create container in page
        final HtmlElement tempContainerFreemarker = Page.createByID(UUID.randomUUID().toString());
        final HtmlElement tempContainerFragment = Page.createByID(UUID.randomUUID().toString());

        // create html from freemarker
        final String freeMarkerHtml = Templates.process(template, data);
        // parse html fragment
        DOMUtils.replaceContent(tempContainerFreemarker, freeMarkerHtml);

        // parse html fragment
        DOMUtils.replaceContent(tempContainerFragment, htmlFragment);

        // split up html fragment by children, #replaceWith_0, #replaceWith_1...
        final List<HtmlElement> children = new ArrayList<>(5);
        final Iterable<DomElement> iterator = tempContainerFragment.getChildElements();
        iterator.forEach(c -> children.add((HtmlElement) c));

        // map the children into the page and replace the placeholders
        for (int i = 0; i < children.size(); i++)
        {
            final HtmlElement target = HPU.find().in(tempContainerFreemarker).byId("replaceWith_" + i).first();
            if (target != null)
            {
                target.replace(children.get(i));
            }
        }

        // clean up the temp stuff
        tempContainerFragment.remove();
        tempContainerFreemarker.remove();

        return tempContainerFreemarker;
    }

    public void replace(final HtmlElement element)
    {
        final HtmlElement result = process();
        element.replace(result.getFirstElementChild());
    }

    public void appendTo(final HtmlElement element)
    {
        final HtmlElement result = process();
        element.appendChild(result.getFirstElementChild());
    }

    public void replaceContentOf(final HtmlElement element)
    {
        final HtmlElement result = process();

        element.removeAllChildren();

        final Iterable<DomElement> iterator = result.getChildElements();
        iterator.forEach(c -> element.appendChild(c));
    }
}
