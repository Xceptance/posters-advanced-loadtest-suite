package com.xceptance.loadtest.headless.actions.catalog.flash;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.headless.pages.catalog.ProductDetailPage;
import com.xceptance.loadtest.headless.pages.components.pdp.ProductDetail;
import com.xceptance.xlt.api.engine.Session;


/**
 * Open the product detail page for pre-set product
 *
 * @author René Schwietzke (Xceptance Software Technologies GmbH)
 */
public class DirectProductDetails extends PageAction<DirectProductDetails>
{
    /**
     * The url to jump to
     */
    private final String url;

    /**
     * Get us the type for later, mainly to save time or debug
     */
    private ProductDetailPage<? extends ProductDetail> pdpType;

    /**
     * Constructor
     *
     * @param url
     *            the url to jump to, we expect a PDP there
     */
    public DirectProductDetails(final String url)
    {
        this.url = url;
    }

    @Override
    public void precheck()
    {
        // nothing here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() throws Exception
    {
        // Open the product page directly
        loadPage(url);

        // wait until it is time to let it run
        futureIsComing();
    }

    /**
     * Refresh the site, if specified, in a repeating interval, after we hit the flash start time,
     * we let t run
     * 
     * @throws Exception
     */
    private void futureIsComing() throws Exception
    {
        final int updaterInterval = Context.configuration().flashRefreshTime;
        final long startOfLoggingPeriod = Session.getCurrent().getDataManager().getStartOfLoggingPeriod();
        if (Context.configuration().flashStartTime <= 0)
        {
            return;
        }

        Instant loadTestStartTime = null;
        // users start running at different times but there is only one test start time
        if (Session.getCurrent().isLoadTest())
        {
            loadTestStartTime = Instant.ofEpochMilli(startOfLoggingPeriod);
        }
        else
        {
            loadTestStartTime = Instant.now();
        }

        // Calculate a timestamp in the future
        Instant fixedTimeInFuture = loadTestStartTime.plus(Context.configuration().flashStartTime, ChronoUnit.MINUTES);

        // The test might already be running for a while, so adding just one interval
        // might not be enough.
        // We need to make sure our future timestamp is beyond now (since all agents use
        // the same starting point adding a fixed interval will end at the same time for
        // all agents)
        while (fixedTimeInFuture.isBefore(Instant.now()))
        {
            fixedTimeInFuture = fixedTimeInFuture.plus(updaterInterval, ChronoUnit.MICROS);
        }

        while (Instant.now().isBefore(fixedTimeInFuture))
        {
            // refresh and update site
            Thread.sleep(updaterInterval * 1000);
            loadPage(url);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // this was a page load, so validate what is important
        Validator.validatePageSource();

        pdpType = ProductDetailPage.identify();
    }

    /**
     * Return us the product type identified
     */
    public ProductDetailPage<? extends ProductDetail> getType()
    {
        return pdpType;
    }
}
