<div class="container cart cart-page">
    <div class="row">
        <div class="col-sm-7 col-md-8">
    
            <#-- Ok all items rendered, we do not render all stuff... -->
            <#list data.basket.items as item>
            <div class="card product-info">
                <div class="line-item-header">
                    <div class="line-item-name">${item.productName}</div>
                    <div class="remove-line-item hidden-lg-up">
                        <button type="button" class="remove-btn remove-product btn btn-light" 
                            data-toggle="modal" 
                            data-target="#removeProductModal" 
                            data-pid="${item.id}" data-name="${item.productName}" 
                            data-action="${data.basket.actionUrls.removeProductLineItemUrl}" 
                            data-uuid="${item.UUID}" 
                            data-name="${item.productName}"
                            aria-label="remove">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="hidden-md-down">
                        <button type="button" class="remove-btn remove-product" 
                            data-toggle="modal" 
                            data-target="#removeProductModal" 
                            data-pid="${item.id}" data-name="${item.productName}" 
                            data-action="${data.basket.actionUrls.removeProductLineItemUrl}" 
                            data-name="${item.productName}"
                            data-uuid="${item.UUID}" 
                            aria-label="remove">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-5">
                        <div class="row">
                            <div class="col-lg-12 d-flex flex-row">
                                <div class="item-image">
                                    <img class="product-image" src="<#if item.images.small[0]??>${item.images.small[0].url}<#else></#if>">
                                </div>
                                <div class="item-attributes d-flex flex-column">
                                
                                    <#-- Options -->
                                    <#if item.options??>
                                        <#list item.options as option>
                                            <p class="line-item-attributes">${option}</p>
                                        </#list>
                                    </#if>
                                    
                                    <#-- Variations -->
                                    <#if item.variationAttributes??>
                                        <#list item.variationAttributes as attr>
                                            <p class="line-item-attributes">${attr.displayName}: ${attr.displayValue!'MISSING'}</p>
                                        </#list>
                                    </#if>
                                    
                                    <div class="line-item-availability availability-${item.UUID}">
                                        <#list item.availability.messages as msg>
                                            <p class="line-item-attributes">${msg}</p>
                                        </#list>
                                        <#if item.availability.inStockDate??>  
                                            <p class="line-item-attributes line-item-instock-date">${item.availability.inStockDate}</p>
                                        </#if>
                                    </div>
                                    <div class="hidden-xs-down edit-add-to-wishlist">
                                        <a href="#" class="edit" title="Edit">Edit</a>
                                        <a href="#" class="add-to-wishlist" title="Add to Wishlist">Add to Wishlist</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-7 product-card-footer">
                        <div class="row">
                            <div class="col-4">
                                <p class="line-item-price-info">Each</p>
                                <div class="price">
                                    <#if item.price.sale??>
                                        <span><span class="sales"><span class="value">${item.price.sale.formatted}</span></span></span>
                                    </#if>
                                    <#if item.price.list??>
                                        <span><span class="list"><span class="value">${item.price.list.formatted}</span></span></span>
                                    </#if>
                                </div>
                            </div>
                            <div class="col-4 col-lg-3 line-item-quantity">
                                <div class="quantity-form">
                                    <label class="line-item-pricing-info quantity-label" for="quantity">Quantity</label>
                                    <select class="form-control quantity custom-select" 
                                        data-uuid="${item.UUID}" 
                                        data-pid="${item.id}" 
                                        data-action="${data.basket.actionUrls.updateQuantityUrl}" 
                                        data-pre-select-qty="1.0" id="quantity" name="quantity">
                                        <#list item.quantityOptions.minOrderQuantity..item.quantityOptions.maxOrderQuantity as qty>
                                            <option <#if qty == item.quantity>selected</#if>>${qty}</option>
                                        </#list>
                                    </select>
                                </div>
                            </div>
                            <div class="col-4 line-item-total-price">
                                <p class="line-item-price-info">
                                    Total
                                </p>
                                <div class="item-total-${item.id} price">
                                    <div class="strike-through non-adjusted-price">TBD</div>
                                    <div class="pricing line-item-total-price-amount item-total-${item.id}">
                                        ${item.priceTotal.renderedPrice}
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-1 hidden-md-down remove-line-item-lg"></div>
                        </div>
                        <div class="line-item-promo item-${item.id}">
                        </div>
                    </div>
                </div>
            </div>
            </#list>
    
            <div class="approaching-discounts">
            </div>
        </div>

        <#include "cart-totals.ftlh">
    </div>
</div>
