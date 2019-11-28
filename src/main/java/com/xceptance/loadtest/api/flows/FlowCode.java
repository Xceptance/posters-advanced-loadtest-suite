package com.xceptance.loadtest.api.flows;

@FunctionalInterface
public interface FlowCode
{
    public boolean execute() throws Throwable;
}