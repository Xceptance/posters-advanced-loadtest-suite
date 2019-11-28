<div class="card-block order-total-summary">
                        
    <div class="row leading-lines subtotal-item">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Subtotal</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="sub-total">${data.order.totals.subTotal}</span></p>
        </div>
    </div>
    
    
    <div class="row leading-lines order-discount <#if data.order.totals.orderLevelDiscountTotal.value == 0>hide-order-discount</#if>">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Order Discount</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="order-discount-total">- ${data.order.totals.orderLevelDiscountTotal.formatted}</span></p>
        </div>
    </div>
    
    
    <div class="row leading-lines shipping-item">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Shipping</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="shipping-total-cost">${data.order.totals.totalShippingCost}</span></p>
        </div>
    </div>
    
    
    <div class="row leading-lines shipping-discount <#if data.order.totals.shippingLevelDiscountTotal.value == 0>hide-shipping-discount</#if>">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Shipping Discount</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="shipping-discount-total">- ${data.order.totals.shippingLevelDiscountTotal.formatted}</span></p>
        </div>
    </div>
    
    
    <div class="row leading-lines sales-tax-item">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Sales Tax</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="tax-total">${data.order.totals.totalTax}</span></p>
        </div>
    </div>
    
    
    <div class="row grand-total leading-lines">
        <div class="col-6 start-lines">
            <p class="order-receipt-label"><span>Total</span></p>
        </div>
        <div class="col-6 end-lines">
            <p class="text-right"><span class="grand-total-sum">${data.order.totals.grandTotal}</span></p>
        </div>
    </div>

</div>