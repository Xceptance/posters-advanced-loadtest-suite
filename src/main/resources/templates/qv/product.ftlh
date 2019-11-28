<div class="product-quickview product-master col product-wrapper product-detail" data-pid="${pv.product.id}">
    <div class="row">
        <!-- Product Images Carousel -->
        <div class="primary-images col-12 col-sm-6">
            <div id="pdpCarousel" class="carousel slide" data-interval="0">
                NO CAROUSEL WHEN LOAD TESTING
            </div>
        </div>
        
        <!-- Product Name and Number -->
        <div class="col-sm-6">
            <section>
                <h1 class="product-name">${pv.product.productName}</h1>
            </section>
            <div class="detail-panel">
                <!-- Attributes -->
                <section class="attributes">
                    <div class="col-12">
                        <!-- variation attributes -->
                        <#if pv.product.variationAttributes??>
                        <#list pv.product.variationAttributes as attr>
                            <div data-attr="${attr.id}">
                                <#if attr.swatchable>
                                    <div class="attribute">
                                        <!-- Select <Attribute> Label -->
                                        <label class="${attr.id}">Select ${attr.displayName}</label>
                                        
                                        <#list attr.values as value>
                                            <!-- Circular Swatch Image Attribute Values -->
                                            <a <#if value.url??>href="${value.url}"</#if>>
                                                <span data-attr-value="${value.value}" class="color-value swatch-circle swatch-value <#if value.selected>selected</#if> <#if value.selectable>selectable</#if>" 
                                                    style="background-image: url(${value.images.swatch[0].url})">
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
                            </div> <!-- data-attr -->
                        </#list>
                        </#if>
                    </div>

                    <hr>
                    <!-- Applicable Promotions -->
                    <div class="row promotions">
                        <div class="col-12 align-self-center">
                            <#if pv.product.promotions??>
                                <#list pv.product.promotions as promotion>
                                    <div class="callout" title="${promotion.calloutMsg}">
                                        ${promotion.calloutMsg}
                                    </div>
                                </#list>
                            </#if>
                        </div>
                    </div>
                </section>

                <div class="row">
                    <div class="col-md-10 col-12 product-options">
                        <#list pv.product.options as option>
                            <div class="row product-option" data-option-id="${option.id}">
                                <div class="col-12">
                                    <label>${option.name}</label>
                                    <select class="options-select custom-select form-control">
                                        <#list option.values as v>
                                            <option 
                                                <#if option.selectedValueId == v.id>selected="selected"</#if> 
                                                    data-value-id="${v.id}"
                                                    value="${v.url}">[${v.price}] ${v.displayValue}
                                            </option>
                                        </#list>
                                    </select>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>