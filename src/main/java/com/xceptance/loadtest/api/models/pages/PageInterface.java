package com.xceptance.loadtest.api.models.pages;

/**
 * Page interface.
 * 
 * @author Xceptance Software Technologies
 */
public interface PageInterface
{
	/**
	 * Validates the page and fails if the page does not match expectations.
	 * 
	 * Most likely the validation includes existence of certain components.
	 */
    public void validate();

    /**
     * Checks if the current page is of the expected page type.
     * 
     * @return true if the current page matches the page type.
     */
    public boolean is();
}