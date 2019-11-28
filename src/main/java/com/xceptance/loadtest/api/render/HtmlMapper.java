package com.xceptance.loadtest.api.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.DOMUtils;

/**
 * Permits to read from two sources (renderer and html fragment) and after establishing the html of
 * these, copy selectively data from source to target.
 *
 * @author rschwietzke
 *
 */
public class HtmlMapper
{
    private final HtmlPage page;
    private HtmlElement in;
    private String htmlFragment;
    private HtmlRenderer renderer;

    private final List<Mapping> mappings = new ArrayList<>(5);

    /**
     * Sets the initial context for the renderer. No page no fun.
     *
     * @param page
     *            the page to use
     * @return the current updated renderer
     */
    private HtmlMapper(final HtmlPage page)
    {
        this.page = page;
    }

    public static HtmlMapper page(final HtmlPage page)
    {
        return new HtmlMapper(page);
    }

    public HtmlMapper in(final HtmlElement in)
    {
        this.in = in;
        return this;
    }

    /**
     * Replace the to elements with the from element
     * @param fromCSS
     * @param toCSS
     * @return this updated instance
     */
    public HtmlMapper byCSS(final String fromCSS, final String toCSS)
    {
        mappings.add(new Mapping(fromCSS, toCSS));
        return this;
    }

    /**
     * Set the CSS path for both sides to be identically.
     *
     * @param css
     * @return this updated instance
     */
    public HtmlMapper byCSS(final String css)
    {
        return byCSS(css, css);
    }

    public HtmlMapper html(final String htmlFragment)
    {
        this.htmlFragment = htmlFragment;
        this.renderer = null;
        ;
        return this;
    }

    public HtmlMapper html(final HtmlRenderer renderer)
    {
        this.renderer = renderer;
        this.htmlFragment = null;
        return this;
    }

    public void map()
    {
        // create container in page
        final HtmlElement tempContainerFragment = Page.createByID(UUID.randomUUID().toString());

        // parse html fragment
        if (renderer != null)
        {
            renderer.replaceContentOf(tempContainerFragment);
        }
        else if (htmlFragment != null)
        {
            DOMUtils.replaceContent(tempContainerFragment, htmlFragment);
        }
        else
        {
            Assert.fail("No html source or renderer provided");
        }

        // map the children into the page and replace the placeholders
        for (final Mapping mapping : mappings)
        {
            final HtmlElement source = HPU.find().in(tempContainerFragment).byCss(mapping.fromCSS).first();
            final HtmlElement target = (in != null) ? HPU.find().in(in).byCss(mapping.toCSS).first()
                            : HPU.find().in(page).byCss(mapping.toCSS).first();

            Assert.assertNotNull("Did not find target in website " + mapping.toCSS + " for render mapping", target);
            Assert.assertNotNull("Did not find source in template " + mapping.fromCSS + " for render mapping", source);

            if (target != null)
            {
                target.replace(source);
            }
        }

        // clean up the temp stuff
        tempContainerFragment.remove();
    }

    class Mapping
    {
        final public String fromCSS;
        final public String toCSS;

        public Mapping(final String fromCSS, final String toCSS)
        {
            this.fromCSS = fromCSS;
            this.toCSS = toCSS;
        }
    }
}
