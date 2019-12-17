package com.xceptance.loadtest.api.flows;

import java.text.MessageFormat;
import java.util.List;

import com.xceptance.loadtest.api.actions.debug.DebugAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Provide a unique interface for flow execution.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public abstract class Flow
{
    /**
     * Run the flow.
     *
     * @throws Throwable
     *             if something bad happens
     * @return true if successful, false otherwise
     */
    protected abstract boolean execute() throws Throwable;

    /**
     * Starts this flow and wraps debug information around it when we are in debug aka non-loadtest
     * mode
     *
     * @return the result of the flow execution
     * @throws Throwable
     */
    public boolean run() throws Throwable
    {
        return run(this.getClass().getSimpleName());
    }

    /**
     * Starts this flow and wraps debug information around it when we are in debug aka non-loadtest
     * mode
     *
     * @param name
     *            the name to report
     * @return the result of the flow execution
     * @throws Throwable
     */
    protected boolean run(final String name) throws Throwable
    {
        if (Context.isLoadTest == false)
        {
            final int newSize = Context.get().debugData.pushLevel(name);
            DebugAction.log(() -> MessageFormat.format(
                            "{1} === {0} ...",
                            name,
                            org.apache.commons.lang3.StringUtils.leftPad("", newSize, '>')));
        }

        // execute the flow and keep the result
        final boolean result = execute();

        if (Context.isLoadTest == false)
        {
            DebugAction.log(() -> MessageFormat.format(
                            "{1} === ... {0}",
                            name,
                            org.apache.commons.lang3.StringUtils.leftPad("", Context.get().debugData.levelDepth(),
                                            '<')));
            Context.get().debugData.popLevel();
        }

        return result;
    }

    /**
     * Creates and executes an adhoc flow
     *
     * @param name
     *            the name to display when not in load test mode
     * @param code
     *            the code to execute
     *
     * @throws Throwable
     */
    public static boolean createAndRun(final String name, final FlowCode code) throws Throwable
    {
        return new AdhocFlow(name, code).run();
    }

    /**
     * Creates and executes an adhoc flow based on a list of selected code pieces
     *
     * @param name
     *            the name to display when not in load test mode
     * @param code
     *            the code to execute
     *
     * @throws Throwable
     */
    public static boolean createAndRun(final String name, final List<FlowCode> codes) throws Throwable
    {
        final int r = XltRandom.nextInt(codes.size());
        return new AdhocFlow(name + ":" + r, codes.get(r)).run();
    }

    /**
     * Just to run any code as flow if needed
     *
     * @author rschwietzke
     */
    static class AdhocFlow extends Flow
    {
        /**
         * The adhoc code
         */
        private final FlowCode code;

        /**
         * Our name for later
         */
        private final String name;

        private AdhocFlow(final String name, final FlowCode code)
        {
            this.name = name;
            this.code = code;
        }

        @Override
        protected boolean execute() throws Throwable
        {
            return code.execute();
        }

        @Override
        public boolean run() throws Throwable
        {
            return run(name);
        }
    }
}
