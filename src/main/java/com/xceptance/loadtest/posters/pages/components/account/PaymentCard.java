package com.xceptance.loadtest.posters.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public class PaymentCard implements Component
{
    public final static PaymentCard instance = new PaymentCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("linkPaymentOverview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
