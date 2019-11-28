package com.xceptance.loadtest.headless.pages.components.plp;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.hpu.LookUpResult;
import com.xceptance.loadtest.api.pages.Page;
import com.xceptance.loadtest.api.pages.components.Component;
import com.xceptance.loadtest.api.util.Context;

public class RefinementBar implements Component
{
    public final static RefinementBar instance = new RefinementBar();

    private final String CATEGORY_LOCALIZED_TEXT = Context.configuration().localizedText("css.category");

    @Override
    public LookUpResult locate()
    {
        return SearchResult.instance.locate().byCss(".refinement-bar");
    }

    @Override
    public boolean exists()
    {
        return locate().exists();
    }

    public LookUpResult getUnSelectedCategoryRefinements()
    {
        // this filter works Java 8 stream style, hence it keeps what returns true aka matches
        return locate().byCss(".refinements .refinement-" + CATEGORY_LOCALIZED_TEXT).byXPath("div//ul/li/a[not(span[contains(@class, 'selected')])]")
                        .filter(Page.VALIDLINKS).discard(Context.configuration().filterCategoryUrls.unweightedList(), e -> e.getAttribute("href"));
    }

    public LookUpResult getSelectedCategoryRefinements()
    {
        return locate().byCss(".refinements .refinement-" + CATEGORY_LOCALIZED_TEXT).byXPath("div//ul/li/a[span[contains(@class, 'selected')]]");
    }

    public LookUpResult getNonCategoryRefinements()
    {
        return locate().byCss(".refinements .refinement:not(.refinement-" + CATEGORY_LOCALIZED_TEXT + ")");
    }

    public LookUpResult getSelectedNonCategoryRefinements()
    {
        return locate().byCss(".refinements div[class*='refinement-']:not(.refinement-" + CATEGORY_LOCALIZED_TEXT + ") ul li:not(.disabled) input[data-href][checked]");
    }

    public static LookUpResult linkFromRefinement(final HtmlElement refinement)
    {
        return HPU.find().in(refinement).byCss("ul li:not(.disabled)").byXPath("a[not(span[contains(@class, 'selected')])]");
    }
}
