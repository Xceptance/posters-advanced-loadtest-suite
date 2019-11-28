package com.xceptance.loadtest.api.validator;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.YamlPropertiesBuilder;
import com.xceptance.loadtest.api.util.Context;
import com.xceptance.loadtest.api.validators.Validator.StatusCodeValidator;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.XltConstants;

import util.TestUtils;

public class ValidatorTest_StatusCodeValidator
{
    @BeforeClass
    public static void initClass() throws Exception
    {
        final XltProperties properties = XltProperties.getInstance();
        properties.setProperty("general.properties.yaml.global.files", "sites/sites.yaml");
        properties.setProperty("general.host", "localhost");
        properties.setProperty("general.baseUrl", "http://${general.host}");
        properties.setProperty("general.ocapi.clientId", "foobar");

        properties.setProperty(XltConstants.XLT_PACKAGE_PATH + ".data.directory", "./src/test/resources/data");
        // reset properties (might be cached
        final Class<?> holder = Class.forName(Context.class.getName() +
                        "$DefaultConfigurationLazyHolder");
        TestUtils.setStaticFieldValue(holder, "INSTANCE",
                        TestUtils.invokeStaticMethod(Context.class, "loadDefaultConfiguration"));
        TestUtils.setStaticFieldValue(YamlPropertiesBuilder.class, "propertiesCache", new ConcurrentHashMap<>());
    }

    @Before
    public void init() throws Exception
    {
        statusCodeValidator = new StatusCodeValidatorTestImpl();
    }

    private StatusCodeValidatorTestImpl statusCodeValidator;

    public void testPlain() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 200;
        statusCodeValidator.validate();
    }

    @Test
    public void testMatchInt() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 200;
        statusCodeValidator.expect(200);

        statusCodeValidator.validate();
    }

    @Test
    public void testMatchPattern() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 200;
        statusCodeValidator.expect("200");

        statusCodeValidator.validate();
    }

    @Test
    public void testMatchPatternRegEx() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 200;
        statusCodeValidator.expect("2\\d[0]");

        statusCodeValidator.validate();
    }

    @Test(expected = AssertionError.class)
    public void testMismatchInt() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 500;
        statusCodeValidator.expect(200);

        statusCodeValidator.validate();
    }

    @Test(expected = AssertionError.class)
    public void testMismatchString() throws Throwable
    {
        statusCodeValidator._responseStatusCode = 500;
        statusCodeValidator.expect("200");

        statusCodeValidator.validate();
    }

    @Test(expected = AssertionError.class)
    public void testIntOutOfBounds_subZero() throws Throwable
    {
        statusCodeValidator.expect(-1);
    }

    @Test(expected = AssertionError.class)
    public void testIntOutOfBounds_zero() throws Throwable
    {
        statusCodeValidator.expect(-1);
    }

    @Test(expected = AssertionError.class)
    public void testIntOutOfBounds_tooLarge() throws Throwable
    {
        statusCodeValidator.expect(601);
    }

    @Test(expected = AssertionError.class)
    public void testPatternInvalid_null() throws Throwable
    {
        statusCodeValidator.expect(null);
    }

    @Test(expected = AssertionError.class)
    public void testPatternInvalid_empty() throws Throwable
    {
        statusCodeValidator.expect("");
    }

    @Test(expected = AssertionError.class)
    public void testPatternInvalid_blank() throws Throwable
    {
        statusCodeValidator.expect("   ");
    }

    private static class StatusCodeValidatorTestImpl extends StatusCodeValidator
    {
        int _responseStatusCode;

        @Override
        protected int getCurrentStatusCode()
        {
            return _responseStatusCode;
        }
    }
}
