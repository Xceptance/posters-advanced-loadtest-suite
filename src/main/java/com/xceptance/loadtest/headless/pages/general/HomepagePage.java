package com.xceptance.loadtest.headless.pages.general;

import com.xceptance.loadtest.headless.pages.components.homepage.PromotedCategories;
import com.xceptance.loadtest.headless.pages.components.homepage.PromotedProducts;

public class HomepagePage extends GeneralPages
{
    public static final HomepagePage instance = new HomepagePage();

    public final PromotedProducts promotedProducts = PromotedProducts.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(promotedProducts));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(promotedProducts));
    }
}
