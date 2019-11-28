package com.xceptance.loadtest.headless.pages.components.plp;

import java.util.regex.Pattern;

import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.DataUtils;

public enum PLPItemCount implements Component
{
    instance;

    private final Pattern pattern = Pattern.compile("([0-9,.]+)\\s*\\w+");

    @Override
    public LookUpResult locate()
    {
        return ProductSearchResult.instance.locate().byCss(".grid-header .result-count");
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
                final String s = RegExUtils.getFirstMatch(content, pattern, 1);
                return DataUtils.toInt(s);
            }
        }

        // nothing found or invalid
        return 0;
    }
}
