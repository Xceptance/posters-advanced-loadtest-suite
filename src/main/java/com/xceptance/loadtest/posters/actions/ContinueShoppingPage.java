package com.xceptance.loadtest.posters.actions;

import org.junit.Assert;

import com.xceptance.loadtest.api.actions.PageAction;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.validators.Validator;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;
import com.xceptance.loadtest.posters.models.pages.general.HomePage;
import com.xceptance.loadtest.posters.models.pages.general.OrderConfirmationPage;

public class ContinueShoppingPage extends PageAction<ContinueShoppingPage>
{
    @Override
    public void precheck()
    {
        super.precheck();
        Assert.assertTrue("Expected order Confirmation page", OrderConfirmationPage.instance.is());
    }

    @Override
    protected void doExecute() throws Exception
    {
        // Click Continue Shopping button
        loadPageByClick(Page.find().byId("goHome").asserted("Expected single Go to Home button").single());
    }

    @Override
    protected void postValidate() throws Exception
    {
        Validator.validatePageSource();
        // Validate that we are at the home page
        HomePage.instance.validate();
        // Validate that the cart is empty
        Assert.assertTrue("Expected empty cart at homepage after successful order placement", GeneralPages.instance.miniCart.isEmpty());
    }
}