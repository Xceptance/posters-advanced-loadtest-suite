package com.xceptance.loadtest.api.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.data.ExclusiveDataProvider;
import com.xceptance.xlt.api.util.XltRandom;


/**
 * A new search data provider that is able to come up with alternative search phrases based on
 * search phrases given. This alternatives should yield the same results as the original one because
 * we try to obey the features SFCC delivers, such as spaces and casing.
 *
 * @author rschwietzke
 */
public class DataSupplier
{
    /**
     * Keep our data loaded and shared based on the site key and they type. We set concurrency for
     * write low but still harvest good read performance
     */
    private final static ConcurrentHashMap<String, List<String>> data = new ConcurrentHashMap<>(1);
    private final static ConcurrentHashMap<String, String> exclusiveData = new ConcurrentHashMap<>(1);
    
    /**
     * Get us the source list for the data in the site context
     *
     * @param filename
     *            file to open in the hierarchy
     * @return the list with the data
     */
    private static List<String> getSourceList(final String filename)
    {
        final Site site = Context.get().data.getSite();

        // get us a key, just use
        final String key = site.toString() + File.separator + filename;

        final List<String> list = data.computeIfAbsent(key, k -> {
            // load the data otherwise break
            final Optional<File> file = DataFileProvider.dataFileBySite(site, filename);

            if (file.isPresent())
            {
                try
                {
                    return Files.readAllLines(file.get().toPath())
                                    .stream()
                                    .map(s -> s.trim())
                                    .filter(s ->
                                    {
                                        return s.length() > 0 && !s.startsWith("#");
                                    })
                                    .collect(Collectors.toList());
                }
                catch (final IOException e)
                {
                    // we will get to the assertion
                }
            }

            Assert.fail(MessageFormat.format("Unable to find data file {0} for site {1}", filename, site));

            // for the compiler, we are not going to reach this otherwise
            return Collections.emptyList();
        });

        // determine a random value
        return list;
    }
    
    /**
     * Read in all predefined files as exclusive and give us an exclusive Optional<Account>.
     * @return new Optional<Account>
     */
    public static Optional<Account> getExclusiveAccount()
    {
        final Site site = Context.get().data.getSite();
        Optional<Account> account = Optional.empty();
        
        final String key = site.toString() + File.separator + Context.configuration().predefinedAccountsFile;  
        
        exclusiveData.computeIfAbsent(key, k -> {
            // load the data otherwise break
             Optional<String> file = DataFileProvider.dataFilePathBySite(site, Context.configuration().predefinedAccountsFile);

            if (!file.isEmpty())
            {
                // get us the location of the file as key
                return file.get();
            }
            else
            {
                Assert.fail(MessageFormat.format("Unable to find data file {0} for site {1}", Context.configuration().predefinedAccountsFile, site));
            }
            // for the compiler, we are not going to reach this otherwise
            return "";
        });
        
        try 
        {
            // get us an exclusive account out of the map with all exclusive files
            account = getExclusiveDataProvider(exclusiveData.get(key)).getRandom();
        } 
        catch (Exception e) 
        {
            Assert.fail(MessageFormat.format("Unable to find data for site {0}", site));        
        }

        // can be null if there are not enough accounts in the file
        if (account == null)
        {
            Assert.fail(MessageFormat.format("Unable to get exclusive account for file in {0}, probably there are not enough accounts", site));
        }
        
        return account;
    }
    
    /**
     * Release the current exclusive account and add it back to the pool from the ExclusiveDataProvider.
     * @param account
     * @throws Exception
     */
    public static void releaseExclusiveAccount(Optional<Account> account) throws Exception
    {
        final String key = Context.getSite().toString() + File.separator + Context.configuration().predefinedAccountsFile;
        getExclusiveDataProvider(exclusiveData.get(key)).add(account);
    }
    
    /**
     * Retrieve an account out of the ExclusiveDataProvider pool, if there are any available.
     * @param path path to the account file
     * @return Optional<Account> from the ExclusiveDataProvider
     * @throws Exception
     */
    private static ExclusiveDataProvider<Optional<Account>> getExclusiveDataProvider(String path) throws Exception
    {
        return ExclusiveDataProvider.getInstance(path, AccountSupplierManager.PARSER);
    }

    /**
     * Get us a first name from a plain file
     */
    public static String firstName()
    {
        return randomString(getSourceList(Context.configuration().dataFileFirstNames));
    }

    /**
     * Get us a last name from a plain file
     */
    public static String lastName()
    {
        return randomString(getSourceList(Context.configuration().dataFileLastNames));
    }

    /***
     * Get a random line from the account file.
     */
    public static String getAccount()
    {
        return randomString(getSourceList(Context.configuration().predefinedAccountsFile));
    }

    /**
     * Just get us a random entry from our list
     */
    private static String randomString(final List<String> list)
    {
        return list.get(XltRandom.nextInt(list.size()));
    }
}
