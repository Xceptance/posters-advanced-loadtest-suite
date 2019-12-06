package com.xceptance.loadtest.posters.pages.components.account;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

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
