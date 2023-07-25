package com.xceptance.loadtest.api.hpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Assert;

import org.htmlunit.html.HtmlElement;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.api.hpu.strategy.CssContainsStrategy;
import com.xceptance.loadtest.api.hpu.strategy.CssIgnoreStrategy;
import com.xceptance.loadtest.api.hpu.strategy.EmptyLookupStrategy;
import com.xceptance.loadtest.api.util.ConcurrentLRUCache;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Look up result set.
 * 
 * @autor Xceptance Software Technologies
 */
public class LookUpResult
{
    /**
     * Cache for keeping the matching state of string and regexp to lower the resource consumption,
     * we do the same stuff over and over again all the time, so it matches once, it matches later
     * on again for sure
     */
    private static final ConcurrentLRUCache<FilterCacheKey, Boolean> filterCache = new ConcurrentLRUCache<>(2000);

    /**
     * A filter
     */
    private List<Predicate<HtmlElement>> filters;

    /**
     * Shall we assert
     */
    private boolean assertResult = false;

    /**
     * Assert message
     */
    private Optional<String> assertMessage = Optional.empty();

    /**
     * Lookup strategy.
     */
    private final Strategy strategy;

    /**
     * The raw results.
     */
    private List<?> results;

    /**
     * Constructor
     *
     * @param strategy
     *            lookup strategy
     */
    public LookUpResult(final Strategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Returns an empty result to avoid null handling later.
     * It is important that we don't share it and always get a fresh one
     * 
     * @return an empty result
     */
    public static LookUpResult DOESNOTEXIST()
    {
    	return new LookUpResult(new EmptyLookupStrategy());
    }
    
    /**
     * Get the lookup strategy.
     *
     * @return the lookup strategy
     */
    protected Strategy getStrategy()
    {
        return strategy;
    }

    /**
     * Get the locator description.
     *
     * @return
     */
    public String getLocatorDescription()
    {
        return getStrategy().getLocatorDescription();
    }

    /**
     * Does the described element exist?
     *
     * @return <code>true</code> if there's at least 1 result element or <code>false</code> otherwise.
     */
    public boolean exists()
    {
        return !raw().isEmpty();
    }

    /**
     * Run the assertion if desired
     *
     * @param assertFunction
     *            the function to executed, with the wild Runnable hack to be () -> void
     */
    private void assertIfNeeded(final Runnable assertFunction)
    {
        if (assertResult)
        {
            try
            {
                assertFunction.run();
            }
            catch (final Throwable t)
            {
                if (assertMessage.isPresent())
                {
                    throw new AssertionError(assertMessage.get(), t);
                }
                else
                {
                    throw t;
                }
            }
        }
    }

    /**
     * Get the unique element.
     *
     * @return the unique result element or <code>null</code> if no such unique element is present
     */
    public <T extends HtmlElement> T single()
    {
        // return unique result element or null
        if (raw().size() == 1)
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(0);
            return element;
        }
        else
        {
            assertIfNeeded(
                            () -> Assert.assertEquals("Too many results for: " + getStrategy().getLocatorDescription(), 1, count()));
        }

        return null;
    }

    /**
     * Get the first result element.
     *
     * @return the first result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T first()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(0);
            return element;
        }

        return null;
    }

    /**
     * Get the last result element.
     *
     * @return the last result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T last()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(raw().size() - 1);
            return element;
        }

        return null;
    }

    /**
     * Get a random result element.
     *
     * @return a random result element or <code>null</code> if no such element is present
     */
    public <T extends HtmlElement> T random()
    {
        if (!raw().isEmpty())
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(XltRandom.nextInt(raw().size()));
            return element;
        }

