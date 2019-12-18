package com.xceptance.loadtest.posters.models.components.plp;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.components.Component;
import com.xceptance.loadtest.posters.models.pages.catalog.ProductListingPage;

public class GridSort implements Component
{
    public final static GridSort instance = new GridSort();

    @Override
    public LookUpResult locate()
    {
        return ProductListingPage.instance.searchResult.locate().byCss(".grid-header select[name='sort-order']");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public LookUpResult getUnselectedOptions()
    {
        return locate().byCss("option:not([selected])");
    }

    public LookUpResult getSelectedOption()
    {
        return locate().byCss("option[selected]");
    }

    public void updateOption(final String id, final String value)
    {
        final LookUpResult sortOption = locate().byCss("option[data-id='" + id + "']");
        if (sortOption.exists())
        {
            sortOption.asserted("Failed to find single option element").single().setAttribute("value", value);
        }
    }
}
