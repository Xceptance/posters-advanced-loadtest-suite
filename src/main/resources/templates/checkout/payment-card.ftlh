<option class="" 
    value="new" 
    data-first-name="" 
    data-last-name="" 
    data-address1="" 
    data-address2="" 
    data-city="" 
    data-state-code="" 
    data-postal-code="" 
    data-country-code="" 
    data-phone="">New Address</option>
<option class="" disabled="">- Existing Shipments -</option>

<#list data.order.shipping as shipment>
    <option class="" 
        value="${shipment.UUID}" 
        data-first-name="${shipment.shippingAddress.firstName}" 
        data-last-name="${shipment.shippingAddress.lastName}" 
        data-address1="${shipment.shippingAddress.address1}" 
        data-address2="${(shipment.shippingAddress.address2)!''}" 
        data-city="${shipment.shippingAddress.city}" 
        data-state-code="${shipment.shippingAddress.stateCode}" 
        data-postal-code="${shipment.shippingAddress.postalCode}" 
        data-country-code="${shipment.shippingAddress.countryCode}"
        data-phone="${shipment.shippingAddress.phone}" 
        selected="selected">${shipment.shippingAddress.firstName} ${shipment.shippingAddress.lastName} ... ${shipment.shippingAddress.postalCode}</option>
</#list>        
