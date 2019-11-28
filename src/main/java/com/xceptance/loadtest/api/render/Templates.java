package com.xceptance.loadtest.api.render;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class Templates
{
    private final static Templates instance = new Templates();

    final Configuration freemarker;

    private Templates()
    {
        // setup freemarker
        freemarker = new Configuration(Configuration.VERSION_2_3_23);
        freemarker.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/");
        freemarker.setDefaultEncoding("UTF-8");

        // no getters needed
        final BeansWrapperBuilder wrapperBuilder = new BeansWrapperBuilder(Configuration.VERSION_2_3_23);
        wrapperBuilder.setExposeFields(true);
        freemarker.setObjectWrapper(wrapperBuilder.build());

        freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // there is no reason to check for changes ever
        freemarker.setTemplateUpdateDelayMilliseconds(24 * 60 * 60 * 1000);
    }

    /**
     * Get a template
     *
     * @param fullTemplateName
     *            the template to process including its full classpath, e.g.
     *            /com/xceptance/foo.ftlh
     *
     * @return a loaded template
     *
     * @throws a
     *             RuntimeException exception indicating a problem, that is
     *             mostly important during development
     */
    public static Template template(final String fullTemplateName)
    {
        try
        {
            // Get the template (uses cache internally)
            final Template template = instance.freemarker.getTemplate(fullTemplateName);
            return template;
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process a template and return the result
     *
     * @param fullTemplateName
     *            the template to process including its full classpath, e.g.
     *            /com/xceptance/foo.ftlh
     *
     * @return a result as string, no assumption made about the type
     *
     * @throws a
     *             RuntimeException exception indicating a problem, that is
     *             mostly important during development
     */
    public static String process(final Template template)
    {
        return process(template, Collections.emptyMap());
    }

    /**
     * Process a template and return the result
     *
     * @param fullTemplateName
     *            the template to process including its full classpath, e.g.
     *            /com/xceptance/foo.ftlh
     * @param data
     *            the data to use
     *
     * @return a result as string, no assumption made about the type
     *
     * @throws a
     *             RuntimeException exception indicating a problem, that is
     *             mostly important during development
     */
    public static String process(final String fullTemplateName, final Map<String, Object> data)
    {
        final Template template = template(fullTemplateName);
        return process(template, data);
    }

    /**
     * Process a template and return the result
     *
     * @param fullTemplateName
     *            the template to process including its full classpath, e.g.
     *            /com/xceptance/foo.ftlh
     * @param data
     *            the data to use
     *
     * @return a result as string, no assumption made about the type
     *
     * @throws a
     *             RuntimeException exception indicating a problem, that is
     *             mostly important during development
     */
    public static String process(final Template template, final Map<String, Object> data)
    {
        try
        {
            /* Merge data-model with template */
            final Writer out = new StringWriter(1024);
            template.process(data, out);
            out.close();

            return out.toString();
        }
        catch (final IOException | TemplateException e)
        {
            throw new RuntimeException(e);
        }
    }
}
