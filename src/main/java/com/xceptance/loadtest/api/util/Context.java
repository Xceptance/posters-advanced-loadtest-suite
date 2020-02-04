package com.xceptance.loadtest.api.util;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.configuration.Configuration;
import com.xceptance.loadtest.api.configuration.ConfigurationBuilder;
import com.xceptance.loadtest.api.configuration.DefaultConfiguration;
import com.xceptance.loadtest.api.configuration.LTProperties;
import com.xceptance.loadtest.api.configuration.YamlPropertiesBuilder;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.CustomDataLogger;
import com.xceptance.loadtest.api.data.DebugData;
import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.data.TestData;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.common.XltConstants;

/**
 * Central component for configuration, current page, last action and all test related data.
 *
 * @author Bernd Weigel (Xceptance Software Technologies GmbH)
 */
public class Context
{
    /**
     * Known test contexts.
     */
    private static final Map<ThreadGroup, Context> CONTEXTS = new ConcurrentHashMap<>(101);

    /**
     * The Configuration for the current thread, wrapped and buffered from the properties.
     */
    public final Configuration configuration;

    /**
     * The last executed action.
     */
    private PageAction<?> previousAction;

    /**
     * The action which is currently executed.
     */
    private PageAction<?> currentAction;

    /**
     * Test data of the current execution to better isolate that from the context
     */
    public final TestData data = new TestData();

    /**
     * Holds debug data for development
     */
    public final DebugData debugData = DebugData.getInstance();

    // Keep a quickly accessible info that we are a load test run
    public static final boolean isLoadTest = Session.getCurrent().isLoadTest();

    /**
     * Constructor; Creates a new Context for a TestCase.
     *
     * @param xltProperties
     *            the initial set of XLT properties
     * @param userName
     *            the currently running user
     * @param fullTestClassName
     *            test name
     * @param site
     *            site context
     */
    private Context(final XltProperties xltProperties,
                    final String userName,
                    final String fullTestClassName,
                    final Site site)
    {
        // where we get the props from later in this code
        final LTProperties totalProperties = new LTProperties(userName, fullTestClassName, site.id);

        // xlt properties come first aka the last line of defense for the later look up
        totalProperties.addProperties(Optional.of(xltProperties.getProperties()));

        // initialize the config and log the time needed
        final CustomDataLogger cdl = CustomDataLogger.start("config.build.testcase");
        {
            // ### Get us sites.yaml and other more global structured properties first

            // ok, we have custom properties in YAML, get them before we do the configuration magic
            final String fileNames = xltProperties.getProperty("general.properties.yaml.global.files", "");
            for (final String fileName : fileNames.split("\\s|,|;"))
            {
                // don't try empty
                if (fileName.trim().length() > 0)
                {
                    final Optional<Properties> newProperties = YamlPropertiesBuilder.build(site.id, fileName);
                    if (newProperties.isPresent())
                    {
                        totalProperties.addProperties(newProperties);
                    }
                }
            }

            // now, the real fun starts, we need the rest of the properties
            final String siteFileNames = xltProperties.getProperty("general.properties.yaml.site.files", "");
            for (final String fileName : siteFileNames.split("\\s|,|;"))
            {
                // don't try empty
                if (fileName.trim().length() > 0)
                {
                    final Optional<Properties> newProperties = YamlPropertiesBuilder.buildWithFallback(site, fileName);
                    totalProperties.addProperties(newProperties);
                }
            }

            // dump all for debugging
            Log.debugWhenDev("{0}", totalProperties);

            // get XLT all our look up data so it is also up to date
            XltProperties.getInstance().setProperties(totalProperties.properties);

            // now, we can do what we always do, because all YAML stuff is available as regular
            // properties
            this.configuration = new ConfigurationBuilder(totalProperties).build(Configuration.class);
        }
        cdl.stopAndLog();

        // keep them for later
        this.configuration.properties = totalProperties;
        this.data.setSite(site);
    }

    /**
     * Gets the current configuration for this TestCase.
     *
     * @return the current configuration for this TestCase
     */
    public static Configuration configuration()
    {
        return get().configuration;
    }

    /**
     * Retrieves the context instance for the current Thread.
     *
     * @return the context instance for the current Thread
     */
    public static Context get()
    {
        final Context context = CONTEXTS.get(Thread.currentThread().getThreadGroup());

        if (context == null)
        {
            XltLogger.runTimeLogger.error("No context available for this thread.");
            Assert.fail("Context initialization problem. We need one, we don't have one.");
        }

        return context;
    }

