package com.xceptance.loadtest.api.actions.debug;

import java.util.function.Supplier;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.engine.SessionImpl;

/**
 * Logs an action name without doing anything else.
 * 
 * Used only for debug purposes.
 *
 * @author Xceptance Software Technologies
 */
public class DebugAction extends PageAction<DebugAction>
{
    /**
     * Creates the action and sets the action name.
     *
     * @param name The action name
     */
    private DebugAction(final String name)
    {
        super();

        this.setTimerName(name);
    }

    @Override
    protected void doExecute() throws Exception
    {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     *
     * @see com.xceptance.xlt.loadtest.actions.AbstractHtmlPageAction#postExecute()
     */
    @Override
    protected void postExecute() throws Exception
    {
        // Do nothing
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Do nothing
    }

    @Override
    public void run() throws Throwable
    {
        // Help to keep the random stream correct
        final SessionImpl sessionImpl = SessionImpl.getCurrent();

        final boolean isFirstActionInTestCase = (sessionImpl.isExecuteThinkTime() == false);

        super.run();

        if (isFirstActionInTestCase)
        {
            // Make the following action think it was the first in the test case
            sessionImpl.setExecuteThinkTime(false);
        }
    }

    @Override
    protected void executeThinkTime()
    {
        // Do nothing
    }

    /**
     * Executes a debug action if permitted (only during development, never during load testing).
     *
     * @param name The action name
     * @throws Throwable
     */
    public static void log(final Supplier<String> name) throws Throwable
    {
        if (Context.isLoadTest == false && Context.configuration().useDebugActions)
        {
            new DebugAction(name.get()).run();
        }
    }
}