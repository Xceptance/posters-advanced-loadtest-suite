package com.xceptance.loadtest.posters.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.loadtest.api.data.Email;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.DOMUtilsTest;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.util.XltRandom;

public class EmailTest
{
    private static final String EMAILFORMAT = "xc[0-9a-z]{14}@varmail.net";

    @BeforeClass
    public static void init() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        Context.createContext(properties, DOMUtilsTest.class.getSimpleName(), DOMUtilsTest.class.getName(), SiteSupplier.randomSite().get());
    }

    /**
     * Test UUID generation
     */
    @Test
    public void testRandomEmailViaUUID()
    {
        XltRandom.setSeed(1);
        Assert.assertEquals(1, XltRandom.getSeed());

        final String email1 = Email.randomEmail(true);
        final String email2 = Email.randomEmail(true);

        XltRandom.setSeed(1);
        Assert.assertEquals(1, XltRandom.getSeed());

        final String email3 = Email.randomEmail(true);
        final String email4 = Email.randomEmail(true);

        Assert.assertFalse(email1.equals(email2));
        Assert.assertFalse(email2.equals(email3));
        Assert.assertFalse(email3.equals(email4));

        Assert.assertTrue(email1.matches(EMAILFORMAT));
        Assert.assertTrue(email2.matches(EMAILFORMAT));
        Assert.assertTrue(email3.matches(EMAILFORMAT));
        Assert.assertTrue(email4.matches(EMAILFORMAT));
    }

    /**
     * Test UUID generation
     */
    @Test
    public void testRandomEmailViaXltRandom()
    {
        XltRandom.setSeed(1);
        Assert.assertEquals(1, XltRandom.getSeed());

        final String email1 = Email.randomEmail(false);
        final String email2 = Email.randomEmail(false);

        XltRandom.setSeed(1);
        Assert.assertEquals(1, XltRandom.getSeed());

        final String email3 = Email.randomEmail(false);
        final String email4 = Email.randomEmail(false);

        Assert.assertFalse(email1.equals(email2));
        Assert.assertFalse(email3.equals(email4));
        Assert.assertTrue(email1.equals(email3));
        Assert.assertTrue(email2.equals(email4));

        Assert.assertTrue(email1.matches(EMAILFORMAT));
        Assert.assertTrue(email2.matches(EMAILFORMAT));
        Assert.assertTrue(email3.matches(EMAILFORMAT));
        Assert.assertTrue(email4.matches(EMAILFORMAT));
    }

}
