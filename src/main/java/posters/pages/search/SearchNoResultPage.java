package posters.pages.search;

import posters.pages.components.plp.BlacklistedProductGrid;
import posters.pages.components.plp.ProductGrid;
import posters.pages.general.GeneralPages;

public class SearchNoResultPage extends GeneralPages
{
    public static final SearchNoResultPage instance = new SearchNoResultPage();

    @Override
    public void validate()
    {
        super.validate();

        validate(hasAnyOf(has(BlacklistedProductGrid.instance), hasNot(ProductGrid.instance)));
    }

    @Override
    public boolean is()
    {
        return super.is() && (matches(hasNot(ProductGrid.instance)) || matches(has(BlacklistedProductGrid.instance)));
    }
}
