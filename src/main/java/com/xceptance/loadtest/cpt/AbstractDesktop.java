package com.xceptance.loadtest.cpt;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.webdriver.XltChromeDriver;

/**
 * TODO: Add class description
 */
public abstract class AbstractDesktop extends AbstractTestCase
{
    private static ChromeOptions chromeOpts;
    static
    {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
                        XltProperties.getInstance().getProperty("xlt.webDriver.chrome_clientperformance.pathToDriverServer"));

        final ChromeOptions chromeOpts = new ChromeOptions();
        chromeOpts.addArguments("user-agent="
                                + XltProperties.getInstance().getProperty("xlt.webDriver.chrome_clientperformance.userAgent.desktop"));
    }

    public AbstractDesktop()
    {
        super(new XltChromeDriver(chromeOpts,
                        XltProperties.getInstance().getProperty("xlt.webDriver.chrome_clientperformance.screenless", true)));
        final WebDriver c = getWebDriver();
        c.manage().window().setSize(new Dimension(1400, 1000));
    }
}