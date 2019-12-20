package com.xceptance.loadtest.posters.models.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;

/**
 * User menu component.
 * 
 * @author Xceptance Software Technologies
 */
public class UserMenu implements Component
{
	public static final UserMenu instance = new UserMenu();

    @Override
    public LookUpResult locate()
    {
        return Header.instance.locate().byCss("#userMenu");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public LookUpResult getLoginLink()
    {
        return locate().byCss("a.goToLogin");
    }
    
    public LookUpResult getCreateAccountLink()
    {
    	return locate().byCss("a.goToRegistration");
    }

    public LookUpResult getMyAccountLink()
    {
        return locate().byCss("a.goToAccountOverview");
    }

    public LookUpResult getLogoutLink()
    {
        return locate().byCss("a.goToLogout");
    }
    
    public boolean isLoggedIn()
    {
        return getLogoutLink().exists();
    }

    public boolean isNotLoggedIn()
    {
        return !isLoggedIn();
    }
}