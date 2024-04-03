package com.xceptance.loadtest.posters.models.pages.account;

import org.junit.Assert;

import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.posters.models.pages.general.GeneralPages;

/**
 * Represents the my account page.
 * 
 * @author Xceptance Software technologies
 */
public class MyAccountPage extends GeneralPages
{
    public static final MyAccountPage instance = new MyAccountPage();

    @Override
    public void validate()
    {
        super.validate();
        
        Assert.assertTrue("Expected my account page title", Page.find().byId("title-account-overview").exists());
        
        Assert.assertTrue("Expected order history link at MyAccount page", getOrderHistoryLink().exists());
        Assert.assertTrue("Expected addresses link at MyAccount page", getMyAddressesLink().exists());
        Assert.assertTrue("Expected payment settings link at MyAccount page", getPaymentSettingsLink().exists());
        Assert.assertTrue("Expected personal data link at MyAccount page", getPersonalDataLink().exists());
    }

    @Override
    public boolean is()
    {
        return Page.find().byId("title-account-overview").exists();
    }
    
    public LookUpResult getOrderHistoryLink()
    {
    	return Page.find().byId("link-order-overview");
    }

    public LookUpResult getMyAddressesLink()
    {
    	return Page.find().byId("link-address-overview");
    }
    
    public LookUpResult getPaymentSettingsLink()
    {
    	return Page.find().byId("link-payment-overview");
    }

    public LookUpResult getPersonalDataLink()
    {
    	return Page.find().byId("link-setting-overview");
    }
}