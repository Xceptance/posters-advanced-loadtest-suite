package com.xceptance.loadtest.posters.pages.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.DataUtils;

public enum PLPItemCount implements Component
{
    instance;

    //private final Pattern pattern = Pattern.compile("([0-9,.]+)\\s*\\w+");

    @Override
    public LookUpResult locate()
    {
        return Page.find().byCss("#totalProductCount");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public int getItemCount()
    {
        if (exists())
        {
            final String content = locate().first().getTextContent();
            if (content != null)
            {
//                final String s = RegExUtils.getFirstMatch(content, pattern, 1);
                return DataUtils.toInt(content);
            }
        }

        // nothing found or invalid
        return 0;
    }
}
