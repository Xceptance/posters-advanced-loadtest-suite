package com.xceptance.loadtest.api.configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.YamlProperties;

public class YamlPropertiesTest
{
    @Test
    public void yamlToProperties()
    {
        try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/yaml/simple.yaml")))
        {
            final Properties yaml = YamlProperties.build(reader);

            final Properties original = new Properties();
            original.load(this.getClass().getResourceAsStream("/yaml/simple.properties"));

            Assert.assertTrue(original.equals(yaml));
        }
        catch (final IOException e)
        {
        }
    }
}
