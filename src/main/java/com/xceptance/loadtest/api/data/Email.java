package com.xceptance.loadtest.api.data;

import java.util.UUID;

import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Email utilities.
 *
 * @author Xceptance Software Technologies
 */
public class Email
{
    /**
     * Returns a random email address using the internal random generator to make it predictable if
     * desired, if not, it goes for UUID
     *
     * @return random email
     */
    public static String randomEmail()
    {
        return randomEmail(Context.configuration().stronglyRandomEmails);
    }

    /**
     * Returns a random email address using the internal random generator to make it predictable if
     * desired, if not, it goes for UUID
     *
     * @param stronglyRandom
     *            if true, go for UUID instead of XltRandom
     * @return random email
     */
    public static String randomEmail(final boolean stronglyRandom)
    {
        String uuid = null;

        if (stronglyRandom)
        {
            uuid = UUID.randomUUID().toString();
        }
        else
        {
            // need 16 bytes random value
            final byte[] byteArray = new byte[16];
            XltRandom.nextBytes(byteArray);

            uuid = UUID.nameUUIDFromBytes(byteArray).toString();
        }

        final String data = uuid.replaceAll("-", "");
        final StringBuilder sb = new StringBuilder(42);

        sb.append(Context.configuration().emailLocalPartPrefix);
        sb.append(data.concat(data).substring(0, Context.configuration().emailLocalPartLength));
        sb.append("@");
        sb.append(Context.configuration().emailDomain);

        return sb.toString().toLowerCase();
    }
}
