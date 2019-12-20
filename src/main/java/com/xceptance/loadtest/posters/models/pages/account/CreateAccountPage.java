package com.xceptance.loadtest.posters.models.pages.account;

import com.xceptance.loadtest.posters.models.components.account.CreateAccountForm;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents the create account page.
 * 
 * @author Xceptance Software Technologies
 */
public class CreateAccountPage extends GeneralPages
{
    public static final CreateAccountPage instance = new CreateAccountPage();
    
    public final CreateAccountForm createAccountForm = CreateAccountForm.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(createAccountForm));
    }

    @Override
    public boolean is()
    {
        return matches(has(createAccountForm));
    }
}