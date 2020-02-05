package com.xceptance.loadtest.api.util;

/**
 * Safety break, a check breaking once a defined limit of iterations is exceeded.
 *
 * @autor Xceptance Software Technologies
 */
public class SafetyBreak
{
    /** Current value */
    private int currentValue;

    /** Threshold */
    private final int max;

    /**
     * Initialize the {@link SafetyBreak}
     *
     * @param maxTries
     *            the safety break's threshold
     */
    public SafetyBreak(final int maxTries)
    {
        if (maxTries <= 0)
        {
            throw new IllegalArgumentException("Safety Break limit must be above zero, but was <" + maxTries + ">.");
        }

        this.max = maxTries;
        reset();
    }

    /**
     * Checks if {@link #currentValue} is higher than zero and updates the
     * counter if necessary.
     *
     * @return <code>false</code> if {@link #currentValue} was higher than zero
     *         when the method was called, <code>true</code> otherwise
     */
    private boolean isThresholdReached()
    {
        if (currentValue > 0)
        {
            currentValue--;
            return false;
        }

        return true;
    }

    /**
     * Updates the number of remaining tries and returns <b>true</b> if the configured limit has been reached. Note:
     * every call of this method modifies the tries-counter value.
     *
     * @param key
     *            - a unique key to identify this special safety break
     * @return <code>true</code> if safety break is reached, <code>false</code> otherwise
     */
    public boolean reached()
    {
        return reached("");
    }

    /**
     * Updates the number of remaining tries and returns <b>true</b> if the configured limit has been reached. Note:
     * every call of this method modifies the tries-counter value.
     *
     * @param key
     *            - a unique key to identify this special safety break
     * @param additionalMessage
     *            custom message that should be added to the log entry
     * @return <code>true</code> if safety break is reached, <code>false</code> otherwise
     */
    public boolean reached(final String additionalMessage)
    {
        if (isThresholdReached())
        {
            return true;
        }
        return false;
    }

    /**
     * Updates the number of remaining tries throws an Exception if so. Note: every call of this method modifies the
     * tries-counter value.
     *
     * @throws FlowStoppedException
     *             if security break limit is reached
     */
    public void check() throws FlowStoppedException
    {
        check("");
    }

    /**
     * Updates the number of remaining tries throws an Exception if so. Note: every call of this method modifies the
     * tries-counter value.
     *
     * @param additionalMessage
     *            custom message that should be part of the exception and the log entry
     * @throws FlowStoppedException
     *             if security break limit is reached
     */
    public void check(final String additionalMessage) throws FlowStoppedException
    {
        if (isThresholdReached())
        {
            final String message = getLogMessage(additionalMessage);
            throw new FlowStoppedException(message);
        }
    }

    /**
     * Resets the safety break counter to the pre-configured value.
     */
    public void reset()
    {
        this.currentValue = max;
    }

    /**
     * Retrieves the class and method name of the calling method for logging purpose.
     *
     * @return class and method name of the calling method or <code>null</code> if class and method could not be parsed
     *         from stack trace.
     */
    private String getCaller()
    {
        // Parse all stack trace elements.
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++)
        {
            // This will filter out the Page.checkExistenceInPage() method and will give us the real method which calls
            // it.
            if (!stackTraceElements[i].getClassName().equals(this.getClass().getName())
                && !stackTraceElements[i].getClassName().equals(Thread.class.getName()))
            {
                final String className = stackTraceElements[i].getClassName();
                return className.substring(className.lastIndexOf('.') + 1) + "." + stackTraceElements[i + 1].getMethodName();
            }
        }

        return "";
    }

    /**
     * Get a message based on the given custom message part.
     *
     * @param additionalMessage
     *            custom part of log message
     * @return message based on the given custom message part
     */
    private String getLogMessage(final String additionalMessage)
    {
        return "SafetyBreak for " + getCaller() + " reached! " + additionalMessage;
    }
}