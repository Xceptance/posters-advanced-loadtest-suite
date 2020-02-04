package com.xceptance.loadtest.api.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.configuration.interfaces.Initable;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Credit Card
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class CreditCard implements Initable
{
    /** Credit card CVN. */
    @Property(key = "cvc", required = true)
    public String cvc;

    /** Credit card owner. */
    @Property(key = "owner", required = false)
    private String owner;

    /** Credit card number. */
    @Property(key = "number", required = true)
    public String number;

    /** Same as above but striclty unformatted aka no spaces **/
    public String unformattedNumber;

    /** Month of credit card expiration. */
    @Property(key = "expiration.month", required = false)
    public String expirationMonth;

    /** Year of credit card expiration. */
    @Property(key = "expiration.year", required = false)
    public String expirationYear;

    /** Issue number. */
    @Property(key = "issueNumber", required = false)
    public String issueNumber;

    /** Credit card type. */
    @Property(key = "type", required = true)
    public String type;

    /**
     * Fix the data records when not all data is available
     */
    @Override
    public void init()
    {
        // build the unformatted one
        unformattedNumber = RegExUtils.removeAll(number, "\\s");

        // CVN. If no CVN is configured, generate a random 3 digit number.
        // But that should not be random but repeatable, same creditcard, same number
        if (StringUtils.isBlank(cvc))
        {
            // make sure it is 100 till 999, so that we do not have a padding problems with 7
            // that should be 007, yes, AMEX is 4 digits
            if ("AMEX".equalsIgnoreCase(type))
            {
                cvc = String.valueOf(new Random(number.hashCode()).nextInt(9000) + 1000);
            }
            else
            {
                cvc = String.valueOf(new Random(number.hashCode()).nextInt(900) + 100);
            }
        }

        // Year. If no year is configured, take the very next year from NOW.
        if (StringUtils.isBlank(expirationYear))
        {
            final LocalDate futureYear = LocalDate.now(ZoneId.of("UTC")).plusYears(2) ;
            expirationYear = String.valueOf(futureYear.getYear());
        }

        // Month. If no month is configured, set a random month.
        if (StringUtils.isBlank(expirationMonth))
        {
            expirationMonth = String.valueOf(XltRandom.nextInt(1, 12));
        }
    }

    /**
     * Returns the owner or if not set, execute the function and use that. If the defaultOwner is an
     * operation, this is more efficient because the function is lazy and if not called, we are
     * faster.
     *
     * @param defaultOwner
     *            the default owner as function
     * @return the owner of the card
     */
    public String getOwnerOrElse(final Supplier<String> defaultOwner)
    {
        return owner == null ? defaultOwner.get() : owner;
    }

    /**
     * Returns the owner or if not set, the default
     *
     * @param defaultOwner
     *            the default owner as old school default value
     * @return the owner of the card
     */
    public String getOwnerOrElse(final String defaultOwner)
    {
        return owner == null ? defaultOwner : owner;
    }

	@Override
	public String toString() {
		return "CreditCard [cvc=" + cvc + ", owner=" + owner + ", number=" + number + ", unformattedNumber="
				+ unformattedNumber + ", expirationMonth=" + expirationMonth + ", expirationYear=" + expirationYear
				+ ", issueNumber=" + issueNumber + ", type=" + type + "]";
	}
}
