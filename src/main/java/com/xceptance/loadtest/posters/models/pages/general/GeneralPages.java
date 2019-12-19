package com.xceptance.loadtest.posters.models.pages.general;

import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.posters.models.components.general.Footer;
import com.xceptance.loadtest.posters.models.components.general.Header;
import com.xceptance.loadtest.posters.models.components.general.MiniCart;
import com.xceptance.loadtest.posters.models.components.general.Navigation;
import com.xceptance.loadtest.posters.models.components.general.SiteSearch;
import com.xceptance.loadtest.posters.models.components.general.UserMenu;

/**
 * Represents a general page with standard components.
 * 
 * @author Xceptance Software Technologies
 */
public class GeneralPages extends Page
{
    public static final GeneralPages instance = new GeneralPages();

    public Header header = Header.instance;
    
    public UserMenu user = UserMenu.instance;
    
    public MiniCart miniCart = MiniCart.instance;
    
    public SiteSearch siteSearch = SiteSearch.instance;
    
    public Navigation navigation = Navigation.instance;
    
    public Footer footer = Footer.instance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate()
    {
        validate(has(header, user, miniCart, siteSearch, navigation, footer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean is()
    {
        return matches(has(header, miniCart, siteSearch, navigation, footer));
    }
}