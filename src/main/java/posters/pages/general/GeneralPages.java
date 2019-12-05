package posters.pages.general;

import com.xceptance.loadtest.api.pages.Page;

import posters.pages.components.general.Footer;
import posters.pages.components.general.Header;
import posters.pages.components.general.MiniCart;
import posters.pages.components.general.Navigation;
import posters.pages.components.general.SiteSearch;
import posters.pages.components.general.User;

public class GeneralPages extends Page
{
    public static final GeneralPages instance = new GeneralPages();

    /**
     * This block indicates all elements accessible for easier direct use
     */
    public Header header = Header.instance;
    public User user = User.instance;
    public MiniCart miniCart = MiniCart.instance;
    public SiteSearch siteSearch = SiteSearch.instance;
    public Footer footer = Footer.instance;
    public Navigation navigation = Navigation.instance;

    /**
     * Validates a common page. Checks for standard components.
     *
     * @throws Exception
     */
    @Override
    public void validate()
    {
        // no check for response code or content anymore because that is transport
        // as well as html end tag. The html parser will probably add that to the tree
        // if unbalanced, so taking the tree, turning it into HTML does not really work
        // rather go for the response stream instead when we know that we should have
        // a full page

        // add additional test here if needed
        validate(has(header, user, miniCart, siteSearch, footer, navigation));
    }

    @Override
    public boolean is()
    {
        // do additional test here if needed and combine them with the super
        // result that uses has and hasNot
        return matches(has(header, miniCart, siteSearch, footer, navigation));
    }
}
