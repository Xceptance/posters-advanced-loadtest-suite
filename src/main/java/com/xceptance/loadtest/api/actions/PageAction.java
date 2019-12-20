package com.xceptance.loadtest.api.actions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.events.EventLogger;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.Log;
import com.xceptance.loadtest.api.util.PageViewCounter;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.api.validators.Validator.StatusCodeValidator;
import com.xceptance.xlt.api.actions.RunMethodStateException;
import com.xceptance.xlt.api.engine.CustomValue;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.ResponseProcessor;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Base class for all actions that perform a page load call.
 *
 * @author Xceptance Software Technologies
 */
public abstract class PageAction<T> extends com.xceptance.xlt.api.actions.AbstractHtmlPageAction
{
    /**
     * Is the debug mode on and shall we prevent certain calls
     */
    private boolean debugCallWasAlreadyMade = false;

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

        /*
         * Sometimes the received response needs to be modified. This can be done easily by response processors. Find
         * here some examples that
         */

        // Drop 'iframe' sections.
        // addResponseProcessor(new ResponseContentProcessor("<noscript\\s*>.*?</noscript>", ""));

        // Drop 'noscript' sections.
        // addResponseProcessor(new ResponseContentProcessor("<iframe[^>]+>", "<iframe>"));

        // Drop ISML tags (Use this workaround for script DEVELOPMENT only)
        // addResponseProcessor(new ResponseContentProcessor("<is.*?>", ""));
        // addResponseProcessor(new ResponseContentProcessor("</is.*?>", ""));

        // addResponseProcessor(new ResponseContentProcessor("selected=\"\"",
        // "selected"));

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

    @Override
    public void addResponseProcessor(final ResponseProcessor processor)
    {
        if (Session.getCurrent().isLoadTest())
        {
            throw new IllegalStateException("It's strongly recommended to avoid ReponseProcessors in load test mode.");
        }
        else
        {
            EventLogger.DEFAULT.warn("ResponseProcessor", "It's strongly recommended to avoid ReponseProcessors in load test mode.");
            super.addResponseProcessor(processor);
        }
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

            // If a retry on timeout is configured execute the action in a more
            // relaxed way.
            final int retryCount = Context.configuration().onTimeoutRetryCount;
            if (retryCount > 0)
            {
                doExecuteTolerateTimeout(retryCount);
            }
            else
            {
                // Otherwise just execute it and break if a timeout is detected.
                doExecute();
            }
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
            PageViewCounter.count(this);
        }

        // Do the post execution steps.
        postExecute();
    }

    /**
     * Execute the commands. If a timeout is detected the commands will be repeated. If the repetitions have reached the
     * given maximum the method just returns. A SocketTimeoutException will never be thrown.
     *
     * @param retryCount How many repetitions are allowed at maximum.
     * @throws Exception In case of an error but a SocketTimeoutException
     */
    private void doExecuteTolerateTimeout(final int retryCount) throws Exception
    {
        // Repeat if necessary
        for (int i = 0; i < retryCount; i++)
        {
            try
            {
                // Execute the action.
                doExecute();

                // No timeout, no retry. We are done.
                break;
            }
            catch (final SocketTimeoutException e)
            {
                // Nothing to do. Catch it and continue. Action will be executed again if possible.
            }
            catch (final RuntimeException rte)
            {
                // Check for the wrapped exception. Do not throw a SocketTimeoutException.
                final Throwable cause = rte.getCause();
                if (cause == null || cause.getClass() != SocketTimeoutException.class)
                {
                    // Propagate it.
                    throw rte;
                }
            }

            // If we reach that, we've caught the exception and will try again. Log it.
            EventLogger.DEFAULT.debug("TimeoutRetry",
                             MessageFormat.format("{0} - Try: {1} failed.", this.getTimerName(), (i + 1)));
        }
    }

    /**
     * Helper for debugging scripts more easily with fixed urls Only work when
     * turned on and in development mode
     *
     * @param url Relative or absolute debug url
     * @return this instance
     */
    public PageAction<T> loadDebugUrlOrElse(final String url) throws Exception
    {
        if (Context.isLoadTest)
        {
            return this; // nothing to do
        }

        if (Context.configuration().useDebugUrls == false)
        {
            return this;
        }

        // ok, let's load our debug url
        final HtmlPage currentPage = Context.getPage();

        // our future url
        URL absoluteUrl;

        if (currentPage != null)
        {
            absoluteUrl = currentPage.getFullyQualifiedUrl(url);
        }
        else
        {
            absoluteUrl = new URL(url);
        }

        loadPage(absoluteUrl); // load our debug url
        debugCallWasAlreadyMade = true; // prevent further loading

        return this;
    }

    /**
     * Executes Commands which needs to be done after the main execution (like
     * the analytics call).
     *
     * @throws Exception
     */
    protected void postExecute() throws Exception
    {
        handleLongRunningRequests();
    }

    protected void handleLongRunningRequests()
    {
        // just for efficiency, so we only have to get this once
        List<Long> loadTimes = null;

        // if the session is known as long-runner-session already we can skip
        if (Context.get().data.isSessionWithLongRunningRequest == false)
        {
            // do we want to mark sessions with long runners at all?
            final int threshold = Context.configuration().longRunningRequestThresholdForSessionMarking;
            if (threshold > 0)
            {
                loadTimes = getRequestRuntimes();

                // do we find a long runner finally?
                if (loadTimes.stream().anyMatch(loadTime -> loadTime > threshold))
                {
                    // log a custom value and flag the session
                    final Session session = Session.getCurrent();

                    final CustomValue customValue = new CustomValue("LongRunnerCount - " + session.getUserName());
                    final CustomValue customValueAll = new CustomValue("LongRunnerCount - ALL");

                    customValue.setValue(1);
                    customValueAll.setValue(1);

                    session.getDataManager().logDataRecord(customValue);
                    session.getDataManager().logDataRecord(customValueAll);

                    Context.get().data.isSessionWithLongRunningRequest = true;
                }
            }
        }

        // do we want to dump on long runners?
        final int dumpResponseTimesWhenLargeThreshold = Context.configuration().dumpResponseTimesWhenLargerThan;
        if (dumpResponseTimesWhenLargeThreshold > 0)
        {
            // skip newly initialization of load times list if possible
            loadTimes = loadTimes == null ? getRequestRuntimes() : loadTimes;

            // do we find a long runner that's worth to get dumped?
            if (loadTimes.stream().anyMatch(loadTime -> loadTime > dumpResponseTimesWhenLargeThreshold))
            {
                throw new RuntimeException("Response time higher than " + (dumpResponseTimesWhenLargeThreshold / 1000) + " sec");
            }
        }
    }

    private List<Long> getRequestRuntimes()
    {
        // extract load times
        final List<Long> loadTimes = Session.getCurrent().getNetworkDataManager().getData()
                        .stream()
                        .map(request -> request.getResponse().getLoadTime())
                        .collect(Collectors.toList());

        return loadTimes;
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
        if (!debugCallWasAlreadyMade)
        {
            super.loadPage(urlAsString);
            validateResponseQuickly();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPage(final URL url) throws Exception
    {
        if (!debugCallWasAlreadyMade)
        {
            super.loadPage(url);
            validateResponseQuickly();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPageByClick(final HtmlElement element) throws Exception
    {
        if (!debugCallWasAlreadyMade)
        {
            super.loadPageByClick(element);
            validateResponseQuickly();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPageByFormSubmit(final HtmlForm form) throws Exception
    {
        if (!debugCallWasAlreadyMade)
        {
            super.loadPageByFormSubmit(form);
            validateResponseQuickly();
        }
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