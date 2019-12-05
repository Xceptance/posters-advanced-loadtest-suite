package posters.actions.account;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.google.gson.Gson;
import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.data.Account;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.util.AjaxUtils;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.util.HttpRequest;
import com.xceptance.loadtest.api.validators.Validator;

import posters.jsondata.account.CreateAccountJSON;
import posters.pages.account.AccountDashboardPage;
import posters.pages.account.LoginPage;

public class CreateAccount extends PageAction<CreateAccount>
{
    private final Account account;

    public CreateAccount(final Account account)
    {
        this.account = account;
    }

    @Override
    protected void doExecute() throws Exception
    {
        // click on login button
        final HtmlForm form = LoginPage.instance.loginAndCreateAccountCard.fillAndGetCreateAccountForm(account);

        final WebResponse response = new HttpRequest().XHR()
                        .url(form.getActionAttribute())
                        .postParams(AjaxUtils.serializeForm(form))
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final Gson gson = Context.getGson();
        final CreateAccountJSON data = gson.fromJson(response.getContentAsString(), CreateAccountJSON.class);
        Assert.assertTrue("Failed submitting account creation request", data.success);

        this.loadPage(Page.makeFullyQualifiedUrl(data.redirectUrl));

    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        AccountDashboardPage.instance.validate();

        account.isRegistered = true;
    }
}
