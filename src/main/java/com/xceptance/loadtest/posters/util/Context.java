package com.xceptance.loadtest.posters.util;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.util.context.CoreContext;
import com.xceptance.loadtest.api.util.context.HtmlUnitContext;
import com.xceptance.loadtest.posters.configuration.Configuration;
import com.xceptance.loadtest.posters.configuration.TestData;
import com.xceptance.xlt.api.engine.Session;

/**
 * Site specific version of the context, parametrized by the employed Configuration and TestData variants.
 */
public class Context extends HtmlUnitContext<Configuration, TestData>
{
    /**
     * Creates an instance of the context.
     *
     * @param fullTestClassName The full class name of the test case.
     * @param site              The current site.
     */
    public Context(final String fullTestClassName, final Site site)
    {
        super(Session.getCurrent().getUserName(),
                fullTestClassName,
                site, Configuration.class, new TestData());
    }

    /**
     * Retrieves an instance of the context.
     *
     * @return The context instance.
     */
    public static Context get()
    {
        return (Context) CoreContext.get();
    }
}
