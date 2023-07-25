package com.xceptance.loadtest.api.hpu;

import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import com.xceptance.common.util.ParameterCheckUtils;

/**
 * Lookup base element.
 * 
 * @autor Xceptance Software Technologies
 */
public class In
{
    /**
     * Set the given element as lookup base.
     * 
     * @param element
     *            lookup base
     * @return strategy and locator holder
     * @throws IllegalArgumentException
     *             if the element is <code>null</code>
     */
    public By in(HtmlElement element) throws IllegalArgumentException
    {
        return in(element, "element");
    }

    /**
     * Set the given page as lookup base.
     * 
     * @param page
     *            lookup base
     * @return strategy and locator holder
     * @throws IllegalArgumentException
     *             if the page is <code>null</code>
     */
    public By in(final HtmlPage page) throws IllegalArgumentException
    {
        return in(page, "page");
    }

    /**
     * Set the lookup base element.
     * 
     * @param parent
     *            Lookup base element
     * @param parameterName
     *            Name of the base parameter ('element' or 'page')
     * @return strategy and locator holder
     * @throws IllegalArgumentException
     *             if the parent is <code>null</code>
     */
    protected By in(final DomNode parent, final String parameterName) throws IllegalArgumentException
    {
        ParameterCheckUtils.isNotNull(parent, parameterName);
        return new By(parent);
    }
}