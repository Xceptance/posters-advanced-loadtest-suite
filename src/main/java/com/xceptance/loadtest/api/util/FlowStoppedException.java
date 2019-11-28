package com.xceptance.loadtest.api.util;

/**
 * Generic exception thrown if a flow is stopped unexpectedly. This usually does not rise an error situation during a
 * load test.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class FlowStoppedException extends Exception
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2689972420218469687L;

    /**
     * Create the exception with custom message
     * 
     * @param message
     *            custom exception message
     */
    public FlowStoppedException(final String message)
    {
        super(message);
    }
}
