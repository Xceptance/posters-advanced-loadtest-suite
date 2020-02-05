package com.xceptance.loadtest.api.configuration.interfaces;

/**
 * Objects implementing this interface are able to execute code after the initial setup of the properties.
 *
 * @author Xceptance Software Technologies
 */
public interface Initable
{
    /**
     * Any kind of setup code that finishes the initialization of the just built object, e.g. calculate missing data, sort things etc.
     */
    public void init();
}