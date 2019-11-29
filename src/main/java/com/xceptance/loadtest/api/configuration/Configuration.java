package com.xceptance.loadtest.api.configuration;

import java.text.MessageFormat;

import org.junit.Assert;

import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.data.Address;
import com.xceptance.loadtest.api.data.CreditCard;
import com.xceptance.loadtest.api.data.PaymentLimitations;

/**
 * Configuration for the Test
 *
 * @author Rene Schwietzke (Xceptance Software Technologies GmbH)
 */
public class Configuration
{
    /**
     * The name of the current running TestCase' class
     */
    public LTProperties properties;

    // ===============================================================
    // Common / General
    //

    @Property(key = "general.url")
    public String siteUrlHomepage;

    // Basic authentication username
    @Property(key = "general.credentials.username", required = false)
    public String credentialsUserName;

    // Basic authentication password
    @Property(key = "general.credentials.password", required = false)
    public String credentialsPassword;

    // URL exclude filter patterns
    @Property(key = "com.xceptance.xlt.http.filter.exclude", required = false, fallback = "")
    public ConfigList excludeUrlPatterns;

    // URL include filter patterns
    @Property(key = "com.xceptance.xlt.http.filter.include", required = false, fallback = "")
    public ConfigList includeUrlPatterns;

    // Whether or not to make the analytics calls
    @Property(key = "general.load.analytics", fallback = "true")
    public boolean loadAnalytics;

    // Whether or not to make the Resources-Load calls
    @Property(key = "general.load.appresources", fallback = "false")
    public boolean loadAppResources;

    // Whether or not to make the ltk tracking calls
    @Property(key = "general.load.ltkTracking", fallback = "false")
    public boolean loadLtkTracking;

//    // Consent: Do we want to check consent?
//    @Property(key = "general.consent.ask")
//    public boolean consentAsk;

//    // Consent: What do we ansswer?
//    @Property(key = "general.consent.confirm")
//    public boolean consentConfirm;

    // URL: To start with the direct order scenario
    @Property(key = "general.direct.order.url")
    public String directOrderUrl;

    // ================ Email
    // Email domain
    @Property(key = "general.email.domain")
    public String emailDomain;

    @Property(key = "general.email.localpart.prefix")
    public String emailLocalPartPrefix;

    @Property(key = "general.email.localpart.length")
    public int emailLocalPartLength;

    // request gzipped resources?
    @Property(key = "com.xceptance.xlt.http.gzip")
    public boolean applyHeaderGzip;

    // dump long running request sessions
    @Property(key = "com.xceptance.xlt.output2disk.onResponseTime.largerThan", required = false, fallback = "0")
    public int dumpResponseTimesWhenLargerThan;

    /** in milliseconds */
    @Property(key = "general.request.longrunning.sessionFlag.threshold", required = false, fallback = "0")
    public int longRunningRequestThresholdForSessionMarking;

    // Optional additional debug UUID in user agent
    // TODO naming
    @Property(key = "general.execute.ontimeout.retry.max")
    public int onTimeoutRetryCount;

    // Allow the use of debug urls while developing
    @Property(key = "general.debug.urls")
    public boolean useDebugUrls;

    // Puts additional actions into the result browser
    // changes the random stream, hence for reproducing production
    // seeds, this has to be disabled
    @Property(key = "general.debug.actions")
    public boolean useDebugActions;

    // generate totally random emails by using the UUID generator
    // or when set to true, use the XltRandom generator to
    // create a reproducible stream of emails
    @Property(key = "general.email.stronglyRandom")
    public boolean stronglyRandomEmails;

    // ================================================================
    // Filter
    //
    @EnumProperty(key = "filter.product.url", clazz = String.class, from = 0, to = 100, stopOnGap = false, required = false)
    public EnumConfigList<String> filterProductUrls;

    @EnumProperty(key = "filter.category.url", clazz = String.class, from = 0, to = 100, stopOnGap = false, required = false)
    public EnumConfigList<String> filterCategoryUrls;

    // ================================================================
    // Flash
    //
    @EnumProperty(key = "flash.product.urls", clazz = String.class, from = 0, to = 100, stopOnGap = false, required = true)
    public EnumConfigList<String> flashUrls;

    // refresh timer
    @Property(key = "flash.refreshTime")
    public int flashRefreshTime;

