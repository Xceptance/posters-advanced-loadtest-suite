package com.xceptance.loadtest.api.configuration;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import com.xceptance.loadtest.api.configuration.annotations.EnumProperty;
import com.xceptance.loadtest.api.configuration.annotations.Property;
import com.xceptance.loadtest.api.configuration.interfaces.Initable;
import com.xceptance.xlt.api.util.XltProperties;


public class EnumConfigListTest
{
    public static final String KEY = "EnumConfigList";

    @Test
    public final void happyPath_A()
    {
        XltProperties.getInstance().setProperty(KEY + "A.0.string", "s0");
        XltProperties.getInstance().setProperty(KEY + "A.0.boolean", "true");

        XltProperties.getInstance().setProperty(KEY + "A.1.string", "s1");
        XltProperties.getInstance().setProperty(KEY + "A.1.boolean", "false");

        final EnumConfigListA c = ConfigurationBuilder.buildDefault(EnumConfigListA.class);

        Assert.assertEquals(2, c.foo.size());
        Assert.assertEquals("s0", c.foo.get(0).s);
        Assert.assertTrue(c.foo.get(0).b);

        Assert.assertEquals("s1", c.foo.get(1).s);
        Assert.assertFalse(c.foo.get(1).b);
    }

    @Test
    public final void compactFalse_B1()
    {
        XltProperties.getInstance().setProperty(KEY + "B1.1.string", "s0");
        XltProperties.getInstance().setProperty(KEY + "B1.1.boolean", "true");

        XltProperties.getInstance().setProperty(KEY + "B1.3.string", "s3");
        XltProperties.getInstance().setProperty(KEY + "B1.3.boolean", "false");

        final EnumConfigListB1 c = ConfigurationBuilder.buildDefault(EnumConfigListB1.class);

        Assert.assertEquals(3, c.foo.size());

        Assert.assertEquals("s0", c.foo.get(0).s);
        Assert.assertTrue(c.foo.get(0).b);

        Assert.assertNull(c.foo.get(1));

        Assert.assertEquals("s3", c.foo.get(2).s);
        Assert.assertFalse(c.foo.get(2).b);
    }

    @Test
    public final void compactTrue_B2()
    {
        XltProperties.getInstance().setProperty(KEY + "B2.1.string", "s0");
        XltProperties.getInstance().setProperty(KEY + "B2.1.boolean", "true");

        XltProperties.getInstance().setProperty(KEY + "B2.3.string", "s3");
        XltProperties.getInstance().setProperty(KEY + "B2.3.boolean", "false");

        final EnumConfigListB2 c = ConfigurationBuilder.buildDefault(EnumConfigListB2.class);

        Assert.assertEquals(2, c.foo.size());

        Assert.assertEquals("s0", c.foo.get(0).s);
        Assert.assertTrue(c.foo.get(0).b);

        Assert.assertEquals("s3", c.foo.get(1).s);
        Assert.assertFalse(c.foo.get(1).b);
    }

    /**
     * complex class<br>
     * explicitly set weights
     *
     * @throws Exception
     */
    @Test
    public final void weighted_ComplexType_ExplicitWait() throws Exception
    {
        final String string0 = "s0";
        final Integer weight0 = 3;

        final String string1 = "s1";
        final Integer weight1 = 5;

        XltProperties.getInstance().setProperty(KEY + "WA.0.string", string0);
        XltProperties.getInstance().setProperty(KEY + "WA.0.boolean", "true");
        XltProperties.getInstance().setProperty(KEY + "WA.0.weight", weight0.toString());

        XltProperties.getInstance().setProperty(KEY + "WA.1.string", string1);
        XltProperties.getInstance().setProperty(KEY + "WA.1.boolean", "false");
        XltProperties.getInstance().setProperty(KEY + "WA.1.weight", weight1.toString());

        /*
         * check config list
         */

        final EnumConfigListWeightedA c = ConfigurationBuilder.buildDefault(EnumConfigListWeightedA.class);

        Assert.assertEquals(2, c.foo.size());

        Assert.assertEquals(string0, c.foo.get(0).s);
        Assert.assertTrue(c.foo.get(0).b);

        Assert.assertEquals(string1, c.foo.get(1).s);
        Assert.assertFalse(c.foo.get(1).b);

        /*
         * check underlying list properties
         */

        final WeightedList<TestClass> xweightedList = c.foo.weightedList;

        final Field overallWeightField = xweightedList.getClass().getDeclaredField("overallWeight");
        overallWeightField.setAccessible(true);
        final int totalWeight = overallWeightField.getInt(xweightedList);
        Assert.assertEquals(weight0 + weight1, totalWeight);

        final Field elementsField = xweightedList.getClass().getDeclaredField("weightedElements");
        elementsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final List<Pair<TestClass, Integer>> elements = (List<Pair<TestClass, Integer>>) elementsField.get(xweightedList);
        Assert.assertEquals(string0, elements.get(0).getLeft().s);
        Assert.assertEquals(weight0, elements.get(0).getRight());
        Assert.assertEquals(string1, elements.get(1).getLeft().s);
        Assert.assertEquals(weight1, elements.get(1).getRight());
    }

