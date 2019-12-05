package posters.pages.account;

import posters.pages.components.account.CheckOrderCard;
import posters.pages.components.account.LoginAndCreateAccountCard;
import posters.pages.general.GeneralPages;

public class LoginPage extends GeneralPages
{
    public static final LoginPage instance = new LoginPage();

    public final CheckOrderCard checkOrderCard = CheckOrderCard.instance;
    public final LoginAndCreateAccountCard loginAndCreateAccountCard = LoginAndCreateAccountCard.instance;

    @Override
    public void validate()
    {
        super.validate();

        validate(has(checkOrderCard, loginAndCreateAccountCard));
    }

    @Override
    public boolean is()
    {
        return matches(has(checkOrderCard, loginAndCreateAccountCard));
    }
}
