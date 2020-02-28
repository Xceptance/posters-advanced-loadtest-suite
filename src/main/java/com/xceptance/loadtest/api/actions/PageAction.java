package com.xceptance.loadtest.api.actions;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Log;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.api.validators.Validator.StatusCodeValidator;
import com.xceptance.xlt.api.actions.RunMethodStateException;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Base class for all actions that perform a page load call.
 *
 * @author Xceptance Software Technologies
 */
public abstract class PageAction<T> extends com.xceptance.xlt.api.actions.AbstractHtmlPageAction
{
    private boolean precheckExecuted = false;
    private boolean precheckFailed = false;

    private final StatusCodeValidator statusCodeValidator = new Validator.StatusCodeValidator();

    /**
     * Create new action that is based on the previous action (if any).
     */
    public PageAction()
    {
        // Let this action base on the action before.
        this(Context.getCurrentAction());
    }

    /**
     * Create a new action that is based on the provided action.
     *
     * @param lastAction Action used as ancestor for the action to create
     */
    public PageAction(final com.xceptance.xlt.api.actions.AbstractHtmlPageAction lastAction)
    {
        super(lastAction, null);

        // Adjust action name
        final StringBuilder newTimerName = new StringBuilder();
        newTimerName.append(getTimerName());

        // Non page view actions are marked as such
        if (this instanceof NonPageView)
        {
            newTimerName.append("_NPV");
        }

        // Adjust action name if necessary, if we go with default and hence we don't rename with the
        // site id
        if ("default".equals(Context.getSite().id) == false)
        {
            newTimerName.append("_");
            newTimerName.append(Context.getSite().id);
        }

        // set new action name finally
        setTimerName(Context.get().debugData.adjustTimerName(() -> newTimerName.toString()));
    }

    /**
     * {@inheritDoc}
     *
     * Marked as final to prevent usage by accident.
     */
    @Override
    public final void execute() throws Exception
    {
        try
        {
            // Make THIS action the current one.
            Context.setCurrentActionINTERNAL(this);

            // Set the Basic Authentication header if necessary.
            Context.setBasicAuthenticationHeader();

            // Otherwise just execute it and break if a timeout is detected.
            doExecute();
        }
        finally
        {
            // fix the html context up in case we have been doing ajax
            if (this.getHtmlPage() == null)
            {
                final PageAction<?> previousAction = Context.getPreviousAction();
                if (previousAction != null && previousAction.getHtmlPage() != null)
                {
                    setHtmlPage(previousAction.getHtmlPage());
                }
            }
        }

        // Do the post execution steps.
        postExecute();
    }

    /**
     * Executes commands which needs to be done after the main execution (like
     * the analytics call).
     *
     * @throws Exception
     */
    protected void postExecute() throws Exception
    {
    	// Currently empty, override in sub class if required
    }
    
    /**
     * Executes the action's main part. What is done here is determined by the sub classes.
     *
     * @throws Exception If an error occurred while executing the action.
     */
    protected abstract void doExecute() throws Exception;

    /**
     * @deprecated Put all initialization lookups into {@link #precheck()}.
     */
    @Deprecated
    @Override
    public final void preValidate()
    {
        // Don't run the official preValidate due to the catching of all exceptions, which
        // is unfortunate and hides some problems, so we got our own with {@link #precheck()}.

        // You cannot use this method any longer!
    }

    /**
     * @deprecated {@link #safePrecheck()} will be called instead.
     */
    @Deprecated
    @Override
    public boolean preValidateSafe()
    {
        return safePrecheck();
    }

    /**
     * Checks the action conditions up front before actually executing ({@link #doExecute()}) the
     * action. Decides if the action can and should be executed.
     *
     * It is possible to already collect data here and save it as state, because the method will
     * only be called once by the framework.
     *
     * {@inheritDoc}
     */
    public void precheck()
    {
        // you can implement stuff here in your own subclass
    }

