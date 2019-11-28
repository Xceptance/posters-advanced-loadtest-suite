package util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.engine.XltWebClient;

public class TestUtils
{
    public static Object invokeMethod(final Object o, final String fieldName)
    {
        return invokeMethod(o, fieldName, new Class<?>[0]);
    }

    public static Object invokeMethod(final Object o, final String fieldName, final Class<?>[] paramTypes, final Object... params)
    {
        try
        {
            final Method method = o.getClass().getDeclaredMethod(fieldName, paramTypes);
            method.setAccessible(true);
            return method.invoke(o, params);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not invoke method " + fieldName
                            + " from object of class " + o.getClass().getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(final Object o, final String fieldName)
    {
        try
        {
            final Field unit = o.getClass().getDeclaredField(fieldName);
            unit.setAccessible(true);
            return (T) unit.get(o);

            // return (T) Whitebox.getField(o.getClass(), fieldName).get(o);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not retrieve field " + fieldName
                            + " from object of class " + o.getClass().getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }
    }

    public static void setFieldValue(final Object o, final String fieldName, final Object value)
    {
        try
        {
            final Field unit = o.getClass().getDeclaredField(fieldName);
            unit.setAccessible(true);
            // remove final
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(unit, unit.getModifiers() & ~Modifier.FINAL);
            // set value
            unit.set(o, value);
            // Whitebox.getField(o.getClass(), fieldName).set(o, value);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not set field " + fieldName
                            + " from object of class " + o.getClass().getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }
    }

    public static void setStaticFieldValue(final Class<?> class1, final String fieldName, final Object value)
    {
        try
        {
            final Field unit = class1.getDeclaredField(fieldName);
            unit.setAccessible(true);
            // remove final
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(unit, unit.getModifiers() & ~Modifier.FINAL);
            // set value
            unit.set(null, value);
            // Whitebox.getField(o.getClass(), fieldName).set(o, value);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not set static field " + fieldName
                            + " from class " + class1.getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }
    }

    /**
     * Returns an empty fake WebPage
     *
     * @return
     * @throws Exception
     */
    public static HtmlPage getFakePage() throws Exception
    {
        final String emptyPageString = "<html><head></head><body></body></html>";

        return getFakePage(emptyPageString);
    }

    /**
     * Returns a fake WebPage from a given String
     *
     * @return
     * @throws Exception
     */
    public static HtmlPage getFakePage(final String pageContent) throws MalformedURLException, IOException
    {
        final StringWebResponse webResponse = new StringWebResponse(pageContent, new URL("http://localhost"));

        final WebClient webClient = new XltWebClient();
        try
        {
            return (HtmlPage) webClient.getPageCreator().createPage(webResponse, webClient.getCurrentWindow());
        }
        finally
        {
            webClient.close();
        }
    }

    /**
     * Invokes a private static method on a specified class, without parameters
     *
     * @param class1
     * @param fieldName
     * @return
     */
    public static Object invokeStaticMethod(final Class<?> class1, final String fieldName)
    {
        return invokeStaticMethod(class1, fieldName, new Class<?>[0]);
    }

    /**
     * Invokes a private static method on a specified class, with the given parameter types and
     * parameters
     *
     * @param class1
     * @param fieldName
     * @return
     */
    public static Object invokeStaticMethod(final Class<?> class1, final String fieldName, final Class<?>[] paramTypes, final Object... params)
    {
        try
        {
            final Method method = class1.getDeclaredMethod(fieldName, paramTypes);
            method.setAccessible(true);
            return method.invoke(null, params);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not invoke static method " + fieldName
                            + " from object of class " + class1.getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }

    }

    public static <T> T getStaticFieldValue(final Class<?> class1, final String fieldName)
    {
        try
        {
            final Field unit = class1.getDeclaredField(fieldName);
            unit.setAccessible(true);
            return (T) unit.get(null);

            // return (T) Whitebox.getField(o.getClass(), fieldName).get(o);
        }
        catch (final Throwable ex)
        {
            throw new AssertionError("Could not retrieve field " + fieldName
                            + " from object of class " + class1.getSimpleName()
                            + " because of " + ex.getClass().getSimpleName(), ex);
        }
    }
}
