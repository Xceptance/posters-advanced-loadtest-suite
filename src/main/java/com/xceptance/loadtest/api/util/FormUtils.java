package com.xceptance.loadtest.api.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.models.pages.Page;
import com.xceptance.xlt.api.util.BasicPageUtils;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Utility for better HTML form handling of a HTML page.
 * 
 * @autor Xceptance Software Technologies
 */
public class FormUtils extends BasicPageUtils
{
    /**
     * <code>false</code> if test is executed in load test mode. Outside a load test (usually debug mode) test suite
     * performance has not the top priority but more logging will help to identify issues.
     */
    private static final boolean isNotLoadTestMode = !Context.isLoadTest;

    /**
     * Finds the HTML select element with the given ID in the specified form and selects the option with the passed
     * value.
     *
     * @param selectId
     *            the ID of the select element
     * @param optionValue
     *            the value of the option element to select
     * @return selected option element
     */
    public static HtmlOption selectByID(final String selectId, final String optionValue)
    {
        // Convert ID to locator and proceed with selection.
        return select(getByID(selectId), optionValue);
    }

    /**
     * Finds the HTML select element with the given locator in the specified form and selects the option with the passed
     * value.
     *
     * @param selectLocator
     *            the locator of the select element
     * @param optionValue
     *            the value of the option element to select
     * @return selected option element
     */
    public static HtmlOption select(final LookUpResult selectLocator, final String optionValue)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(selectLocator);
            ParameterCheckUtils.isNotNull(optionValue, "optionValue");
        }

        // Get the select element.
        final HtmlSelect select = getAsserted(selectLocator);

        // Check if given option value fits to any of the select's option.
        try
        {
            final HtmlOption option = select.getOptionByValue(optionValue);
            selectOption(select, option);

            return option;
        }
        catch (final ElementNotFoundException e)
        {
            Assert.fail("Value '" + optionValue + "' not present in '" + selectLocator.getLocatorDescription() + "'");
        }

        return null;
    }

    /**
     * Finds the HTML select element with the given locator in the specified form and set one of the options selected
     * randomly. Disabled option will be ignored. This method allows you to exclude first and last option.
     *
     * @param selectLocator
     *            the locator of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     * @return selected option element
     */
    public static HtmlOption selectRandomly(final LookUpResult selectLocator, final boolean excludeFirst, final boolean excludeLast)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(selectLocator);
        }

        // Get the select element.
        final HtmlSelect select = getAsserted(selectLocator);

        // Select by random.
        return selectRandomly(select, excludeFirst, excludeLast);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and selects one of the options randomly.
     *
     * @param selectId
     *            the ID of the select element
     * @return selected option element
     */
    public static HtmlOption selectRandomlyByID(final String selectId)
    {
        // Select while not excluding any option.
        return selectRandomlyByID(selectId, false, false);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and selects one of the options randomly.
     * This method allows you to exclude the first option.
     *
     * @param selectId
     *            the ID of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @return selected option element
     */
    public static HtmlOption selectRandomlyByID(final String selectId, final boolean excludeFirst)
    {
        // Select while not excluding any option.
        return selectRandomlyByID(selectId, excludeFirst, false);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and set one of the options selected
     * randomly. Disabled option will be ignored. This method allows you to exclude first and last option.
     *
     * @param selectId
     *            the ID of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     * @return selected option element
     */
    public static HtmlOption selectRandomlyByID(final String selectId, final boolean excludeFirst, final boolean excludeLast)
    {
        return selectRandomly(getByID(selectId), excludeFirst, excludeLast);
    }

    /**
     * Selects one of the options of the given select randomly. Disabled options will be ignored. This method allows you
     * to exclude first and last option.
     *
     * @param select
     *            the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     * @return selected option element
     */
    public static HtmlOption selectRandomly(final HtmlSelect select, final boolean excludeFirst, final boolean excludeLast)
    {
        if (isNotLoadTestMode)
        {
            ParameterCheckUtils.isNotNull(select, "select");
        }

        // Get the option elements.
        final List<HtmlOption> origOptions = new ArrayList<>(select.getOptions());

        // If we want to remove the first option, we need to do it before we start filtering
        if (excludeFirst && origOptions.size() > 0)
        {
            origOptions.remove(0);
        }

        // Do the same for the last element
        final int size = origOptions.size();
        if (excludeLast && size > 0)
        {
            // zero based access - hence we need to subtract 1
            origOptions.remove(size - 1);
        }

        // Extract all non-disabled options.
        final List<HtmlOption> options = new ArrayList<>(origOptions.size());
        for (int i = 0; i < origOptions.size(); i++)
        {
            final HtmlOption option = origOptions.get(i);
            if (!option.isDisabled())
            {
                options.add(option);
            }
        }

        // Pick one of the non-disabled options.
        // final HtmlOption option = pickOneRandomly(options, excludeFirst, excludeLast);
        final HtmlOption option = BasicPageUtils.pickOneRandomly(options);

        selectOption(select, option);

        return option;
    }

    public static void selectOption(final HtmlOption option)
    {
        final HtmlSelect select = option.getEnclosingSelect();
        selectOption(select, option);
    }

    private static void selectOption(final HtmlSelect select, final HtmlOption option)
    {
        // Remove selection marker from previously selected option
        if (!select.isMultipleSelectEnabled())
        {
            final LookUpResult previouslySelectedOption = HPU.find().in(select).byCss("option[selected]");
            if (previouslySelectedOption.exists())
            {
                final HtmlOption oldOption = previouslySelectedOption.single();
                oldOption.setSelected(false);
                oldOption.removeAttribute("selected");
            }
        }

        // Select the given option value
        option.setSelected(true);
        option.setAttribute("selected", "selected");

        if (isNotLoadTestMode)
        {
            final String idOrName = select != null ? getIdOrName(select) : getIdOrName(option);
            XltLogger.runTimeLogger.info(String.format("Selecting option for '%s' with value '%s'", idOrName, option.getValueAttribute()));
        }
    }

    /**
     * Returns the value of the "id" attribute or, if there is no such attribute, the "name" attribute of the given HTML
     * element.
     *
     * @param element
     *            the HTML element in question
     * @return the ID or the name or <code>&lt;unnamed&gt;</code> if neither an ID nor a name is available
     */
    private static String getIdOrName(final HtmlElement element)
    {
        // parameter check
        ParameterCheckUtils.isNotNull(element, "element");

        // Get the ID.
        String result = element.getId();

        // If no ID is available get the name.
        if (StringUtils.isEmpty(result))
        {
            result = element.getAttribute("name");
        }

        // If even no name is available fall back to a generic value.
        if (StringUtils.isEmpty(result))
        {
            result = "<unnamed>";
        }

        return result;
    }

    /**
     * Finds the HTML check box input element with the given ID in the specified form and sets its value.
     *
     * @param checkboxId
     *            the ID of the check box
     * @param checked
     *            the new check box value
     * @return the modified checkbox element
     */
    public static HtmlCheckBoxInput checkCheckboxByID(final String checkboxId, final boolean checked)
    {
        return checkCheckbox(getByID(checkboxId), checked);
    }

    /**
     * Finds the HTML check box input element with the given locator in the specified form and sets its value.
     *
     * @param checkboxLocator
     *            the locator of the check box
     * @param checked
     *            the new check box value
     * @return the modified checkbox element
     */
    public static HtmlCheckBoxInput checkCheckbox(final LookUpResult checkboxLocator, final boolean checked)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(checkboxLocator);
            ParameterCheckUtils.isNotNull(checked, "isChecked");
        }

        // Get the check box and set the selection state.
        final HtmlCheckBoxInput checkbox = getAsserted(checkboxLocator);

        return checkCheckbox(checkbox, checked);
    }

    public static HtmlCheckBoxInput checkCheckbox(final HtmlCheckBoxInput checkbox, final boolean checked)
    {
        // update object state
        checkbox.setChecked(checked);

        // update attribute
        if (checked)
        {
            checkbox.setAttribute("checked", "checked");
        }
        else
        {
            checkbox.removeAttribute("checked");
        }

        if (isNotLoadTestMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting checkbox value: %s = %b", getIdOrName(checkbox), checked));
        }

        return checkbox;
    }

    /**
     * Finds the HTML input element with the given locator and sets its value.
     *
     * @param inputLocator
     *            the locator of the input element
     * @param value
     *            the new value
     * @return the modified input element
     */
    public static HtmlInput setInputValue(final LookUpResult inputLocator, final String value)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(inputLocator);
        }

        // Get the input element and set the new value.
        final HtmlInput input = getAsserted(inputLocator);
        input.setValueAttribute(value);

        if (isNotLoadTestMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting input value: %s = %s", getIdOrName(input), value));
        }

        return input;
    }

    /**
     * Finds the HTML input element with the given ID in the specified form and sets its value.
     *
     * @param inputId
     *            the ID of the input element
     * @param value
     *            the new value
     * @return the modified input element
     */
    public static HtmlInput setInputValueByID(final String inputId, final String value)
    {
        return setInputValue(getByID(inputId), value);
    }

    /**
     * Finds the HTML radio button element with the given locator in the specified form and checks it.
     *
     * @param radioLocator
     *            the locator of the radio button
     * @return the modified radio button
     */
    public static HtmlRadioButtonInput checkRadioButton(final LookUpResult radioLocator)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(radioLocator);
        }

        // Get the requested radio button and set the given selection state.
        return checkRadioButton(getAsserted(radioLocator));
    }

    /**
     * Finds the HTML radio button element with the given ID in the specified form and checks it.
     *
     * @param id
     *            the ID of the radio button
     * @return the modified radio button
     */
    public static HtmlRadioButtonInput checkRadioButtonByID(final String id)
    {
        return checkRadioButton(getByID(id));
    }

    public static HtmlRadioButtonInput checkRadioButton(final HtmlRadioButtonInput radioButton)
    {
        // Remove checked state from previously checked radio button
        final HtmlForm form = radioButton.getEnclosingForm();
        if (form != null)
        {
            final LookUpResult previouslyChecked = HPU.find().in(form).byCss("[name='" + radioButton.getNameAttribute() + "'][checked]");
            if (previouslyChecked.exists())
            {
                final HtmlRadioButtonInput oldRadioButton = previouslyChecked.single();
                oldRadioButton.setChecked(false);
                oldRadioButton.removeAttribute("checked");
            }
        }
        else
        {
            // Comment the next line if you know what you're doing only.
            Assert.fail(
                    "RadioButton is NOT inside a form. This might be due to broken HTML. It's strongly recommended to fix HTML issues first, then continue testing.");
        }

        // Check current radio button
        radioButton.setChecked(true);
        radioButton.setAttribute("checked", "checked");

        if (isNotLoadTestMode)
        {
            XltLogger.runTimeLogger.info(String.format("Checking radio button: %s", getIdOrName(radioButton)));
        }

        return radioButton;
    }

    /**
     * Finds the HTML textarea element (specified by the given locator) and sets its value.
     *
     * @param textAreaLocator
     *            the locator of the textarea
     * @param value
     *            the new value
     * @return the modified textarea element
     */
    public static HtmlTextArea setTextAreaValue(final LookUpResult textAreaLocator, final String value)
    {
        if (isNotLoadTestMode)
        {
            isNotNull(textAreaLocator);
        }

        // Get the input element and set the new value.
        final HtmlTextArea textArea = getAsserted(textAreaLocator);
        textArea.setText(value);

        if (isNotLoadTestMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting textarea value: %s = %s", getIdOrName(textArea), value));
        }

        return textArea;
    }

    /**
     * Finds the HTML textarea element with the given ID in the specified form and sets its value.
     *
     * @param textAreaId
     *            the ID of the textarea element
     * @param value
     *            the new value
     * @return the modified textarea element
     */
    public static HtmlTextArea setTextAreaValueByID(final String textAreaId, final String value)
    {
        return setTextAreaValue(getByID(textAreaId), value);
    }

    /**
     * Get locator object for given ID
     *
     * @param id
     *            the ID to lookup
     * @return the built locator
     */
    private static LookUpResult getByID(final String id)
    {
        return Page.find().byId(id);
    }

    /**
     * Get the element selected by the given locator. The element is asserted to exist and to be unique.
     *
     * @param locator
     *            describes the element to lookup
     * @return the selected element
     */
    private static <T extends HtmlElement> T getAsserted(final LookUpResult locator)
    {
        return locator.asserted().single();
    }

    /**
     * Check that the given locator is not <code>null</code>.
     *
     * @param locator
     *            the locator to check
     * @throws IllegalArgumentException
     *             if the passed argument is <code>null</code>
     */
    private static void isNotNull(final LookUpResult locator) throws IllegalArgumentException
    {
        if (locator == null)
        {
            throw new IllegalArgumentException("Locator must not be NULL");
        }
    }
}