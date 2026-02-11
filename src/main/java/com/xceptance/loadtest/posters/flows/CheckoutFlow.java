package com.xceptance.loadtest.posters.flows;

import java.util.Optional;

import com.xceptance.loadtest.api.flows.Flow;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.posters.actions.checkout.Checkout;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutPayment;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutPlaceOrder;
import com.xceptance.loadtest.posters.actions.checkout.CheckoutShippingAddress;
import com.xceptance.loadtest.api.util.CustomData;
import com.xceptance.xlt.api.engine.CustomValue;
import com.xceptance.xlt.api.engine.Session;

/**
 * Follows the checkout process, optionally placing an order.
 * 
 * @author Xceptance Software Technologies
 */
public class CheckoutFlow extends Flow
{
	private boolean placeOrder = false;

	public CheckoutFlow(boolean placeOrder)
	{
		this.placeOrder = placeOrder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execute() throws Throwable
	{
		// Custom Timers
		final CustomData checkoutFlowTimer = new CustomData("CheckoutFlow");
		final Optional<CustomData> checkoutTimer = Optional.ofNullable(placeOrder ? new CustomData("Checkout") : null);

		// Custom Value: CartItemCount
		final CustomValue cartItemCount = new CustomValue("CartItemCount");
		cartItemCount.setValue(Context.get().data.cartLineItemCount);
		Session.getCurrent().getDataManager().logDataRecord(cartItemCount);

		try
		{
			// Enter checkout
			new Checkout().run();

			// Provide shipping address
			new CheckoutShippingAddress().run();

			// Provide payment details
			new CheckoutPayment().run();

			// Place the order and proceed to the Order Confirmation Page
			if(placeOrder)
			{
				// Custom Value: OrderValue
				final String totalText = Page.find().byCss("#order-total").asserted().first().getTextContent();
				// Basic price cleaning: remove anything that is not a digit, dot or comma
				final String cleanTotalText = totalText.replaceAll("[^0-9.,]", "");
				final CustomValue orderValue = new CustomValue("OrderValue");
				orderValue.setValue(Double.parseDouble(cleanTotalText.replace(",", ".")));
				Session.getCurrent().getDataManager().logDataRecord(orderValue);

				new CheckoutPlaceOrder().run();

				// Stop and log the Checkout timer (successful orders only)
				checkoutTimer.ifPresent(timer ->
				{
					timer.setRunTime();
					Session.getCurrent().getDataManager().logDataRecord(timer);
				});
			}
		}
		finally
		{
			// Stop and log the CheckoutFlow timer (all attempts)
			checkoutFlowTimer.setRunTime();
			Session.getCurrent().getDataManager().logDataRecord(checkoutFlowTimer);
		}

		return true;
	}
}