    /**
     * simple class<br>
     * one implicit weight
     *
     * @throws Exception
     */
    @Test
    public final void weighted_SimpleType_ImplicitWeight() throws Exception
    {
        final String string0 = "s0";
        final Integer weight0 = 7;

        final String string1 = "s1";
        final Integer weight1 = 1; // implicit

        XltProperties.getInstance().setProperty(KEY + "WB.0", string0);
        XltProperties.getInstance().setProperty(KEY + "WB.0.weight", weight0.toString());

        XltProperties.getInstance().setProperty(KEY + "WB.1", string1);

        /*
         * check config list
         */

        final EnumConfigListWeightedB c = ConfigurationBuilder.buildDefault(EnumConfigListWeightedB.class);

        Assert.assertEquals(2, c.foo.size());
        Assert.assertEquals(string0, c.foo.get(0));
        Assert.assertEquals(string1, c.foo.get(1));

        /*
         * check underlying list properties
         */

        final WeightedList<String> weightedList = c.foo.weightedList;

        final Field overallWeightField = weightedList.getClass().getDeclaredField("overallWeight");
        overallWeightField.setAccessible(true);
        final int totalWeight = overallWeightField.getInt(weightedList);
        Assert.assertEquals(weight0 + weight1, totalWeight);

        final Field elementsField = weightedList.getClass().getDeclaredField("weightedElements");
        elementsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final List<Pair<String, Integer>> elements = (List<Pair<String, Integer>>) elementsField.get(weightedList);
        Assert.assertEquals(string0, elements.get(0).getLeft());
        Assert.assertEquals(weight0, elements.get(0).getRight());
        Assert.assertEquals(string1, elements.get(1).getLeft());
        Assert.assertEquals(weight1, elements.get(1).getRight());
    }

