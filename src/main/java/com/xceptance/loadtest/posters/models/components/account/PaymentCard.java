package com.xceptance.loadtest.posters.models.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

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
