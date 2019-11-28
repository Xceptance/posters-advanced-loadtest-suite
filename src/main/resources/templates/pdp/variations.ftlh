
<!--
     Attributes and Quantity, we don't care right now that quickview might be different than PDP.
     That is very unfortunate and does not really make our life easier.
 -->
<div class="xc-renderedVariations col-md-10 col-12">

    <#list variationAttributes as attr>
    
        <div data-attr="${attr.id}">

            <!-- the wild quantity hack, because quantity is mixed into attributes -->
            <#if attr?is_last>
                <!-- Quantity Drop Down Menu -->
                <div class="quantity col-sm-6">
                    <label>Quantity</label>
                    <select class="quantity-select custom-select form-control">
                        <#list quantities as quantity>
                            <option 
                                <#if quantity.selected>selected="selected"</#if> value="${quantity.value}"
                                data-url="${quantity.url}"
                                >
                                ${quantity.value}
                            </option>  
                        </#list>
                    </select>
                </div>
            </#if>
            
            <#if attr.swatchable>

                <div class="attribute">
                    <!-- Select <Attribute> Label -->
                    <label class="${attr.id}">Select ${attr.displayName}</label>
                    
                    <#list attr.values as value>
                        <!-- Circular Swatch Image Attribute Values -->
                        <a <#if value.url??>href="${value.url}"</#if>>
                            <span data-attr-value="${value.value}" class="color-value swatch-circle swatch-value <#if value.selected>selected</#if> <#if value.selectable>selectable</#if>" 
                                <#if value.images.swatch?size != 0>
                                    style="background-image: url(${value.images.swatch[0].url})"
                                </#if>>
                            </span>
                        </a>
                    </#list>
                </div>
            
            <#else>
            
                <div class="attribute">
                    <!-- Select <Attribute> Label -->
                    <label class="${attr.id}">Select ${attr.displayName}</label>
    
                    <!-- Attribute Values Drop Down Menu -->
                    <select class="custom-select form-control select-${attr.id}">
                        <option value="">Select ${attr.displayName}</option>
                        <#list attr.values as value>
                            <#if value.url??>
                                <option <#if value.selected>selected="selected"</#if> value="${value.url}" <#if !value.selectable>disabled</#if> data-attr-value="${value.value}">${value.displayValue}</option>
                            <#else>
                                <option <#if value.selected>selected="selected"</#if> <#if !value.selectable>disabled</#if> data-attr-value="${value.value}">${value.displayValue}</option>
                            </#if>
                        </#list>
                    </select>
                </div>
            
            </#if>
        </div>
    
    </#list>
</div>    

