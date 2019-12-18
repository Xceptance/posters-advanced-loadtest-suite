package com.xceptance.loadtest.posters.models.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.api.models.pages.Page;

public class AddressBookCard implements Component
{
    public final static AddressBookCard instance = new AddressBookCard();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byId("linkAddressOverview");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
