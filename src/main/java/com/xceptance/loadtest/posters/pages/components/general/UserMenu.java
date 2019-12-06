package com.xceptance.loadtest.posters.pages.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;

public enum User implements Component
{
    instance;

    @Override
    public LookUpResult locate()
    {
        return Header.instance.locate().byCss("#showUserMenu");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public boolean isNotLoggedOn()
    {
        // horrible CSS code right now!!!
        return !locate().byCss(".popover").exists();
    }

    public LookUpResult getLoginLink()
    {
        return locate().byCss("a");
    }

    public LookUpResult getMyAccountLink()
    {
        return locate().byCss(".popover a:nth-child(1)");
    }

    public LookUpResult getLogoutLink()
    {
        // the horrible design of SFRA...
        return locate().byCss(".popover a:last-child");
    }
}
