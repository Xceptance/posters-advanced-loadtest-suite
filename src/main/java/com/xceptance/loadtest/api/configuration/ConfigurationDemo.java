package com.xceptance.loadtest.api.configuration;

import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.xlt.api.data.Account;

/**
 * Configuration for the Test
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class ConfigurationDemo
{
    /**
     * Will be looked up as search.hits.misses.list
     */
    @Property(key = "search.hits.misses", delimiters = ",", fallback = "foo,bar")
    public ConfigList noHitSearchParams;

    @EnumProperty(clazz = Account.class, key = "account", from = 1, to = 5, stopOnGap = false, required = true, compact = true, immutable = true)
    public EnumConfigList<Account> accountList;

    // string has no automcomplete
    @Property(key = "test.class")
    public String testClassName;

    // not extended, fallback can only be range definition
    @Property(key = "search.products", fallback = "1-2", autocomplete = false)
    public ConfigRange searchProducts;

    // extended by count
    @Property(key = "search", fallback = "10")
    public int search;

    // extended by probability
    @Property(key = "refinement.catalog", fallback = "50")
    public ConfigProbability catalogRefinement;

    @Property(key = "refinement.catalog", fallback = "true")
    public boolean catalogRefinementEnabled;

    @Property(key = "refinement.catalog", fallback = "true")
    public boolean catalogRefinementEnabled2;

    // extends to distribution
    // the random data access part is blocked and it will always return
    // the same value when asked
    @Property(key = "cart.size", fallback = "10/10 20/10 50/1", immutable = false)
    public ConfigDistribution cartSize;

    @Property(key = "cart.size", required = true)
    public int cartSizeCount;
}