    /**
     * Closes the browser. Will also remove all cookies! It does not clean up our internal data
     * state of the test case!!!
     */
    public static void closeBrowser()
    {
        Context.getPreviousAction().closeWebClient();
        Context.setCurrentActionINTERNAL(null);
    }

    /**
     * Adds a new Context instance for the current Thread to the map. This Method is used by the
     * AbstractTestCase and therefore won't need to be called manually
     *
     * @param fullTestClassName
     *            the name of the test case for property identification
     * @param site
     *            site context
     */
    public static void createContext(final XltProperties xltProperties, final String userName, final String fullTestClassName, final Site site)
    {
        // NOTE: previous added Context instances for this Thread will be ignored
        CONTEXTS.put(
                        Thread.currentThread().getThreadGroup(),
                        new Context(xltProperties, userName, fullTestClassName, site));
    }

    /**
     * Gets the action which is currently executed.
     *
     * @return the current action
     */
    public static PageAction<?> getCurrentAction()
    {
        return get().currentAction;
    }

    /**
     * Sets the action which is currently executed. Just a wrapper method for the static call.
     *
     * @param action
     *            the current action
     */
    private void setCurrentActionInternal(final PageAction<?> action)
    {
        this.previousAction = this.currentAction;
        this.currentAction = action;
    }

    /**
     * Gets the action which was executed before the current one.
     *
     * @return the previous action if any or <code>null</code>
     */
    public static PageAction<?> getPreviousAction()
    {
        return get().previousAction;
    }

    /**
     * Sets the action which was executed last.
     *
     * @param action
     *            the current action
     */
    public static void setCurrentActionINTERNAL(final PageAction<?> action)
    {
        get().setCurrentActionInternal(action);
    }

    /**
     * Reset the last action with an older one. Make sure you also carry over
     * the URL, otherwise calls might not be executed. This can be used for a
     * browser back emulation.
     *
     * @param action
     *            the current action
     */
    public static void resetCurrentAction(final PageAction<?> action)
    {
        get().setCurrentActionInternal(action);

        // sync the web client in terms of current url
        if (action.getHtmlPage() != null)
        {
            action.getWebClient().getCurrentWindow().setEnclosedPage(action.getHtmlPage());
        }
    }

    /**
     * Gets the current page.
     *
     * @return the current page
     */
    public static HtmlPage getPage()
    {
        final HtmlPage page = getCurrentAction().getHtmlPage();
        return page != null ? page : getPreviousAction() == null ? null : getPreviousAction().getHtmlPage();
    }

    /**
     * Overrides the current page if it was loaded or changed externally.
     *
     * @param page
     *            the current page object
     */
    public static void setCurrentPage(final HtmlPage page)
    {
        getCurrentAction().setHtmlPage(page);
    }

    /**
     * Releases the context for the current thread.
     */
    public static void releaseContext()
    {
        // log data that is used typically to re run a test under same conditions
        try
        {
            get().logRerunData();
        }
        catch (final Throwable e)
        {
            XltLogger.runTimeLogger.error("Error during debug info logging", e);
        }

        // release the account (if it applies it's put back into the account pool)
        try
        {
            get().data.releaseAccount();
        }
        catch (final Throwable e)
        {
            XltLogger.runTimeLogger.error("Error during release of account", e);
        }

        // log the information about sessions with long running requests
        try
        {
            PageViewCounter.publishAndDestroy();
        }
        catch (final Throwable e)
        {
            XltLogger.runTimeLogger.error("Error while trying to publishAndDestroy the pageviewcounter", e);
        }

        // remove the context finally
        CONTEXTS.remove(Thread.currentThread().getThreadGroup());
    }

    /**
     * Executed before the release of the context. Includes handling for additional debug logging.
     */
    private void logRerunData()
    {
        // Do for non-load-test mode only.
        if (isLoadTest == false)
        {
            final StringBuilder out = new StringBuilder(1024);

            // Test rerun information.
            out.append("\n\nIf you want to rerun this testcase, insert the following lines into your config/dev.properties file: \n\n");

            // Account information
            final Optional<Account> attachedAccount = data.getAccount();
            if (attachedAccount.isPresent())
            {
                final Account account = attachedAccount.get();

                final String siteId = getSite().getId();
                out.append("account.").append(siteId).append(".email = ").append(account.email).append("\n");
                out.append("account.").append(siteId).append(".password = ").append(account.password).append("\n");
                out.append("account.").append(siteId).append(".isRegistered = ").append(account.isRegistered).append("\n");
            }

            // Randomizer initialization value.
            out.append(XltConstants.RANDOM_INIT_VALUE_PROPERTY).append(" = ").append(XltRandom.getSeed()).append("\n\n");

            if (Boolean.parseBoolean(configuration().properties.getProperty("randomInitValueWasSet")))
            {
                out.append("Testcase was configured with that random value\n\n");
            }

            // Print the collected information.
            XltLogger.runTimeLogger.info(out.toString());
        }
    }

