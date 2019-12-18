package com.xceptance.loadtest.posters.tests.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.tests.LoadTestCase;
import com.xceptance.loadtest.posters.actions.catalog.ClickACategory;
import com.xceptance.loadtest.posters.flows.VisitFlow;
import com.xceptance.loadtest.posters.models.pages.catalog.CategoryLandingPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.models.pages.search.SearchNoResultPage;

/**
 * This test case visits all pages that could be reached by Select(Top)Category and helps to identify unknown pages.
 *
 * @author Daniel Kirst
 */
public class TVisitAllCategoryLinks extends LoadTestCase
{
    /** Unknown pages */
    private final Set<String> crashedPages = new HashSet<>();

    /** Link without href */
    private int missingHref = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Throwable
    {
        // Start at the landing page.
        new VisitFlow().run();

        // Visit all (top) category links.
        visitAllCategoryLinks();
    }

    private void visitAllCategoryLinks()
    {

        // Get all category menu links
        final List<HtmlElement> links = GeneralPages.instance.navigation.getTopCategories().all();
        links.addAll(GeneralPages.instance.navigation.getCategories().all());

        // Visit every (top) category.
        visitAll(links);

        // Print result.
        outputUnknowPages();
    }

    /**
     * Visit every passed page in the link collection.
     *
     * @param links
     *            A list of links to visit.
     */
    private void visitAll(final List<HtmlElement> links)
    {
        int index = 0;
        // Visit every link in the collection.
        for (final HtmlElement link : links)
        {
            ++index;
            System.out.println("Link: " + index + " / " + links.size());
            // Visit the category page. If anything goes wrong or the page is of a unknown type, the page will be
            // added to the set of unknown pages.
            try
            {
                // ClickACategory
                new ClickACategory(link).run();
                // ended on PDP/QV?
                if (ProductListingPage.instance.is())
                {
                    // all good
                }
                else if (ProductDetailPage.instance.is())
                {
                    // even better
                }
                else if (CategoryLandingPage.instance.is())
                {
                    // Nothing to do here, landing page
                }
                else if (SearchNoResultPage.instance.is())
                {
                    // We got no product hits and did not open the product details. So there's
                    // nothing to do.
                }
                else
                {
                    // We already logged an event if we've refined to a page without any hits.
                    Assert.fail("Browsing flow ended on unknown page.");
                }
            }
            catch (final Throwable e)
            {
                // Check whether an href attribute with text exists
                if (StringUtils.isNotBlank(link.getAttribute("href")))
                {
                    // Add the link to the set
                    crashedPages.add(link.getAttribute("href"));
                }
                else
                {
                    // For unknown reasons the href attribute is missing - just add it to the list to report.
                    missingHref++;
                }
            }
        }
    }

    /**
     * Prints the unknown pages from the last run to the error log.
     */
    public void outputUnknowPages()
    {
        if (!crashedPages.isEmpty())
        {
            System.out.println("\n\nFound " + crashedPages.size() + " following unknown page(s):");
            System.out.println("################################\n");
            for (final String page : crashedPages)
            {
                System.out.println(page);
            }
            System.out.println("\nFound " + missingHref + " following links without href.");

            Assert.fail("Unknown pages detected. Please see console output for more details");
        }
        else
        {
            System.out.println("No unknown page found.");
        }
    }
}
