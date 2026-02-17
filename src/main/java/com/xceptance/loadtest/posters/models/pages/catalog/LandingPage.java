package com.xceptance.loadtest.posters.models.pages.catalog;

import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

import org.junit.Assert;

/**
 * Represents a landing page.
 * 
 * @author Xceptance Software Technologies
 */
public class LandingPage extends GeneralPages
{
    public static final LandingPage instance = new LandingPage();
    
    private static final String LOCATOR = "TODO Add Landing Page Locator here";

    @Override
    public void validate()
    {
        super.validate();

        Assert.assertTrue("Expected landing page", Page.find().byCss(LOCATOR).exists());
    }

    @Override
    public boolean is()
    {
        return super.is() && Page.find().byCss(LOCATOR).exists();
    }
}