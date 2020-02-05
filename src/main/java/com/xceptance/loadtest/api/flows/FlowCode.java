package com.xceptance.loadtest.api.flows;

/**
 * Interface for flow code.
 * 
 * @author Xceptance Software Technologies
 */
@FunctionalInterface
public interface FlowCode
{
    public boolean execute() throws Throwable;
}