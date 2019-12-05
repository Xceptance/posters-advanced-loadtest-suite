package com.xceptance.loadtest.posters.data;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xceptance.loadtest.api.hpu.HPU;
import com.xceptance.loadtest.api.render.Templates;
import com.xceptance.loadtest.posters.jsondata.ProductJSON;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.engine.XltWebClient;

public class ProductVariationTest
{
    @Test
    public void testColorSelect1()
    {
        final Gson gson = new GsonBuilder().create();
        final ProductJSON pv = gson.fromJson(
                        new InputStreamReader(this.getClass().getResourceAsStream("/json/25592211-01-color-selected.json")),
                        ProductJSON.class);

        Assert.assertEquals("String covertion failed.", "Product-Variation", pv.action);
        Assert.assertTrue("Double convertion failed", Double.compare(128, pv.product.price.sales.value) == 0);
        Assert.assertEquals("HashMap String convertion failed.", "This item is currently not available.", pv.resources.get("label_allnotavailable"));

        Assert.assertTrue("Color not selected", pv.product.variationAttributes.get(0).values.get(0).selected);
        Assert.assertFalse("Size selected (but should not)", pv.product.variationAttributes.get(1).values.get(0).selected);
        Assert.assertEquals("Wrong Quantitiy", pv.product.selectedQuantity, 1);

        Assert.assertNull("List price should be null", pv.product.price.list);
    }

    @Test
    public void testColorSelect2()
    {
        final Gson gson = new GsonBuilder().create();
        final ProductJSON pv = gson.fromJson(
                        new InputStreamReader(this.getClass().getResourceAsStream("/json/25592211-02-color-and-size-selected.json")),
                        ProductJSON.class);

        Assert.assertEquals("String covertion failed.", "Product-Variation", pv.action);
        Assert.assertTrue("Double convertion failed", Double.compare(128, pv.product.price.sales.value) == 0);
        Assert.assertEquals("HashMap String convertion failed.", "This item is currently not available.", pv.resources.get("label_allnotavailable"));
        Assert.assertEquals("Array convertion failed", 2, pv.product.variationAttributes.size());

        Assert.assertTrue("Color not selected", pv.product.variationAttributes.get(0).values.get(0).selected);
        Assert.assertTrue("Size not selected", pv.product.variationAttributes.get(1).values.get(0).selected);
        Assert.assertEquals("Wrong Quantitiy", pv.product.selectedQuantity, 1);

        Assert.assertNull("List price should be null", pv.product.price.list);
    }

    @Test
    public void testColorSelect3()
    {
        final Gson gson = new GsonBuilder().create();
        final ProductJSON pv = gson.fromJson(
                        new InputStreamReader(
                                        this.getClass().getResourceAsStream("/json/25592211-03-color-and-size-quantity-selected.json")),
                        ProductJSON.class);

        Assert.assertEquals("String covertion failed.", "Product-Variation", pv.action);
        Assert.assertTrue("Double convertion failed", Double.compare(128, pv.product.price.sales.value) == 0);
        Assert.assertEquals("HashMap String convertion failed.", "This item is currently not available.", pv.resources.get("label_allnotavailable"));
        Assert.assertEquals("Array convertion failed", 2, pv.product.variationAttributes.size());

        Assert.assertTrue("Color not selected", pv.product.variationAttributes.get(0).values.get(0).selected);
        Assert.assertTrue("Size not selected", pv.product.variationAttributes.get(1).values.get(0).selected);
        Assert.assertEquals("Wrong Quantitiy", pv.product.selectedQuantity, 3);

        Assert.assertNull("List price should be null", pv.product.price.list);
    }

    @Test
    public void testListAndSalesPrice()
    {
        final Gson gson = new GsonBuilder().create();
        final ProductJSON pv = gson.fromJson(
                        new InputStreamReader(this.getClass().getResourceAsStream("/json/25604455-first-select-two-prices.json")),
                        ProductJSON.class);

        Assert.assertEquals("String covertion failed.", "Product-Variation", pv.action);
        Assert.assertTrue("Double convertion failed", Double.compare(49.99, pv.product.price.sales.value) == 0);
        Assert.assertEquals("HashMap String convertion failed.", "This item is currently not available.", pv.resources.get("label_allnotavailable"));
        Assert.assertEquals("Array convertion failed", 3, pv.product.variationAttributes.size());

        Assert.assertTrue("Color not selected", pv.product.variationAttributes.get(0).values.get(0).selected);
        Assert.assertFalse("Size selected", pv.product.variationAttributes.get(1).values.get(0).selected);
        Assert.assertEquals("Wrong Quantitiy", pv.product.selectedQuantity, 1);

        Assert.assertNotNull("List price should be null", pv.product.price.list);
        Assert.assertTrue("Wrong Quantitiy", Double.compare(pv.product.price.list.value, 69.5) == 0);
    }

    @Test
    public void testTemplating() throws Exception
    {
        final Gson gson = new GsonBuilder().create();
        final ProductJSON pv = gson.fromJson(
                        new InputStreamReader(this.getClass().getResourceAsStream("/json/25604455-first-select-two-prices.json")),
                        ProductJSON.class);

        // our html is several templates, build it and swap it in
        final Map<String, Object> data = new HashMap<>();
        data.put("pv", pv);
        final String result = Templates.process("/templates/pdp/product-detail-variation.ftlh", data);

        final HtmlPage createdPage = createPage(result);

        HPU.find().in(createdPage).byCss(".xc-renderedVariations").asserted().exists();

        System.out.println(result);

        // array
        Assert.assertEquals("fakeurl", HPU.find().in(createdPage).byCss(".quantity-select > option").asserted().random().getAttribute("data-url"));
        // number
        Assert.assertEquals("$49.99", HPU.find().in(createdPage).byCss(".price .sales > .value").asserted().single().getTextContent());
        Assert.assertEquals("$69.50", HPU.find().in(createdPage).byCss(".price .strike-through > .value").asserted().single().getTextContent());
        // hashmap
        Assert.assertEquals("In Stock", HPU.find().in(createdPage).byCss(".availability-msg").asserted().single().getTextContent().trim());

    }

    private HtmlPage createPage(final String pageString) throws Exception
    {
        XltProperties.getInstance().setProperty("com.xceptance.xlt.http.filter.include", "http://localhost");
        final StringWebResponse webResponse = new StringWebResponse(pageString, new URL("http://localhost"));

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
}
