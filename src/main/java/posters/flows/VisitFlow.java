package posters.flows;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.util.Context;

import posters.actions.Homepage;

public class VisitFlow extends Flow
{
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute() throws Throwable
    {
        // Open the start URL
        new Homepage(Context.configuration().siteUrlHomepage).run();

        return true;
    }
}
