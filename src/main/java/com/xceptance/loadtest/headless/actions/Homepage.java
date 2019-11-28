package com.xceptance.loadtest.headless.actions;

import java.net.URL;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.headless.pages.general.HomepagePage;

/**
 * Opens the start page.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Homepage extends PageAction<Homepage>
{
    /**
     * Start page URL string.
     */
    private final String urlString;

    /**
     * Constructor.
     *
     * @param urlString
     *            start page URL as string
     */
    public Homepage(final String urlString)
    {
        this.urlString = urlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        final URL url = new URL(urlString);

        // Open the start page.
        loadPage(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        // basic checks for the homepage
        HomepagePage.instance.validate();

        // make sure cart is empty and user logged off
        Assert.assertTrue("Cart not empty", HomepagePage.instance.miniCart.isEmpty());
        Assert.assertTrue("User is logged on", HomepagePage.instance.user.isNotLoggedOn());
    }
}