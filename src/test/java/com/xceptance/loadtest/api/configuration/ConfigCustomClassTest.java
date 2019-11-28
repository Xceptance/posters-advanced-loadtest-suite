package com.xceptance.loadtest.api.configuration;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.ConfigurationBuilder;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.configuration.interfaces.Initable;
import com.xceptance.xlt.api.util.XltProperties;


public class ConfigCustomClassTest
{
    public static final String KEY = "ConfigCustom";

    @Test
    public final void happyPath()
    {
        XltProperties.getInstance().setProperty(KEY + "1.bar", "test1");
        XltProperties.getInstance().setProperty(KEY + "1.level2.level3", "test2");

        final ConfigCustom1 c = ConfigurationBuilder.buildDefault(ConfigCustom1.class);
        Assert.assertEquals("test1", c.foo.bar);
        Assert.assertEquals("test2", c.foo.foo.bar);
    }

    @Test
    public final void withInit()
    {
        XltProperties.getInstance().setProperty(KEY + "2.bar", "yeah");

        final ConfigCustom2 c = ConfigurationBuilder.buildDefault(ConfigCustom2.class);
        Assert.assertEquals("yeah", c.foo.bar);
        Assert.assertEquals("init", c.foo.foo);
    }
}

// happy path
class ConfigCustom1
{
    @Property(key = ConfigCustomClassTest.KEY + "1")
    public ConfigCustom1_2 foo;
}

class ConfigCustom1_2
{
    @Property(key = "bar")
    public String bar;

    @Property(key = "level2")
    public ConfigCustom1_3 foo;
}

class ConfigCustom1_3
{
    @Property(key = "level3")
    public String bar;
}

// init
class ConfigCustom2
{
    @Property(key = ConfigCustomClassTest.KEY + "2")
    public ConfigCustom2_1 foo;
}

class ConfigCustom2_1 implements Initable
{
    @Property(key = "bar")
    public String bar;

    public String foo;

    @Override
    public void init()
    {
        foo = "init";
    }
}