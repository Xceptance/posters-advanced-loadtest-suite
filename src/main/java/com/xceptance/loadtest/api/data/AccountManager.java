package com.xceptance.loadtest.api.data;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.data.DataPool;

/**
 * Account manager. 
 *
 * @author Xceptance Software Technologies
 */
public class AccountManager
{
    /**
     * Our managers by key (which is mostly site or a grouping of sites)
     */
    private static final ConcurrentHashMap<String, AccountManager> managers = new ConcurrentHashMap<>();

    /**
     * Available accounts pool.
     */
    private final DataPool<Account> accounts;

    /**
     * The account pool is initialized with a configured size and expiration rate. In this case the account reusage
     * probability is the base to determine the expiration rate. If not set, all accounts will expire.
     */
    private AccountManager()
    {
        // Initial data pool with configured pool size and expiration rate to control percentage of reusable accounts.
        final int size = Context.configuration().accountPoolSize;
        final int reusageProbability = Context.configuration().accountPoolReuseProbability.raw;

        accounts = new DataPool<>(size, 100 - reusageProbability);
    }

    /**
     * Get an account.
     *
     * @return an account or an empty optional if no account is available
     */
    public Optional<Account> getAccount()
    {
        final Account account = accounts.getDataElement();
        if (account != null)
        {
            account.origin = AccountOrigin.POOL;
        }
        return Optional.ofNullable(account);
    }

    /**
     * Adds the given account to the pool. Will complain if the account is empty
     *
     * @param account
     *            an account
     */
    public void addAccount(final Optional<Account> account)
    {
        accounts.add(account.get());
    }

    /**
     * Get us an account, either from the pool if any in the pool or a fresh one according to the
     * config of the context we are in.
     *
     * @return new account as optional
     */
    public static Optional<Account> aquireAccount()
    {
        if (Context.requiresRegisteredAccount())
        {
            final AccountManager mgr = getInstance(Context.configuration().accountPoolSiteSeparator);

            return Optional.of(mgr.getAccount().orElse(AccountSupplierManager.getAccount()));
        }
        else
        {
            return Optional.of(AccountSupplierManager.getAccount());
        }
    }
    
    /**
     * Get us an account from a file. 
     * @param exclusive determine if the account should be exclusive or not
     * @return new account as optional, Optional.empty() otherwise
     */
    public static Optional<Account> aquireAccountFromFile(boolean exclusive)
    {
        if (Context.requiresRegisteredAccount())
        {
            if (exclusive)
            {
                return Optional.of(AccountSupplierManager.getExclusiveAccount().get());
            }
            else
            {
                return Optional.of(AccountSupplierManager.getFromFile().get());
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the account to the pool, or add it back to the ExculsiveDataProvider. It will return and empty optional as kind of confirmation
     *
     * @param account
     *            account to return, will fail of the account was empty
     * @return an empty optional
     * @throws Exception 
     */
    public static Optional<Account> returnAccount(final Optional<Account> account) throws Exception
    {
        if (!AccountOrigin.FILE.equals(account.get().origin) &&
            !AccountOrigin.EXCLUSIVE.equals(account.get().origin))
        {
            final AccountManager mgr = getInstance(Context.configuration().accountPoolSiteSeparator);
            mgr.addAccount(account);
        }
        if (AccountOrigin.EXCLUSIVE.equals(account.get().origin))
        {
            DataSupplier.releaseExclusiveAccount(account);
        }
        
        return Optional.empty();
    }

    /**
     * Returns the account manager mapped to the given key. Use {@link #getInstance()} instead if a
     * single globally shared account manager is all you need.
     *
     * @param key
     *            The key the account manager is bound to. If the key is <code>null</code> the
     *            unmapped global account manager is returned.
     * @return account manager instance
     */
    public static AccountManager getInstance(final String key)
    {
        Assert.assertNotNull("Key for the account manager is required", key);

        // Lookup existing manager for given key or create new one if required.
        return managers.computeIfAbsent(key, k -> new AccountManager());
    }
}