<div class="card-body xlt-generated">
    <#list data.order.shipping as shipment>  

        <div class="single-shipping" data-shipment-summary="${shipment.UUID}">
            <p class="summary-section-label shipping-addr-label">Shipping Address:</p>
    
            <div class="summary-details shipping">
                <div class="address-summary">
                    <div>
                        <span class="firstName">${shipment.shippingAddress.firstName}</span>
                        <span class="lastName">${shipment.shippingAddress.lastName}</span>
                    </div>
    
                    <div class="address1">${shipment.shippingAddress.address1}</div>
    
                    <div class="address2">${shipment.shippingAddress.address2!''}</div>
    
                    <span class="city">${shipment.shippingAddress.city}</span>
                    <span class="stateCode">${shipment.shippingAddress.stateCode}</span>
                    <span class="postalCode">${shipment.shippingAddress.postalCode}</span>
                </div>
    
                <div class="shipping-phone">${shipment.shippingAddress.phone}</div>
            </div>
    
            <p class="summary-section-label shipping-method-label">Shipping Method:</p>
            <div class="row summary-details leading-lines">
                <div class="col-8 start-lines">
                    <p class="shipping-method">
                        <span class="shipping-method-title">${shipment.selectedShippingMethod.displayName}</span>
                        <span class="shipping-method-arrival-time">
                            <#if shipment.selectedShippingMethod.estimatedArrivalTime??>
                                ( ${shipment.selectedShippingMethod.estimatedArrivalTime} )</span>
                            </#if>
                    </p>
                </div>
                <div class="col-4 end-lines">
                    <p class="text-right pricing shipping-method-price">${shipment.selectedShippingMethod.shippingCost}</p>
                </div>
            </div>
    
        </div>
    </#list>
    
    <p class="summary-section-label shipping-addr-label multi-shipping">
        Addresses &amp; shipping methods are listed under your items.
    </p>
</div>