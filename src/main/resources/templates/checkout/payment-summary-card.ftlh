<div class="card xlt-generated">
    <span class="summary-section-label billing-addr-label">Billing Address:</span><br>
    
    <#assign ba = data.order.billing.billingAddress.address />
    <#assign payments = data.order.billing.payment.selectedPaymentInstruments />
    <div class="summary-details billing">
        <div class="address-summary">
            <div>
                <span class="firstName">${ba.firstName}</span>
                <span class="lastName">${ba.lastName}</span>
            </div>
        
            <div class="address1">${ba.address1}</div>
        
            <div class="address2">${(ba.address2)!''}</div>
        
            <span class="city">${ba.city}</span>
            <span class="stateCode">${ba.stateCode}</span>
            <span class="postalCode">${ba.postalCode}</span>
        </div>
    
        <span class="order-summary-email">${data.email.value}</span><!-- incorrect JSON again -->
        <br>
        <span class="order-summary-phone">${data.phone.value}</span>
    </div>

    <div class="summary-details">
        <span class="summary-section-label payment-info-label">Payment:</span><br>
        
        <#list payments as payment>
            <div class="payment-details">
                <span>${payment.paymentMethod} ${payment.type}</span>
                <div>${payment.maskedCreditCardNumber}</div>
                <div><span>${payment.expirationMonth}/${payment.expirationYear}</span></div>
            </div>
        </#list>
    </div>
</div>