    /**
     * Calls {@link #precheck()} SAFELY. With this method we are checking action conditions up front
     * but without failing on an assertion occurrence.
     *
     * Intentionally, this method will not catch generic exceptions, but all AssertionError faults.
     *
     * @return true when we have no assertion errors, false otherwise
     */
    private boolean safePrecheck()
    {
        // check if we already executed the precheck, use cached result in that case
        if (precheckExecuted)
        {
            XltLogger.runTimeLogger.debug("# " + getTimerName() + " - precheck() was already called");
            return !precheckFailed;
        }

        precheckFailed = false;
        try
        {
            XltLogger.runTimeLogger.debug("# " + getTimerName() + " - precheck()");
            precheckExecuted = true;

            // run the official one that only does state tracking and
            // because preValidate() is empty, nothing is done or will happen
            super.preValidateSafe();

            // run ours
            precheck();
        }
        catch (final AssertionError ae)
        {
            // this error type is expected because we did not find what we were looking for
            Log.debugWhenDev("# {0} - precheck() failed: {1}", getTimerName(), ae);
            precheckFailed = true;
        }

        return !precheckFailed;
    }

    /**
     * Executes the test action (aka {@link #doExecute()}) method if and only if {@link #precheck()} was successful.<br>
     *
     * Please note that the return value <strong>does not</strong> indicate the success of the
     * {@link #doExecute()} method, but only indicates if the action was executed or not.
     *
     * @return <code>true</code> if {@link #preValidate()} was executed without problems, <code>false</code> otherwise.
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    public Optional<T> runIfPossible() throws Throwable
    {
        if (safePrecheck())
        {
            run();
            return Optional.of((T) this);
        }
        else
        {
            return Optional.empty();
        }
    }

    /**
     * Executes the action code.
     *
     * Does an initial call of method {@link #precheck()} (instead of the deprecated preValidate()).
     *
     * @throws Throwable
     */
    @Override
    public void run() throws Throwable
    {
        // we do not execute precheck twice when done runIfPossible() before, so this is safe here
        if (precheckExecuted == false)
        {
            precheck();
        }
        else
        {
            if (precheckFailed)
            {
                // if the previous precheck failed, we cannot continue, because we will not see
                // that error. So people might have called run() that accidentally.
                throw new RunMethodStateException(
                                "precheck() was already called in safe mode and failed. Check your test flow and do not call run() in case preValidateSafe() returned false.");
            }
        }

        super.run();
    }

    /**
     * Fancier version of run to be able to chain
     *
     * @return just a copy of myself
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    public T runAndGet() throws Throwable
    {
        this.run();
        return (T) this;
    }

    /**
     * Creates a link with provided URL as href and clicks it.
     *
     * @param url The URL for the new link
     * @throws Exception If an error occurred while loading the page
     */
    public void loadPageByUrlClick(final String url) throws Exception
    {
        final HtmlElement link = HtmlPageUtils.createHtmlElement("a", Page.getBody());
        link.setAttribute("href", url);

        loadPageByClick(link);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPage(final String urlAsString) throws Exception
    {
        super.loadPage(urlAsString);
        validateResponseQuickly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPage(final URL url) throws Exception
    {
        super.loadPage(url);
        validateResponseQuickly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPageByClick(final HtmlElement element) throws Exception
    {
        super.loadPageByClick(element);
        validateResponseQuickly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPageByFormSubmit(final HtmlForm form) throws Exception
    {
        super.loadPageByFormSubmit(form);
        validateResponseQuickly();
    }

    /**
     * By default the action will check that page loads return with status code 200. This can be
     * overwritten for the next page load if necessary.
     *
     * @param statusCodePattern Any regular expression that matches a response code
     * @return The configured action
     */
    protected PageAction<T> expectStatusCode(final String statusCodePattern)
    {
        statusCodeValidator.expect(statusCodePattern);
        return this;
    }

    /**
     * By default the action will check that page loads return with status code 200. This can be
     * overwritten for the next page load if necessary.
     *
     * @param statusCode A valid HTTP status code
     * @return The configured action
     */
    protected PageAction<T> expectStatusCode(final int statusCode)
    {
        statusCodeValidator.expect(statusCode);
        return this;
    }

    /**
     * Check the latest received page load status code against the expected one.<br>
     * This method also checks for a valid HTML end tag and logs additional information in case this
     * check fails.
     *
     * @throws IOException
     */
    private void validateResponseQuickly() throws IOException
    {
        statusCodeValidator.validate();
        Validator.validateHtmlEndTag();
    }
}