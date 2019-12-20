package com.xceptance.loadtest.posters.models.pages.account;

import com.xceptance.loadtest.posters.models.components.account.LoginForm;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents the login page.
 * 
 * @author Xceptance Software Technologies
 */
public class LoginPage extends GeneralPages
{
    public static final LoginPage instance = new LoginPage();

    public final LoginForm loginForm = LoginForm.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(loginForm));
    }

    @Override
    public boolean is()
    {
        return matches(has(loginForm));
    }
}