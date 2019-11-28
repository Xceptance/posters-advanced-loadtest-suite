<div class="container product-detail set-item" data-pid="${pv.product.id}">
    <div class="row">
        <div class="col-12">
            <!-- Product Name -->
            <div class="row">
                <div class="hidden-md-up col-sm-12">
                    <h3 class="product-name">
                        <a href="${pv.product.selectedProductUrl}">
                            ${pv.product.productName}
                        </a>
                    </h3>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Product Images Carousel -->
        <div class="primary-images col-12 col-sm-6">
            <div id="pdpCarousel" class="carousel slide" data-interval="0">
                NO CAROUSEL WHEN LOAD TESTING
            </div>
        </div>

        <div class="col-12 col-sm-6">
            <!-- Product Name -->
            <h3 class="product-name hidden-sm-down">
                <a href="${pv.product.selectedProductUrl}">
                    ${pv.product.productName}
                </a>
            </h3>

            <div class="product-number-rating clearfix">
                <!-- Product Number -->
                <div class="product-number hidden-xs-down pull-left">
                    Item No.
                    <span class="product-id">${pv.product.id}</span>
                </div>
            </div>

            <div class="row justify-content-center">
                <div class="col-md-10 col-12">
                    <!-- variation attributes -->
                    <#if pv.product.variationAttributes??>
                    <#list pv.product.variationAttributes as attr>
                        <div data-attr="${attr.id}">

                            <!-- the wild quantity hack, because quantity is mixed into attributes -->
                            <#if attr?is_last>
                                <!-- Quantity Drop Down Menu -->
                                <div class="quantity col-sm-6">
                                    <label>Quantity</label>
                                    <select class="quantity-select custom-select form-control">
                                        <#list pv.product.quantities as quantity>
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
                    </#if> <!-- if variation attributes exist -->

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

                <!-- Availability -->
                <div class="availability col-md-10 col-12 product-availability" data-ready-to-order="${pv.product.readyToOrder?c}" data-available="${pv.product.available?c}">
                    <div class="row">
                        <div class="col-12 col-sm-3">
                            <label class="availability">Availability:</label>
                        </div>
                
                        <div class="col-12 col-sm-9">
                            <ul class="list-unstyled availability-msg">
                                <#list pv.product.availability.messages as msg>
                                    <li>${msg}</li>
                                <#else>
                                    <li>Select Styles for Availability</li>
                                </#list>
                            </ul>
                        </div>
                    </div>
                </div>

                <!-- Applicable Promotions -->
                <div class="col-md-10 col-12 promotions">
                    <#if pv.product.promotions??>
                        <#list pv.product.promotions as promotion>
                            <div class="callout" title="${promotion.calloutMsg}">
                                ${promotion.calloutMsg}
                            </div>
                        </#list>
                    </#if>
                </div>

                <div class="col-md-10 col-12">
                    <hr>
                </div>

                <div class="col-12">
                    <!-- Prices -->
                    <div class="prices">
                        ${pv.product.price.html?no_esc}
                    </div>

                    <!-- Cart and [Optionally] Apple Pay -->
                    <div class="row cart-and-ipay">
                        <div class="col-sm-12">
                            <input class="add-to-cart-url" value="/on/demandware.store/Sites-SiteGenesis-Site/en_US/Cart-AddProduct" type="hidden">
                            <button class="add-to-cart btn btn-primary" data-pid="${pv.product.id}" <#if pv.product.readyToOrder == false>disabled="disabled"</#if>>
                                <i class="fa fa-shopping-bag"></i>Add to Cart
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>