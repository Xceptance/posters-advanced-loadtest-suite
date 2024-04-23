package com.xceptance.loadtest.posters.models.pages.search;

import com.xceptance.loadtest.posters.models.components.homepage.PromotedProducts;
import com.xceptance.loadtest.posters.models.components.plp.NotFoundMessage;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents a no result page after executing a search.
 * 
 * @author Xceptance Software Technologies
 */
public class SearchNoResultPage extends GeneralPages
{
    public static final SearchNoResultPage instance = new SearchNoResultPage();
    
    @Override
    public void validate()
    {
        super.validate();

        validate(hasAnyOf(has(PromotedProducts.instance), has(NotFoundMessage.instance)));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(hasAnyOf(has(PromotedProducts.instance), has(NotFoundMessage.instance)));
    }
}