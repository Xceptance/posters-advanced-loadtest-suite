package com.xceptance.loadtest.posters.models.pages.catalog;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.posters.models.components.category.CategorySlot;
import com.xceptance.loadtest.posters.models.components.homepage.PromotedProducts;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

public class CategoryLandingPage extends GeneralPages
{
    public static final CategoryLandingPage instance = new CategoryLandingPage();

    private final CategorySlot categorySlot = CategorySlot.instance;
    private final PromotedProducts promotedProduct = PromotedProducts.instance;

    @Override
    public void validate()
    {
        super.validate();
        validate(has(categorySlot, promotedProduct));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(categorySlot, promotedProduct));
    }

    public LookUpResult getCategorySlotsLinks()
    {
        return categorySlot.getCategorySlotsLinks();
    }
}
