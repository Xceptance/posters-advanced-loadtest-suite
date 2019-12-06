package com.xceptance.loadtest.posters.pages.account;

import com.xceptance.loadtest.posters.pages.components.account.AddressBookCard;
import com.xceptance.loadtest.posters.pages.components.account.CheckOrderCard;
import com.xceptance.loadtest.posters.pages.components.account.PaymentCard;
import com.xceptance.loadtest.posters.pages.components.account.ProfileCard;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class AccountDashboardPage extends GeneralPages
{
    public static final AccountDashboardPage instance = new AccountDashboardPage();

    public final CheckOrderCard checkOrderCard = CheckOrderCard.instance;
    public final AddressBookCard addressBookCard = AddressBookCard.instance;
    public final PaymentCard paymentCard = PaymentCard.instance;
    public final ProfileCard profileCard = ProfileCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(checkOrderCard, addressBookCard, paymentCard, profileCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(checkOrderCard, addressBookCard, paymentCard, profileCard));
    }
}
