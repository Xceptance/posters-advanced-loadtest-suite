package com.xceptance.loadtest.api.data;

/**
 * Represents a search option.
 * 
 * @author Xceptance Software Technologies
 */
public enum SearchOption
{
    /** Search is expected to have results. */
    HITS,

    /** Search is expected to have no results. */
    MISS;
}