    /**
     * weight 0
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public final void weighted_weightZero() throws Exception
    {
        XltProperties.getInstance().setProperty(KEY + "WC.0", "s");
        XltProperties.getInstance().setProperty(KEY + "WC.0.weight", "0");

        ConfigurationBuilder.buildDefault(EnumConfigListWeightedC.class);
    }

    /**
     * weight negative
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public final void weighted_weightNegative() throws Exception
    {
        XltProperties.getInstance().setProperty(KEY + "WD.0", "s");
        XltProperties.getInstance().setProperty(KEY + "WD.0.weight", "-1");

        ConfigurationBuilder.buildDefault(EnumConfigListWeightedD.class);
    }

    /**
     * weight not a number
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public final void weighted_weightNotANumber() throws Exception
    {
        XltProperties.getInstance().setProperty(KEY + "WE.0", "s");
        XltProperties.getInstance().setProperty(KEY + "WE.0.weight", "nan");

        ConfigurationBuilder.buildDefault(EnumConfigListWeightedE.class);
    }

    /**
     * weight empty
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public final void weighted_weightWhitespaces() throws Exception
    {
        XltProperties.getInstance().setProperty(KEY + "WF.0", "s");
        XltProperties.getInstance().setProperty(KEY + "WF.0.weight", "");

        ConfigurationBuilder.buildDefault(EnumConfigListWeightedF.class);
    }

    @Test
    public final void requiredFalse()
    {
        final EnumConfigListRequiredFalse c = ConfigurationBuilder.buildDefault(EnumConfigListRequiredFalse.class);
        Assert.assertEquals(0, c.foo.size());
    }

    @Test(expected = AssertionError.class)
    public final void requiredTrue()
    {
        ConfigurationBuilder.buildDefault(EnumConfigListRequiredTrue.class);
    }

    @Test
    public final void stopOnGap_C()
    {
        XltProperties.getInstance().setProperty(KEY + "C.2.string", "s0");
        XltProperties.getInstance().setProperty(KEY + "C.2.boolean", "true");

        final EnumConfigListC c = ConfigurationBuilder.buildDefault(EnumConfigListC.class);

        Assert.assertEquals(1, c.foo.size());
        Assert.assertEquals("s0", c.foo.get(0).s);
        Assert.assertTrue(c.foo.get(0).b);
    }

    @Test(expected = AssertionError.class)
    public final void doNotPermitMissing()
    {
        XltProperties.getInstance().setProperty(KEY + "C.2.string", "s0");
        XltProperties.getInstance().setProperty(KEY + "C.2.boolean", "true");

        ConfigurationBuilder.buildDefault(EnumConfigListDoNotPermitMissing.class);
    }

    @Test
    public final void checkInitWhenExists()
    {
        XltProperties.getInstance().setProperty(KEY + "WithInit.1.string1", "s11");
        XltProperties.getInstance().setProperty(KEY + "WithInit.2.string1", "s21");

        final EnumConfigListWithInit c = ConfigurationBuilder.buildDefault(EnumConfigListWithInit.class);

        Assert.assertEquals(2, c.foo.size());
        Assert.assertEquals("s11", c.foo.get(0).s1);
        Assert.assertEquals("init", c.foo.get(0).s2);

        Assert.assertEquals("s21", c.foo.get(1).s1);
        Assert.assertEquals("init", c.foo.get(1).s2);
    }

    @Test
    public final void withString()
    {
        XltProperties.getInstance().setProperty(KEY + "WithString.1", "s1");
        XltProperties.getInstance().setProperty(KEY + "WithString.2", "s2");

        final EnumConfigListWithString c = ConfigurationBuilder.buildDefault(EnumConfigListWithString.class);

        Assert.assertEquals("s1", c.foo.get(0));
        Assert.assertEquals("s2", c.foo.get(1));
    }

    @Test
    public final void withInteger()
    {
        XltProperties.getInstance().setProperty(KEY + "WithInteger.1", "1");
        XltProperties.getInstance().setProperty(KEY + "WithInteger.2", "2");

        final EnumConfigListWithInteger c = ConfigurationBuilder.buildDefault(EnumConfigListWithInteger.class);

        Assert.assertTrue(c.foo.get(0).equals(1));
        Assert.assertTrue(c.foo.get(1).equals(2));
    }

    @Test
    public final void withBoolean()
    {
        XltProperties.getInstance().setProperty(KEY + "WithBoolean.1", "true");
        XltProperties.getInstance().setProperty(KEY + "WithBoolean.2", "false");

        final EnumConfigListWithBoolean c = ConfigurationBuilder.buildDefault(EnumConfigListWithBoolean.class);

        Assert.assertTrue(c.foo.get(0));
        Assert.assertFalse(c.foo.get(1));
    }

    @Test
    public final void withInnerSpecialType()
    {
        XltProperties.getInstance().setProperty(KEY + "WithInnerType.1.boolean", "true");
        XltProperties.getInstance().setProperty(KEY + "WithInnerType.1.string", "s1");
        XltProperties.getInstance().setProperty(KEY + "WithInnerType.1.testclass2.boolean", "true");
        XltProperties.getInstance().setProperty(KEY + "WithInnerType.1.testclass2.string", "s2");

        final EnumConfigListWithInnerType i = ConfigurationBuilder.buildDefault(EnumConfigListWithInnerType.class);

        Assert.assertTrue(i.foo.get(0).b);
        Assert.assertEquals("s1", i.foo.get(0).s);

        Assert.assertTrue(i.foo.get(0).tc2.b2);
        Assert.assertEquals("s2", i.foo.get(0).tc2.s2);
}

    @Test
    public final void withUnsupportedDouble()
    {
        XltProperties.getInstance().setProperty(KEY + "WithUnsupportedDoouble.1", "1.1");
        XltProperties.getInstance().setProperty(KEY + "WithUnsupportedDoouble.2", "2.2");

        try
        {
            ConfigurationBuilder.buildDefault(EnumConfigListWithUnsupportedDouble.class);
            Assert.fail();
        }
        catch (final RuntimeException e)
        {
            final String s = e.getMessage();
            Assert.assertEquals("Could not initialize class java.lang.Double due to IllegalArgumentException - wrong number of arguments", s);
        }
    }
}

class EnumConfigListA
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "A", from = 0, to = 1)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListB1
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "B1", from = 1, to = 3, compact = false, stopOnGap = false)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListB2
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "B2", from = 1, to = 3, compact = true, stopOnGap = false)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListC
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "C", from = 2, to = 4, stopOnGap = true)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListWithInnerType
{
    @EnumProperty(clazz = TestClass1.class, key = EnumConfigListTest.KEY + "WithInnerType", from = 1, to = 2, stopOnGap = true)
    public EnumConfigList<TestClass1> foo;
}

class EnumConfigListRequiredFalse
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "RequiredFalse", from = 1, to = 4, required = false)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListRequiredTrue
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "RequiredTrue", from = 1, to = 4, required = true)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListDoNotPermitMissing
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "DoNotPermitMissing", from = 2, to = 4, stopOnGap = false, permitMissing = false)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListWithInit
{
    @EnumProperty(clazz = TestClassWithInit.class, key = EnumConfigListTest.KEY + "WithInit", from = 1, to = 2, stopOnGap = true)
    public EnumConfigList<TestClassWithInit> foo;
}

class EnumConfigListWithString
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WithString", from = 1, to = 2)
    public EnumConfigList<String> foo;
}

class EnumConfigListWithBoolean
{
    @EnumProperty(clazz = Boolean.class, key = EnumConfigListTest.KEY + "WithBoolean", from = 1, to = 2)
    public EnumConfigList<Boolean> foo;
}

class EnumConfigListWithInteger
{
    @EnumProperty(clazz = Integer.class, key = EnumConfigListTest.KEY + "WithInteger", from = 1, to = 2)
    public EnumConfigList<Integer> foo;
}

class EnumConfigListWithUnsupportedDouble
{
    @EnumProperty(clazz = Double.class, key = EnumConfigListTest.KEY + "WithUnsupportedDouble", from = 1, to = 2)
    public EnumConfigList<Double> foo;
}

class EnumConfigListWeightedA
{
    @EnumProperty(clazz = TestClass.class, key = EnumConfigListTest.KEY + "WA", from = 0, to = 1)
    public EnumConfigList<TestClass> foo;
}

class EnumConfigListWeightedB
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WB", from = 0, to = 1)
    public EnumConfigList<String> foo;
}

class EnumConfigListWeightedC
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WC", from = 0, to = 0)
    public EnumConfigList<String> foo;
}

class EnumConfigListWeightedD
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WD", from = 0, to = 0)
    public EnumConfigList<String> foo;
}

class EnumConfigListWeightedE
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WE", from = 0, to = 0)
    public EnumConfigList<String> foo;
}

class EnumConfigListWeightedF
{
    @EnumProperty(clazz = String.class, key = EnumConfigListTest.KEY + "WF", from = 0, to = 0)
    public EnumConfigList<String> foo;
}

class TestClass
{
    @Property(key = "string")
    public String s;

    @Property(key = "boolean")
    public boolean b;
}

class TestClass1
{
    @Property(key = "string")
    public String s;

    @Property(key = "boolean")
    public boolean b;

    @Property(key = "testclass2")
    public TestClass2 tc2;
}

class TestClass2
{
    @Property(key = "string")
    public String s2;

    @Property(key = "boolean")
    public boolean b2;
}

class TestClassWithInit implements Initable
{
    @Property(key = "string1")
    public String s1;

    @Property(key = "string2", required = false)
    public String s2;

    @Override
    public void init()
    {
        s2 = "init";
    }
}