        return null;
    }

    /**
     * Get all result elements.
     *
     * @return all result elements (never <code>null</code>)
     */
    public <T extends HtmlElement> List<T> all()
    {
        @SuppressWarnings("unchecked")
        final List<T> elements = (List<T>) raw();
        return elements;
    }

    /**
     * Get the result element with given index.
     *
     * @param index
     *            result list index
     * @return the result element with given index from result list or <code>null</code> if no such element is present
     * @throws IllegalArgumentException
     *             if the index value is lower than <code>0</code>
     */
    public <T extends HtmlElement> T index(final int index)
        throws IllegalArgumentException
    {
        ParameterCheckUtils.isGreaterThan(index, -1, "index");

        assertIfNeeded(() -> Assert.fail(
                        "Given index is higher than maximum results index (" + (count() - 1) + ") for: " + getStrategy().getLocatorDescription()));

        if (count() > index)
        {
            @SuppressWarnings("unchecked")
            final T element = (T) raw().get(index);
            return element;
        }

        return null;
    }

    /**
     * Get the number of result elements.
     *
     * @return number of result elements
     */
    public int count()
    {
        return raw().size();
    }

    /**
     * Check if result elements count is as expected.
     *
     * @param expectedCount
     *            the expected results count
     * @return <code>true</code> if the results count is equal to the expected count, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if expected count is lower than <code>0</code>
     */
    public boolean isCount(final int expectedCount) throws IllegalArgumentException
    {
        // expectedCount must be 0 at least
        ParameterCheckUtils.isGreaterThan(expectedCount, -1, "expectedCount");

        final boolean result = count() == expectedCount;

        assertIfNeeded(
                        () -> Assert.assertEquals("Number of current results is not equal to number of expected results for: "
                                        + getStrategy().getLocatorDescription(), expectedCount, count()));

        return result;
    }

    /**
     * Check if result elements count is within the given range.
     *
     * @param min
     *            minimum asserted count
     * @param max
     *            maximum asserted count
     * @return <code>true</code> if results count is within given range, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if <code>min</code> is lower than <code>0</code> or <code>max</code> is lower than <code>min</code>
     */
    public boolean isCount(final int min, final int max) throws IllegalArgumentException
    {
        // min must be at least 0
        ParameterCheckUtils.isGreaterThan(min, -1, "min");
        // max must not be lower than min
        ParameterCheckUtils.isGreaterThan(max, min - 1, "max");

        final int count = count();
        final boolean result = min <= count && count <= max;

        if (result == false)
        {
            assertIfNeeded(
                            () -> Assert.fail("Number of current results (" + count() + ") is out of given boundaries for: " + getStrategy().getLocatorDescription()));
        }

        return result;
    }

    /**
     * Internal efficient cache because most of the test will be done over and over again
     *
     * @param text
     *            the text to check
     * @param regexp
     *            the regexp to apply
     */
    private boolean isMatchingFilter(final String text, final String regexp)
    {
        // do we know the result already?
        final FilterCacheKey cacheKey = new FilterCacheKey(text, regexp);
        Boolean result = filterCache.get(cacheKey);

        if (result == null)
        {
            result = RegExUtils.isMatching(text, regexp);
            filterCache.put(cacheKey, result);
        }

        return result;
    }

    /**
     * Get the unprocessed results.
     *
     * @return unprocessed results (never <code>null</code>)
     */
    public List<?> raw()
    {
        // lookup results if necessary
        if (results == null)
        {
            // look up of raw data
            List<?> values = getStrategy().lookup();

            if (filters != null)
            {
                // apply the filters
                for (final Predicate<HtmlElement> filter : filters)
                {
                    // new list to collect stuff in
                    final List<Object> filteredResults = new ArrayList<>(values.size());
                    for (final Object value : values)
                    {
                        // we deal only with HtmlElements, rest stays unfiltered
                        if (value instanceof HtmlElement)
                        {
                            if (filter.test((HtmlElement) value))
                            {
                                filteredResults.add(value);
                            }
                        }
                        else
                        {
                            // don't care
                            filteredResults.add(value);
                        }
                    }

                    // make the collected list the new one
                    values = filteredResults;
                }
            }

            // hand it out
            results = values;

        }

        assertIfNeeded(
                        () -> Assert.assertFalse(
                                        "No element found for: " + getStrategy().getLocatorDescription(),
                                        results.isEmpty()));

        return results;
    }

    /**
     * Enable result assertions(for example expected minimum/maximum amount of result elements).
     *
     * @return {@link ResultsAsserted} object to query the results from
     */
    public LookUpResult asserted()
    {
        assertResult = true;
        return this;
    }

    /**
     * Enable result assertions(for example expected minimum/maximum amount of result elements). Fail with given custom
     * message if necessary.
     *
     * @param message
     *            assertion-failed message
     * @return {@link ResultsAsserted} object to query the results from
     */
    public LookUpResult asserted(final String assertionMessage)
    {
        assertResult = true;
        assertMessage = assertionMessage != null ? Optional.of(assertionMessage) : assertMessage;
        return this;
    }

    /**
     * Set XPath lookup strategy and locator.
     *
     * @param locator
     *            XPath locator
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult byXPath(final String locator) throws IllegalArgumentException
    {
        return new By(strategy).byXPath(locator);
    }

    /**
     * Set CSS lookup strategy and locator.
     *
     * @param locator
     *            CSS locator
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult byCss(final String locator) throws IllegalArgumentException
    {
        return new By(strategy).byCss(locator);
    }

    /**
     * Set CSS ignore strategy and locator.
     *
     * @param locator
     *            specifies elements that must NOT be contained
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult hasNotCss(final String locator)
    {
        return new LookUpResult(new CssIgnoreStrategy(strategy, locator));
    }

    /**
     * Set CSS contains strategy and locator.
     *
     * @param locator
     *            specifies elements that must be contained
     * @return {@link LookUpResult} object to query the results from
     * @throws IllegalArgumentException
     *             if given locator is <code>null</code> or empty
     */
    public LookUpResult hasCss(final String locator)
    {
        return new LookUpResult(new CssContainsStrategy(strategy, locator));
    }

    /**
     * Sets the filter up either to keep stuff or to discard
     *
     * @param text
     *            list of strings or regexp to match
     * @param data
     *            a function that returns a string to match against
     * @param keepMatching
     *            true, keep what matches, false otherwise
     */
    private LookUpResult filter(final List<String> texts, final Function<HtmlElement, String> data, final boolean keepMatching)
    {
        if (filters == null)
        {
            filters = new ArrayList<>(4);
        }
        filters.add(new Filter(texts, data, keepMatching));

        return this;
    }

    /**
     * Removes all elements that match the function and text (regexp optionally).
     *
     * @param text a string or regexp to match
     * @param data a function that returns a string to match against
     */
    public LookUpResult discard(final String text, final Function<HtmlElement, String> data)
    {
        return filter(Arrays.asList(text), data, false);
    }

    /**
     * Removes all elements that match the function and text list (regexp optionally).
     *
     * @param text
     *            list of strings or regexp to match
     * @param data
     *            a function that returns a string to match against
     */
    public LookUpResult discard(final List<String> texts, final Function<HtmlElement, String> data)
    {
        return filter(texts, data, false);
    }

    /**
     * Keeps all matching elements
     *
     * @param text
     *            a string or regexp to match
     * @param data
     *            a function that returns a string to match against
     */
    public LookUpResult keep(final String text, final Function<HtmlElement, String> data)
    {
        return filter(Arrays.asList(text), data, true);
    }

    /**
     * Keeps all matching elements
     *
     * @param text
     *            list of strings or regexp to match
     * @param data
     *            a function that returns a string to match against
     */
    public LookUpResult keep(final List<String> texts, final Function<HtmlElement, String> data)
    {
        return filter(texts, data, true);
    }

    /**
     * Filter based on predicates, Java 8 style. This means, what matches is kept.
     *
     * @param filter
     *            a predicate to apply, if true, we keep the content, if false we discard it.
     */
    public LookUpResult filter(final Predicate<HtmlElement> filter)
    {
        // lazy array list
        if (filters == null)
        {
            filters = new ArrayList<>(4);
        }

        filters.add(filter);
        return this;
    }

    class Filter implements Predicate<HtmlElement>
    {
        public final List<String> texts;
        public final Function<HtmlElement, String> data;
        public final boolean keepMatching;

        public Filter(final List<String> texts, final Function<HtmlElement, String> data, final boolean keepMatching)
        {
            this.texts = texts;
            this.data = data;
            this.keepMatching = keepMatching;
        }

        @Override
        public boolean test(final HtmlElement element)
        {
            // get us the string based on our function
            final String text = data.apply(element);

            // run a stream to filter and due to findFirst, we shortcut when we got
            // a result to save cycles
            final Optional<String> match = texts
                            .stream()
                            .filter(regexp -> isMatchingFilter(text, regexp))
                            .findFirst();

            // if it matches and we want to keep matching
            final boolean matches = match.isPresent();
            if (matches == true && keepMatching == true)
            {
                return true;
            }
            else if (matches == false && keepMatching == false)
            {
                return true;
            }

            return false;
        }
    }

    /**
     * Our cache entry. Has to be static to avoid problems with the references being held of the
     * parent.
     *
     * @author rschwietzke
     *
     */
    static class FilterCacheKey
    {
        public String text;
        public String regexp;

        public final int hashcode;

        public FilterCacheKey(final String text, final String regexp)
        {
            if (text == null || regexp == null)
            {
                throw new NullPointerException("Invalid cache entry");
            }

            this.text = text;
            this.regexp = regexp;

            // fix up the hashcode
            int result = 31 + regexp.hashCode();
            result = 31 * result + text.hashCode();

            this.hashcode = result;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return hashcode;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj)
        {
            if (obj == null)
            {
                return false;
            }

            if (obj.hashCode() != this.hashCode())
            {
                return false;
            }

            final FilterCacheKey o = (FilterCacheKey) obj;
            if (o.text.equals(this.text) && o.regexp.equals(this.regexp))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}