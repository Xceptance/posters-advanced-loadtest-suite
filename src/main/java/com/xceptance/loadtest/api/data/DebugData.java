package com.xceptance.loadtest.api.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

import com.xceptance.loadtest.api.util.Context;

/**
 * Keeps track of debug information for development purposes.
 *
 * @author rschwietzke
 */
public class DebugData
{
    private static final DebugData instance = new DebugData();

    /**
     * Hide constructor, but cannot be private
     */
    DebugData()
    {
    }

    /**
     * Add a level to the stack
     *
     * @param name
     *            the new level name
     * @return the new level depth
     */
    public int pushLevel(final String name)
    {
        return 0;
    }

    /**
     * Remove a level from the stack
     *
     * @return the new level depth
     */
    public int popLevel()
    {
        return 0;
    }

    /**
     * Returns the current depth of the call stack
     *
     * @return current depth
     */
    public int levelDepth()
    {
        return 0;
    }

    /**
     * Set a new timer name that expresses the depth and other things
     *
     * @param a
     *            name supplier
     * @return a new name
     */
    public String adjustTimerName(final Supplier<String> name)
    {
        return name.get();
    }

    /**
     * Returns the right instance, either the load test one or the development one This will save is
     * a lot of "if loadtest" lines
     */
    public static DebugData getInstance()
    {
        if (Context.isLoadTest)
        {
            return instance; // just return the same empty wrapper all the time, saves a little bit
                             // of memory and does not do any harm
        }
        else
        {
            return new DevelopmentDebugData();
        }
    }
}

/**
 * This is the instance that holds the real logic, hence if we forget to implement it the load test
 * will work fine because the compile will force us to make sure that DebugData has the right
 * methods.
 *
 * @author rschwietzke
 */
class DevelopmentDebugData extends DebugData
{
    private final Deque<String> callLevels = new ArrayDeque<>();

    /**
     * Hide constructor
     */
    DevelopmentDebugData()
    {
    }

    /**
     * Add a level to the stack
     *
     * @param name
     *            the new level name
     * @return the new level depth
     */
    @Override
    public int pushLevel(final String name)
    {
        callLevels.push(name);
        return callLevels.size();
    }

    /**
     * Remove a level from the stack
     *
     * @return the new level depth
     */
    @Override
    public int popLevel()
    {
        callLevels.pop();
        return callLevels.size();
    }

    /**
     * The current level depth of the call stack
     *
     * @eturn the current depth
     */
    @Override
    public int levelDepth()
    {
        return callLevels.size();
    }

    /**
     * Set a new timer name that expresses the depth and other things
     *
     * @param a
     *            name supplier
     * @return a new name
     */
    @Override
    public String adjustTimerName(final Supplier<String> name)
    {
        return org.apache.commons.lang3.StringUtils.leftPad("", levelDepth(), '>') + " " + name.get();
    }
}
