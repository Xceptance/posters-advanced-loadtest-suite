package com.xceptance.loadtest.posters.models.pages.general;

import com.xceptance.loadtest.posters.models.components.confirmationpage.ConfirmationRow;

/**
 * Represents an Order Confirmation page after an order has been placed.
 * 
 * @author Xceptance Software Technologies
 */
public class OrderConfirmationPage extends GeneralPages
{
    public final ConfirmationRow confirmationRow = ConfirmationRow.instance;
    

    @Override
    public void validate()
    {
        super.validate();

        validate(has(confirmationRow));
    }

    @Override
    public boolean is()
    {
        return super.is() && matches(has(confirmationRow));
    }
    
   
}