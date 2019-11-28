<#list data.order.shipping as shipment>
    <!-- xlt-generated -->
    <input name="shipmentUUID" type="hidden" value="${shipment.UUID}"/>
    
    <#list shipment.applicableShippingMethods as method>
        <div class="form-check col-9 start-lines">
            <label class="form-check-label shipping-method-option">
                <input class="form-check-input" id="shippingMethod-${method.ID}" name="dwfrm_shipping_shippingAddress_shippingMethodID" value="${method.ID}" 
                    <#if method.selected>checked="checked"</#if> type="radio">
                <span class="display-name">${method.displayName}</span>
                <span class="text-muted arrival-time" style="">${method.estimatedArrivalTime!""}</span>
            </label>
        </div>
        <div class="col-3 text-right shipping-method-pricing end-lines">
            <span class="shipping-cost">${method.shippingCost}</span>
        </div>
    </#list>
</#list>
