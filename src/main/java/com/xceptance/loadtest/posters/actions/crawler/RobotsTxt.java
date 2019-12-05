package com.xceptance.loadtest.posters.actions.crawler;

import java.net.URL;

import org.apache.http.client.utils.URIBuilder;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.crawler.Robots;

/**
 * Loads robots.txt
 *
 * @author Rene Schwietzke
 * @version
 */
public class RobotsTxt extends PageAction<RobotsTxt>
{
    private WebResponse response;

    @Override
    protected void doExecute() throws Exception
    {
        Context.setBasicAuthenticationHeader();

        // build URL
        final URL robotsUrl = new URIBuilder(Context.configuration().siteUrlHomepage) //
                .setPath("/robots.txt") //
                .removeQuery() //
                .setFragment(null) //
                .build().toURL();

        final WebRequest request = new WebRequest(robotsUrl, HttpMethod.GET);
        response = getWebClient().loadWebResponse(request);
    }

    @Override
    protected void postExecute() throws Exception
    {
        // nothing
    }

    @Override
    protected void postValidate() throws Exception
    {
        Robots.TXT.readFrom(response);
    }
}
