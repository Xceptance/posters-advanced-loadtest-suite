package com.xceptance.loadtest.api.data;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.YamlPropertiesBuilder;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.util.PageTest;
import com.xceptance.xlt.api.data.DataPool;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.XltConstants;

import util.TestUtils;

public class AccountOriginTest
{
    @BeforeClass
    public static void initClass() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        properties.setProperty(XltConstants.XLT_PACKAGE_PATH + ".data.directory", "./src/test/resources/data");
        // reset properties (might be cached
        final Class<?> holder = Class.forName(Context.class.getName() +
                        "$DefaultConfigurationLazyHolder");
        TestUtils.setStaticFieldValue(holder, "INSTANCE",
                        TestUtils.invokeStaticMethod(Context.class, "loadDefaultConfiguration"));
        TestUtils.setStaticFieldValue(YamlPropertiesBuilder.class, "propertiesCache", new ConcurrentHashMap<>());
    }

    @After
    public void after()
    {
        Context.releaseContext();
    }

    @Test
    public void testRandomAccount()
    {
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());

        final Account account = Context.get().data.attachAccount().get();
        Assert.assertEquals("Default Account should come from origin RANDOM", AccountOrigin.RANDOM, account.origin);
    }

    @Test
    public void testPropertiesAccountOrigin()
    {
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("US").get());

        Assert.assertEquals("Account does not match preconfigured.", "US", Context.get().data.attachAccount().get().shippingAddress.countryCode);
        Assert.assertEquals("Account does not match preconfigured.", "120 Properties Way", Context.get().data.getAccount().get().shippingAddress.addressLine1);
        Assert.assertEquals("Account does not match preconfigured.", "propertiestest@varmail.de", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Account does not match preconfigured.", "Pro", Context.get().data.getAccount().get().firstname);
        Assert.assertEquals("Account does not match preconfigured.", "Perties", Context.get().data.getAccount().get().lastname);
        Assert.assertEquals("Default Account should come from origin PROPERTIES.", AccountOrigin.PROPERTIES, Context.get().data.getAccount().get().origin);
    }

    @Test
    public void testPoolAccount() throws Exception
    {
        // prepare context to expect an account from pool
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());
        Context.requiresRegisteredAccount(true);

        // clear the pool
        final DataPool<Account> pool = TestUtils.getFieldValue(AccountManager.getInstance("UK"), "accounts");
        TestUtils.setFieldValue(pool, "expirationRate", 0);
        pool.clear();

        // create an account and add it to the pool
        final Account account = new Account();
        account.email = "test@varmail.de";
        account.firstname = "Klaus";
        account.lastname = "Klausen";
        account.isRegistered = true;
        AccountManager.returnAccount(Optional.of(account));

        // require an account from pool
        // check that it matches the previously added one
        Assert.assertEquals("Account does not match preconfigured.", account.email, Context.get().data.attachAccount().get().email);
        // check the account origin flag is set properly
        Assert.assertEquals("Default Account should come from origin POOL", AccountOrigin.POOL, Context.get().data.getAccount().get().origin);
    }

    @Test
    public void testEmptyPoolAccount() throws Exception
    {
        // prepare context to expect an account from pool
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());
        Context.requiresRegisteredAccount(true);

        // clear the pool
        final DataPool<Account> pool = TestUtils.getFieldValue(AccountManager.getInstance("UK"), "accounts");
        TestUtils.setFieldValue(pool, "expirationRate", 0);
        pool.clear();

        // require an account (from pool, because requiresRegisteredAccount flag is set to true).
        Context.get().data.attachAccount();

        // as there is no account it will be generated freshly.
        Assert.assertEquals("Default Account should come from origin POOL", AccountOrigin.RANDOM, Context.get().data.getAccount().get().origin);
    }

    @Test
    public void testFileAccount()
    {
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());

        Context.get().configuration.predefinedAccountsFile = "accounts.csv";
        final Optional<Account> fromFile = AccountSupplierManager.getFromFile();
        Context.get().data.setAccount(fromFile);
        Assert.assertEquals("Account does not match preconfigured.", "hansi@varmail.net", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Default Account should come from origin FILE", AccountOrigin.FILE, Context.get().data.getAccount().get().origin);
    }
    
    @Test
    public void testFileAccountFromFile()
    {
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());

        Context.requiresRegisteredAccount(true);
        
        Context.get().configuration.predefinedAccountsFile = "accounts.csv";
        Context.get().data.attachAccountFromFile(false);
        
        Assert.assertEquals("Account does not match preconfigured.", "hansi@varmail.net", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Default Account should come from origin FILE", AccountOrigin.FILE, Context.get().data.getAccount().get().origin);
    }

    @Test
    public void testFileAccount2() throws Exception
    {
        // prepare context, require an registered account, but also require it from file
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());
        Context.requiresRegisteredAccount(true);
        Context.get().configuration.predefinedAccountsFile = "accounts.csv";

        // clear the pool
        final DataPool<Account> pool = TestUtils.getFieldValue(AccountManager.getInstance("UK"), "accounts");
        TestUtils.setFieldValue(pool, "expirationRate", 0);
        pool.clear();

        // create an account and add it to the pool
        final Account account = new Account();
        account.email = "test@varmail.de";
        account.firstname = "Klaus";
        account.lastname = "Klausen";
        account.isRegistered = true;
        AccountManager.returnAccount(Optional.of(account));

        // get account from file
        final Optional<Account> fromFile = AccountSupplierManager.getFromFile();
        Context.get().data.setAccount(fromFile);

        // check resulting account
        Assert.assertEquals("Account does not match preconfigured.", "hansi@varmail.net", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Default Account should come from origin FILE", AccountOrigin.FILE, Context.get().data.getAccount().get().origin);
    }
    
    @Test
    public void testExclusiveAccountSiteSpecific() throws Exception
    {
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("US").get());
        
        Context.get().configuration.predefinedAccountsFile = "accounts.csv";
        final Optional<Account> exclusiveFromFile2 = AccountSupplierManager.getExclusiveAccount();
        Context.get().data.setAccount(exclusiveFromFile2);
        Assert.assertEquals("Account does not match preconfigured.", "horsti@varmail.net", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Default Account should come from origin EXCLUSIVE", AccountOrigin.EXCLUSIVE, Context.get().data.getAccount().get().origin);
        
        Context.releaseContext();        
        Context.createContext(XltProperties.getInstance(), PageTest.class.getSimpleName(), PageTest.class.getName(), SiteSupplier.siteById("UK").get());
        
        Context.get().configuration.predefinedAccountsFile = "accounts.csv";
        final Optional<Account> exclusiveFromFile = AccountSupplierManager.getExclusiveAccount();
        Context.get().data.setAccount(exclusiveFromFile);
        Assert.assertEquals("Account does not match preconfigured.", "hansi@varmail.net", Context.get().data.getAccount().get().email);
        Assert.assertEquals("Default Account should come from origin EXCLUSIVE", AccountOrigin.EXCLUSIVE, Context.get().data.getAccount().get().origin);
    }
}