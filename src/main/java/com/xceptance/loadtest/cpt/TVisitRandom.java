package com.xceptance.loadtest.cpt;

import java.text.MessageFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.data.DataFileProvider;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.xlt.api.data.DataProvider;

/**
 * Single click visitor. The visitor opens the landing page and will not do any interaction.
 *
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 *
 */
public class TVisitRandom extends AbstractDesktop
{
    /**
     * {@inheritDoc}
     */
    @Test
    public void test() throws Throwable
    {
        final DataProvider URLS = getCptUrls("cpt-urls.txt");

        for (int i = 0; i < Context.configuration().urlCount.random(); i++)
        {
            final String url = URLS.getRandomRow();
            Assert.assertTrue("Blank URL not allowed.", StringUtils.isNotBlank(url));

            openUrl(getActionName(url), url);
        }
    }

    private String getActionName(String url)
    {
        // remove trailing "/"
        url = url.replaceAll("/$", "");

        final int lastPosition = url.lastIndexOf("/");

        return url.substring(lastPosition);
    }

    private DataProvider getCptUrls(final String fileName) throws Exception
    {
        final Optional<String> fileInDataDirectory = DataFileProvider.dataFilePathBySite(getSite(), fileName);
        if (fileInDataDirectory.isPresent())
        {
            return DataProvider.getInstance(fileInDataDirectory.get());
        }

        Assert.fail(MessageFormat.format("Unable to find file {0} for site {1}", fileName, getSite()));
        return null;
    }
}
