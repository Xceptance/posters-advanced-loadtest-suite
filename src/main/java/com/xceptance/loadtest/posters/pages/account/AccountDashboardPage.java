package com.xceptance.loadtest.posters.pages.account;

import com.xceptance.loadtest.posters.pages.components.account.AddressBookCard;
import com.xceptance.loadtest.posters.pages.components.account.PasswordCard;
import com.xceptance.loadtest.posters.pages.components.account.PaymentCard;
import com.xceptance.loadtest.posters.pages.components.account.ProfileCard;
import com.xceptance.loadtest.posters.pages.general.GeneralPages;

public class AccountDashboardPage extends GeneralPages
{
    public static final AccountDashboardPage instance = new AccountDashboardPage();

    public final ProfileCard profileCard = ProfileCard.instance;
    public final PaymentCard paymentCard = PaymentCard.instance;
    public final AddressBookCard addressBookCard = AddressBookCard.instance;
    public final PasswordCard passwordCard = PasswordCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(profileCard, paymentCard, addressBookCard, passwordCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(profileCard, paymentCard, addressBookCard, passwordCard));
    }
}
