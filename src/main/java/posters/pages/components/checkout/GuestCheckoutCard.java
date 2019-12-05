package posters.pages.components.checkout;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public class GuestCheckoutCard implements Component
{
    public final static GuestCheckoutCard instance = new GuestCheckoutCard();

    /**
     * Lookup the footer.
     */
    @Override
    public LookUpResult locate()
    {
        // this CSS path is bad, because the html is bad
        return Page.find().byCss(".card").hasCss(".btn.checkout-as-guest");
    }

    /**
     * Indicates if this component exists
     *
     * @return
     */
    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    /**
     * Return the guest checkout link or button
     */
    public LookUpResult getGuestCheckoutButton()
    {
        return locate().byCss(".btn.checkout-as-guest");
    }
}
