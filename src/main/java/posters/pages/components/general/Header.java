package posters.pages.components.general;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;

public class Header implements Component
{
    public final static Header instance = new Header();

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("header");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
