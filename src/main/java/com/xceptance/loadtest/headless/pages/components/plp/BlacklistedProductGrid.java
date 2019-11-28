package com.xceptance.loadtest.headless.pages.components.plp;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.headless.pages.catalog.ProductListingPage;

/**
 * A component which represents a product grid containing only blacklisted (filtered) products.
 */
public class BlacklistedProductGrid implements Component
{
    public static final BlacklistedProductGrid instance = new BlacklistedProductGrid();

    @Override
    public LookUpResult locate()
    {
        // Get the product grid container of this PLP
        final ProductGrid productGrid = ProductListingPage.instance.productGrid;

        // Get all products contained in the grid
        final LookUpResult productTilesResult = productGrid.getProducts();

        // Apply the product URL filter on all products of this grid
        final LookUpResult validProducts = ProductTile.getPDPLinks(productTilesResult)
                        .discard(Context.configuration().filterProductUrls.unweightedList().stream()
                                        .filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList()), e -> e.getAttribute("href"));

        // There are some products which did not get filtered, hence this component is not required (does not exist)
        if (validProducts.exists())
        {
            return LookUpResult.DOESNOTEXIST;
        }

        // All products got filtered, hence this component is required and does exist
        return productGrid.locate();
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }
}
