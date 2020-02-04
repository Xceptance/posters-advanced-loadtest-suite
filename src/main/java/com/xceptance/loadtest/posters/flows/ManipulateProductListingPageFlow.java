package com.xceptance.loadtest.posters.flows;

import java.util.Arrays;
import java.util.List;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.flows.FlowCode;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.Paging;

/**
 * Manipulates the product listing page by executing the available product listing page actions.
 * 
 * Available actions will be executed in a random fashion.
 * 
 * @author Xceptance Software Technologies
 */
public class ManipulateProductListingPageFlow extends Flow
{
    private static final List<FlowCode> flows = Arrays.asList(
                    () ->
                    {
                        page();
                        // If the page would support further listing page actions (refinements, sorting etc.) we would put them here

                        return true;
                    },
                    () ->
                    {
                        page();
                        page();
                        page();
                        // If the page would support further listing page actions (refinements, sorting etc.) we would put them here
                        
                        return true;
                    });

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        return Flow.createAndRun(this.getClass().getSimpleName(), flows);
    }

    /**
     * Performs paging.
     */
    private static void page() throws Throwable
    {
        if (Context.configuration().displayMoreProbability.random())
        {
            if (!new Paging().runIfPossible().isPresent())
            {
                // Context.logForDebug("No paging/infinite scroll available.");
            }
        }
    }
}