package com.xceptance.loadtest.cpt;

import org.junit.After;
import org.openqa.selenium.WebDriver;

import com.xceptance.loadtest.api.data.Site;
import com.xceptance.loadtest.api.data.SiteSupplier;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverScriptTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * TODO: Add class description
 */
public abstract class AbstractTestCase extends AbstractWebDriverScriptTestCase
{
    /**
     * The determined site or an empty optional if not yet done
     */
    private Site site;

    public AbstractTestCase(final WebDriver driver)
    {
        super(driver);

        // this moved here to make sure we see the exceptions
        Context.createContext(XltProperties.getInstance(), Session.getCurrent().getUserName(), getClass().getName(), getSite());
    }

    public void openUrl(final String name, final String url)
    {
        startAction(name);
        open(url);
        stopAction();

        think();
    }

    public static void think()
    {
        try
        {
            Thread.sleep(3000);
        }
        catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void reset()
    {
        deleteAllVisibleCookies();
    }

    /**
     * Clean up.
     */
    @After
    public void quitDriver()
    {
        // Shutdown WebDriver.
        getWebDriver().quit();
    }

    /**
     * Returns a random site
     *
     * @return
     */
    public Site getSite()
    {
        return site == null ? site = supplySite() : site;
    }

    public Site supplySite()
    {
        return SiteSupplier.randomSite().get();
    }
}