    // Flash start time
    @Property(key = "flash.startTime")
    public int flashStartTime;

    // list of OCAPI product ID's
    @EnumProperty(key = "flash.ocapi_product.id", clazz = String.class, from = 0, to = 100, stopOnGap = false, required = true)
    public EnumConfigList<String> flashIDs;

    // ================================================================
    // Search
    //
    // range of product searches
    @Property(key = "search.count", immutable = false)
    public ConfigRange searchesCount;

    // Whether or not to load search suggestions
    @Property(key = "search.hitTermsFile")
    public String searchHitTermsFile;

    // Whether or not to load search suggestions
    @Property(key = "search.loadSuggestions")
    public boolean searchSuggestionsEnabled;

    // Probability to execute a 'no-hits' search
    @Property(key = "search.noHits", immutable = false)
    public ConfigProbability searchNoHitsProbability;

    // A list of search params which should result in a no hits page.
    @EnumProperty(key = "search.noHitsTerms", clazz = String.class, from = 0, to = 100, stopOnGap = false, required = false, immutable = false)
    public EnumConfigList<String> searchNoHitsTerms;

    // Shall we try to bypass pagecache for searches
    @Property(key = "search.cacheBusting")
    public boolean searchCacheBusting;

    // How many different variations of the dynamic part do we want?
    @Property(key = "search.cacheBusting.count")
    public int searchCacheBustingCount;

    // Probability to execute an article search
    @Property(key = "search.deriveNewPhrases", immutable = false)
    public ConfigProbability searchNewPhraseDeriveProbability;

    // ==========================================================
    // Browsing
    //

    // What is the regular size of PLPs aka amount of tiles. Helps to select
    // PDPs from tile pages better. Not mandatory to be accurate.
    @Property(key = "browsing.plp.tiles.count", immutable = true)
    public int numberOfPLPTiles;

    // Select which switch statement should be used in the product listing pages flow
    @Property(key = "browsing.plp.flow.selector", required = false, fallback = "-1")
    public int plpFlowSelector;

    // How often do we want to walk the catalog path from the top
    // including refinemnets
    @Property(key = "browsing.flow")
    public ConfigRange fullBrowseFlow;

    // How often do we need the categories touched per browsing flow
    // before refining
    @Property(key = "browsing.flow.categories.flow")
    public ConfigRange browseCategoriesFlow;

    // HHow often do we refine within the larger browse flow per round
    @Property(key = "browsing.flow.refine.flow")
    public ConfigRange browseRefineFlow;

    // refine only when enough products are shown, so avoid refining against
    // single results and such things
    @Property(key = "browsing.refine.products.minimumCountShown")
    public ConfigRange refinementMinimumProductCount;

    // Top category browsing probability
    @Property(key = "browsing.category.top", immutable = false)
    public ConfigProbability topCategoryBrowsing;

    // Probability for attribute refinements
    @Property(key = "browsing.refine", immutable = false)
    public ConfigProbability refinementProbability;

    // Probability for category refinements
    @Property(key = "browsing.refine.category", immutable = false)
    public ConfigProbability categoryRefinementProbability;

    // Probability for sorting
    @Property(key = "browsing.sorting", immutable = false)
    public ConfigProbability sortingProbability;

    // Probability for display more
    @Property(key = "browsing.displaymore", immutable = false)
    public ConfigProbability displayMoreProbability;

    // shall we do infinite scrolling?
    @Property(key = "browsing.infinitescroll")
    public boolean useInfiniteScroll;

    // Minimum number of products to view when viewing product details
    @Property(key = "browsing.product.view.count", immutable = false)
    public ConfigRange productViewCount;

    // Probability for accessing a product via quick view
    @Property(key = "browsing.product.quickview.view", immutable = false)
    public ConfigProbability quickViewProbability;

    // When we do quick views, how often shall we do them
    @Property(key = "browsing.product.quickview.view.count", immutable = false)
    public ConfigRange quickViewCount;

    // ===========================================================
    // Cart
    //

    // Probability to execute a 'search' instead of using the navigation menu.
    @Property(key = "cart.search", immutable = false)
    public ConfigProbability searchOnAddToCartProbability;

    // Add to cart as distribution
    @Property(key = "cart.add.count")
    public ConfigDistribution addToCartCount;

