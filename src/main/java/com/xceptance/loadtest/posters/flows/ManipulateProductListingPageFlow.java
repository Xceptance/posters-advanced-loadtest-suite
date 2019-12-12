package com.xceptance.loadtest.posters.flows;

import java.util.Arrays;
import java.util.List;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.flows.FlowCode;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.catalog.DisplayMore;

/**
 * Manipulates the product listing page by executing the available product listing page actions.
 * 
 * Available actions will be executed in a random fashion.
 */
public class ManipulateProductListingPageFlow extends Flow
{
    private static final List<FlowCode> flows = Arrays.asList(
                    () ->
                    {
                        displayMore();
                        // TODO add more/different product listing page actions

                        return true;
                    },
                    () ->
                    {
                        displayMore();
                        // TODO add more/different product listing page actions
                        
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
     * Performs paging or load more (infinite scroll).
     */
    private static void displayMore() throws Throwable
    {
        if (Context.configuration().displayMoreProbability.random())
        {
            if (!new DisplayMore().runIfPossible().isPresent())
            {
                // Context.logForDebug("No paging/infinite scroll available.");
            }
        }
    }
}
