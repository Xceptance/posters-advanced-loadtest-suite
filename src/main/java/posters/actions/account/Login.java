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

import posters.jsondata.account.LoginJSON;
import posters.pages.account.AccountDashboardPage;
import posters.pages.account.LoginPage;

public class Login extends PageAction<Login>
{
    private final Account account;

    public Login(final Account account)
    {
        this.account = account;
    }

    @Override
    protected void doExecute() throws Exception
    {
        // click on login button
        final HtmlForm form = LoginPage.instance.loginAndCreateAccountCard.fillLoginForm(account);

        final WebResponse response = new HttpRequest().XHR()
                        .url(form.getActionAttribute())
                        .postParams(AjaxUtils.serializeForm(form))
                        .assertStatusCode(200)
                        .POST()
                        .fire();

        // render shipping method list
        final Gson gson = Context.getGson();
        final LoginJSON data = gson.fromJson(response.getContentAsString(), LoginJSON.class);
        Assert.assertTrue("Failed submitting login request", data.success);

        this.loadPage(Page.makeFullyQualifiedUrl(data.redirectUrl));
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();

        AccountDashboardPage.instance.is();
    }
}
