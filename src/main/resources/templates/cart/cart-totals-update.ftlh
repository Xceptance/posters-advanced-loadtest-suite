<div class="col-sm-5 col-md-4 totals">
    <div class="row">
        <div class="col-12 hidden-sm-up">
            <p class="optional-promo">Enter promo code (optional)</p>
        </div>
    </div>
    <div class="row">
        <div class="col-12">
            <form action="/on/demandware.store/Sites-SiteGenesis-Site/en_US/Cart-AddCoupon" class="promo-code-form" method="GET" name="promo-code-form">
                <div class="form-group">
                    <label for="couponCode">Enter Promo Code</label>
                    <div class="row">
                        <div class="col-7">
                            <input type="text" class="form-control coupon-code-field" id="couponCode" name="couponCode" placeholder="Promo Code">
                        </div>
                        <input type="hidden" name="csrf_token" value="I WILL BE UPDATED">
                        <div class="col-5 promo-code-submit">
                            <button type="submit" class="btn btn-primary btn-block promo-code-btn">
                            Submit
                            </button>
                        </div>
                    </div>
                    <div class="form-control-feedback coupon-error">
                        <span class="coupon-missing-error">No coupon code entered</span>
                        <span class="coupon-error-message"></span>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="coupons-and-promos">
    </div>
    <div class="row">
        <div class="col-12">
            <div class="form-group">
                <label for="shippingMethods">Shipping</label>
                <select class="custom-select form-control shippingMethods" id="shippingMethods" name="shippingMethods" 
                    data-actionurl="${data.actionUrls.selectShippingUrl}">
                    <#list data.shipments as shipment>
                        <#list shipment.shippingMethods as method>
                            <option <#if method.selected>selected </#if>data-shipping-id="${method.ID}">${method.displayName} ( ${method.estimatedArrivalTime} )</option>
                        </#list>
                    </#list>
                </select>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-8">
            <p>Shipping cost</p>
        </div>
        <div class="col-4">
            <p class="text-right shipping-cost">${data.totals.totalShippingCost}</p>
        </div>
    </div>
    <div class="row shipping-discount <#if data.totals.shippingLevelDiscountTotal.value == 0>hide-shipping-discount</#if>">
        <div class="col-8">
            <p>Shipping Discount</p>
        </div>
        <div class="col-4">
            <p class="text-right shipping-discount-total">- ${data.totals.shippingLevelDiscountTotal.formatted}</p>
        </div>
    </div>
    <div class="row">
        <div class="col-8">
            <p>Sales Tax</p>
        </div>
        <div class="col-4">
            <p class="text-right tax-total">${data.totals.totalTax}</p>
        </div>
    </div>
    <div class="row order-discount <#if data.totals.orderLevelDiscountTotal.value == 0>hide-order-discount</#if>">
        <div class="col-8">
            <p>Order Discount</p>
        </div>
        <div class="col-4">
            <p class="text-right order-discount-total"> - ${data.totals.orderLevelDiscountTotal.formatted}</p>
        </div>
    </div>
    <div class="row">
        <div class="col-8">
            <strong>Estimated Total</strong>
        </div>
        <div class="col-4">
            <p class="text-right grand-total">${data.totals.grandTotal}</p>
        </div>
    </div>
    <div class="row">
        <div class="col-12 checkout-continue">
            <div>
                <a href="I WILL BE TRANSFERRED" 
                    class="btn btn-primary btn-block checkout-btn " role="button" aria-pressed="true">
                Checkout
                </a>
            </div>
        </div>
    </div>
    <br>
</div>
