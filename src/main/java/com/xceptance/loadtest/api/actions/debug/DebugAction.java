package com.xceptance.loadtest.api.actions.debug;

import java.util.function.Supplier;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.engine.SessionImpl;

/**
 * This does not do anything and will break when used in performance mode.
 *
 * @author Rene Schwietzke
 * @version
 */
public class DebugAction extends PageAction<DebugAction>
{
    // public static final AtomicInteger ai = new AtomicInteger(0);
    // public static final List<Integer> rl = new ArrayList<>();

    /**
     * Just create an action and log the name
     *
     * @param name
     *            the action name
     */
    private DebugAction(final String name)
    {
        super();

        // that is all we want!
        this.setTimerName(name);
    }

    @Override
    protected void doExecute() throws Exception
    {
        // nothing
    }

    /*
     * (non-Javadoc)
     *
     * @see com.xceptance.xlt.loadtest.actions.AbstractHtmlPageAction#postExecute()
     */
    @Override
    protected void postExecute() throws Exception
    {
        // nothing to do
    }

    @Override
    protected void postValidate() throws Exception
    {
        // do nothing
    }

    @Override
    public void run() throws Throwable
    {
        // help to keep the random stream correct
        final SessionImpl sessionImpl = SessionImpl.getCurrent();

        final boolean isFirstActionInTestCase = (sessionImpl.isExecuteThinkTime() == false);

        // do the regular stuff even though we don't need it
        super.run();

        if (isFirstActionInTestCase)
        {
            // make the following action think it was the first in the test case
            sessionImpl.setExecuteThinkTime(false);
        }
    }

    @Override
    protected void executeThinkTime()
    {
    }

    /**
     * Executes a debug action if permitted aka only during development, never during load testing
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
