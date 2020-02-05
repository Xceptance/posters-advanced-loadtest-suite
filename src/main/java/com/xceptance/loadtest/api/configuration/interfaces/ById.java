package com.xceptance.loadtest.api.configuration.interfaces;

/**
 * Interface introducing object look up via id.
 *
 * @author Xceptance Software Technologies
 */
public interface ById
{
    /**
     * Returns anything the developer might think is a good idea to be used as an id of this very
     * object that implements this interface.
     *
     * @return an id
     */
    public String getId();
}