    /**
     * Initialize a registered customer test case.
     */
    public static void requiresRegisteredAccount(final boolean isRegistered)
    {
        Context.get().data.requiresRegisteredAccount = isRegistered;
    }

    /**
     * Runs the current test case a registered customer scenario?
     *
     * @return <code>true</code> if the current test case a registered customer scenario, <code>false</code> otherwise
     */
    public static boolean requiresRegisteredAccount()
    {
        return Context.get().data.requiresRegisteredAccount;
    }

    /**
     * Get the site context.
     *
     * @return the site context
     */
    public static Site getSite()
    {
        return Context.get().data.getSite();
    }


    /**
     * Set the Basic Authentication header if credentials are configured.
     */
    public static void setBasicAuthenticationHeader()
    {
        // get context
        final Context context = get();

        // Is a user name for Basic Authentication configured?
        final String username = context.configuration.credentialsUserName;
        if (StringUtils.isNotBlank(username))
        {
            // It is. So let's get the password.
            final String password = context.configuration.credentialsPassword;

            // Set the request header.
            setBasicAuthenticationHeader(username, password);
        }
    }

    /**
     * Set the Basic Authentication header with given credentials.
     * {@link #setBasicAuthenticationHeader()} is to be preferred over this
     * method if possible.
     *
     * @param username
     *            must not be blank, otherwise method will act like a noop.
     * @param password
     */
    public static void setBasicAuthenticationHeader(final String username, final String password)
    {
        // Is a user name for Basic Authentication configured?
        if (StringUtils.isNotBlank(username))
        {
            // Set the request header.
            final String userPass = username + ":" + password;
            final String userPassBase64 = Base64.encodeBase64String(userPass.getBytes());

            getCurrentAction().getWebClient().addRequestHeader("Authorization", "Basic " + userPassBase64);
        }
    }

    /////////////////////////////////////////////////////////////////
    // Load Default Configuration
    /////////////////////////////////////////////////////////////////

    public static ThreadLocal<DefaultConfiguration> defaultConfiguration = new ThreadLocal<DefaultConfiguration>()    
    {
    	private DefaultConfiguration defaultConfiguration;
    	
    	@Override
        public DefaultConfiguration get()
        {
            if (defaultConfiguration == null)
            {
                defaultConfiguration = Context.loadDefaultConfiguration();
            }
            return defaultConfiguration;
        }

        @Override
        public void remove()
        {
            defaultConfiguration = null;
        }
    };

    /**
     * Loads the default non test case dependent configuration. Usually very short. Helps to do
     * something outside of the regular context
     *
     * @return the default configuration
     */
    private static DefaultConfiguration loadDefaultConfiguration()
    {
    	// save random seed
        final long savedSeed = XltRandom.getSeed();
    	
        // where we get the props from later in this code
        final LTProperties totalProperties = new LTProperties("", "", "");

        // xlt properties come first aka the last line of defense for the later look up
        totalProperties.addProperties(Optional.of(XltProperties.getInstance().getProperties()));

        // that's what we want
        DefaultConfiguration defaultConfiguaration = null;

        // initialize the config and log the time needed
        final CustomDataLogger cdl = CustomDataLogger.start("config.build.default");
        {
            // ### Get us sites.yaml and other more global structured properties first

            // ok, we have custom properties in YAML, get them before we do the configuration magic
            final String fileNames = XltProperties.getInstance().getProperty("general.properties.yaml.global.files", "");
            for (final String fileName : fileNames.split("\\s|,|;"))
            {
                // don't try empty
                if (fileName.trim().length() > 0)
                {
                    final Optional<Properties> newProperties = YamlPropertiesBuilder.build("DEFAULTCONFIGUATION", fileName);
                    if (newProperties.isPresent())
                    {
                        totalProperties.addProperties(newProperties);
                    }
                }
            }

            // now, we can do what we always do, because all YAML stuff is available as regular
            // properties
            defaultConfiguaration = new ConfigurationBuilder(totalProperties).build(DefaultConfiguration.class);
        }
        
        cdl.stopAndLog();
        
        // restore seed for proper reproducibility
        XltRandom.setSeed(savedSeed);

        // keep them for later
        return defaultConfiguaration;
    }
}
