package com.xceptance.loadtest.api.data;

import org.apache.commons.lang3.StringUtils;

import com.xceptance.xlt.api.data.ExclusiveDataProvider;

public class GiftCardProvider
{
    private static final String GIFT_CARDS_FILE = "giftcards.txt";

    /**
     * Get a random gift card ID.
     * 
     * @return gift card ID or <code>null</code> if no gift card is available
     * @throws Exception
     *             if gift cards cannot get accessed, or not enough gift cards (for distribution) are available.
     */
    public static String getGiftCard() throws Exception
    {
        return ExclusiveDataProvider.getInstance(GIFT_CARDS_FILE).getRandom();
    }

    /**
     * Release the current gift card.
     * 
     * @throws Exception
     *             if releasing gift card fails
     */
    public static void releaseGiftCard(final String giftCard) throws Exception
    {
        if (StringUtils.isNotBlank(giftCard))
        {
            ExclusiveDataProvider.getInstance(GIFT_CARDS_FILE).add(giftCard);
        }
    }
}
