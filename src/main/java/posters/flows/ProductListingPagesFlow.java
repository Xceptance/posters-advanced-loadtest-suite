package posters.flows;

import java.util.Arrays;
import java.util.List;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.flows.FlowCode;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.catalog.DisplayMore;
import posters.actions.catalog.RefineBy;
import posters.actions.catalog.RefineByCategory;
import posters.actions.catalog.SortBy;

/**
 * Page, sort, change items per page and refine results on the product grid page.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ProductListingPagesFlow extends Flow
{
    // this has been moved here to avoid to reset this piece again and again
    private static final List<FlowCode> flows = Arrays.asList(
                    () ->
                    {
                        // 0: standard flow, one click of everything
                        refineByCategory();
                        refine();
                        sort();
                        displayMore();
                        return true;
                    },
                    () ->
                    {
                        // 1: simple
                        refineByCategory();
                        refine();
                        displayMore();
                        return true;
                    },
                    () ->
                    {
                        // 2: I am not a refiner ;)
                        return true;
                    },
                    () ->
                    {
                        // 2: user prefers categories
                        refineByCategory();
                        refineByCategory();
                        return true;
                    },
                    () ->
                    {
                        // 4: detailed refiner
                        refineByCategory();
                        refine();
                        refine();
                        refine();

                        return true;
                    },
                    () ->
                    {
                        // 5: trying to sort and see
                        sort();
                        displayMore();
                        displayMore();
                        displayMore();
                        sort();
                        displayMore();
                        displayMore();

                        return true;
                    },
                    () ->
                    {
                        // 6: we understand only the category thing
                        refineByCategory();
                        refineByCategory();
                        refineByCategory();

                        return true;
                    },
                    () ->
                    {
                        // 7: different order of default
                        displayMore();
                        sort();
                        refine();
                        refineByCategory();

                        return true;
                    },
                    () ->
                    {
                        // 8: refine only
                        refine();
                        refine();
                        refine();
                        refine();

                        return true;
                    },
                    () ->
                    {
                        // 9: quick decider
                        refineByCategory();

                        return true;
                    },
                    () ->
                    {
                        // 10: little bit of everything
                        refineByCategory();
                        refineByCategory();
                        refine();
                        displayMore();
                        sort();
                        refineByCategory();
                        refine();
                        sort();
                        displayMore();

                        return true;
                    },
                    () ->
                    {
                        // 11: piece by piece but not the first product, but maybe
                        // a later one
                        refine();
                        displayMore();
                        displayMore();
                        displayMore();
                        displayMore();

                        return true;
                    });

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // not fancy enough yet, just to make sure we don't do the same order of
        // things all the time
        return Flow.createAndRun(this.getClass().getSimpleName(), flows);
    }

    /**
     * According to the configured probability perform the paging or not.
     */
    private static void displayMore() throws Throwable
    {
        if (Context.configuration().displayMoreProbability.random())
        {
            if (!new DisplayMore().runIfPossible().isPresent())
            {
                // If there's no display more available, write out an debug
                // log. If there are to much logging message, the
                // XPath or the catalog may have issues
                // Context.logForDebug("No paging/inifinte scroll available.");
            }
        }
    }

    /**
     * Processes sorting with the configured probability.
     */
    private static void sort() throws Throwable
    {
        if (Context.configuration().sortingProbability.random())
        {
            if (!new SortBy().runIfPossible().isPresent())
            {
                // If there's no sorting option available, write out an debug log. If there are to much logging message,
                // the XPath or the catalog may have issues
                // Context.logForDebug("No sorting available.");
            }
        }
    }

    /**
     * The configured probability will decide whether to run the refining action or not.
     */
    private static void refine() throws Throwable
    {
        if (Context.configuration().refinementProbability.random())
        {
            new RefineBy().runIfPossible();
        }
    }

    /**
     * Refine by category
     */
    private static void refineByCategory() throws Throwable
    {
        // If we are a typical category surfer, we might do category
        // refinement now
        if (Context.configuration().categoryRefinementProbability.random())
        {
            // Refine by category.
            if (!new RefineByCategory().runIfPossible().isPresent())
            {
                // Context.logForDebug("No refine by category available.");
            }
        }
    }
}