    // How often should the cart be shown after an add to cart
    @Property(key = "cart.view", immutable = false)
    public ConfigProbability viewCartProbability;

    // How often should the cart be shown after an add to cart
    @Property(key = "cart.minicart.view", immutable = false)
    public ConfigProbability viewMiniCartProbability;

    // Do we need a counter for add2cart and view cart, mainly for performance
    // debugging
    @Property(key = "cart.report.bySize")
    public boolean reportCartBySize;

    // how many product do we want per add to cart?
    @Property(key = "cart.product.quantity", immutable = false)
    public ConfigRange cartProductQuantity;

    // =========================================================
    // Account
    //

    // where should we take it from
    @Property(key = "account.source", required = true)
    public String accountSource;

    // A list of accounts
    @EnumProperty(key = "account", clazz = Account.class, from = 0, to = 20, stopOnGap = false)
    public EnumConfigList<Account> accounts;

    // A list of addresses to select from, this can grown if needed
    @EnumProperty(key = "addresses", clazz = Address.class, from = 0, to = 20, stopOnGap = false)
    public EnumConfigList<Address> addresses;

    @Property(key = "account.predefined.file", required = false)
    public String predefinedAccountsFile;

    // Account Pool
    //

    // Whether or not to separate account pools
    @Property(key = "account.pool.separator")
    public String accountPoolSiteSeparator;

    // Pools size
    @Property(key = "account.pool.size")
    public int accountPoolSize;

    // Probability to reuse an account
    // (aka, everything else does not make it to the pool)
    @Property(key = "account.pool.reuse")
    public ConfigProbability accountPoolReuseProbability;

    // ===========================================================
    // Payment
    //

    @EnumProperty(key = "creditcards", clazz = CreditCard.class, from = 0, to = 100, stopOnGap = true)
    public EnumConfigList<CreditCard> creditcards;

    @EnumProperty(key = "paymentlimitations", clazz = PaymentLimitations.class, from = 0, to = 10, stopOnGap = true)
    public EnumConfigList<PaymentLimitations> paymentLimitations;

    // ===========================================================
    // Store
    //
    @Property(key = "store.search.count", immutable = false)
    public ConfigDistribution storeSearches;

    // ===========================================================
    // Special Scenarios
    //
    @Property(key = "extra.revisitWaitingTime", immutable = false)
    public ConfigRange revisitWaitingTime;

    // ===========================================================
    // All data files to be used... this is all for sites aka with hierarchy lookup
    //
    @Property(key = "data.file.firstNames")
    public String dataFileFirstNames;

    @Property(key = "data.file.lastNames")
    public String dataFileLastNames;

    // ===========================================================
    // OCAPI
    //
//    @Property(key = "general.ocapi.contentType")
//    public String ocapiContentType;

//    @Property(key = "general.ocapi.charset")
//    public String ocapiCharset;

    // OCAPI Shop API
//
//    @Property(key = "general.ocapi.url")
//    public String ocapiUrl;

//    @Property(key = "general.ocapi.clientId")
//    public String ocapiClientId;
//
//    @Property(key = "general.ocapi.eTagHandling")
//    public boolean ocapiEtagHandling;
//
//    @Property(key = "general.ocapi.resourceStateHandling")
//    public boolean ocapiResourceStateHandling;

    // ===========================================================
    // CPT

    // Number of URLs called on execution of CPT test case TVisitRandom
    @Property(key = "cpt.urls.count", immutable = false)
    public ConfigRange urlCount;

    /**
     * Return text from the localization section, fails if the text is not available
     *
     * @param key
     *            the key to search for including hierarchy excluding "localization."
     * @return the found localized
     */
    public String localizedText(final String key)
    {
        final String result = properties.getProperty("localization." + key);
        if (result == null)
        {
            // no result, we fail to be safe for the setup of tests
            Assert.fail(MessageFormat.format("Localization key {0} not found", key));
        }

        return result;
    }

    /**
     * Returns the properties that are current for this context and the source of this
     * configuration. You can also directly access them, if you like.
     *
     * @return the property set
     */
    public LTProperties getProperties()
    {
        return properties;
    }

    /**
     * Constructor
     */
    public Configuration()
    {
    	super();
    }
}