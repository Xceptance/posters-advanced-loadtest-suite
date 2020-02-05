package com.xceptance.loadtest.api.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.common.util.CsvUtils;
import com.xceptance.loadtest.api.configuration.LTProperties;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.data.ExclusiveDataProvider.Parser;

/**
 * Manages providing accounts (account sources).
 *
 * @author Xceptance Software Technologies
 */
public class AccountSupplierManager
{
    /**
     * Get a new account based on the configuration of the site
     */
    public static Account getAccount()
    {
        return Context.configuration().accounts.value.fork(a ->
        {
            boolean propertyUsed = false;

            final boolean overrideUsed = handleOverrides(a);

            // still possible that another property has set one of the following
            if (StringUtils.isBlank(a.firstname))
            {
                a.firstname = DataSupplier.firstName();
            }
            else
            {
                propertyUsed = true;
            }

            if (StringUtils.isBlank(a.lastname))
            {
                a.lastname = DataSupplier.lastName();
            }
            else
            {
                propertyUsed = true;
            }

            if (StringUtils.isBlank(a.email))
            {
                a.email = Email.randomEmail();
            }
            else
            {
                propertyUsed = true;
            }

            if (StringUtils.isBlank(a.login))
            {
                a.login = a.email;
            }
            else
            {
                propertyUsed = true;
            }

            // make the value origin visible
            if (overrideUsed)
            {
                a.origin = AccountOrigin.OVERRIDE;
            }
            else if (propertyUsed)
            {
                a.origin = AccountOrigin.PROPERTIES;
            }

            return a;
        });
    }

    /**
     * Checks if account data for current siteId is set in properties file to override randomness
     * for debug purpose
     *
     * @param account
     * @return flag if at least one override was used
     */
    private static boolean handleOverrides(final Account account)
    {
        // check if some properties should override for current siteId
        boolean overrideUsed = false;

        final String siteId = Context.getSite().getId();
        final LTProperties properties = Context.get().configuration.getProperties();

        final String overrideFirstName = properties.getProperty("account." + siteId + ".firstname");
        final String overrideLastName = properties.getProperty("account." + siteId + ".lastname");
        final String overrideEmail = properties.getProperty("account." + siteId + ".email");
        final String overridePassword = properties.getProperty("account." + siteId + ".password");
        final String overrideIsRegistered = properties.getProperty("account." + siteId + ".isRegistered");

        // do all overrides first
        if (StringUtils.isNotBlank(overrideFirstName))
        {
            account.firstname = overrideFirstName;
            overrideUsed = true;
        }

        if (StringUtils.isNotBlank(overrideLastName))
        {
            account.lastname = overrideLastName;
            overrideUsed = true;
        }
        if (StringUtils.isNotBlank(overrideEmail))
        {
            account.email = overrideEmail;
            account.login = overrideEmail;
            overrideUsed = true;
        }

        if (StringUtils.isNotBlank(overridePassword))
        {
            account.password = overridePassword;
            overrideUsed = true;
        }

        if (StringUtils.isNotBlank(overrideIsRegistered))
        {
            account.isRegistered = Boolean.parseBoolean(overrideIsRegistered);
            overrideUsed = true;
        }

        return overrideUsed;
    }

    /**
     * Read the account from file and replace the values from the file. Mark the account origin to FILE.
     *
     * @return Account
     */
    public static Optional<Account> getFromFile()
    {
        final String[] decode = CsvUtils.decode(DataSupplier.getAccount());

        return Context.configuration().accounts.value.optFork(a ->
        {
            a.firstname = decode[0];
            a.lastname = decode[1];
            a.email = decode[2];
            a.login =  decode[3];
            a.orderID = decode[4];

            a.isRegistered = true;

            a.origin = AccountOrigin.FILE;
            return a;
        });
    }
    
    /**
     *  Parser for the exclusive data provider to read account files.
     */
    public static final Parser<Optional<Account>> PARSER = new Parser<Optional<Account>>()
    {
        @Override
        public List<Optional<Account>> parse(final List<String> rows) 
        {
            return rows.stream()
                .filter(row -> !row.trim().startsWith("#")) // ignore hashed out lines
                .map(row -> getExclusiveFromFile(row)) // convert row to object
                .filter(gc -> gc != null) // ignore failed conversions
                .collect(Collectors.toList());
        }
    };
    
    /**
     * Read a previous used account from file, exclusively.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private static Optional<Account> getExclusiveFromFile(String row)
    {
        final String[] decode = CsvUtils.decode(row);
        return Context.configuration().accounts.value.optFork(a ->
        {
            a.firstname = decode[0];
            a.lastname = decode[1];
            a.email = decode[2];
            a.login = decode[3];
            a.orderID = decode[4];

            a.isRegistered = true;

            a.origin = AccountOrigin.EXCLUSIVE;
            return a;
        });
    }
    
    /**
     * Get us an exclusive account.
     * @return new Optional<Account>
     */
    public static final Optional<Account> getExclusiveAccount()
    {
        return DataSupplier.getExclusiveAccount();
    }
}
