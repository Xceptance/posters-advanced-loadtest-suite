<div class="simple-quantity">
    <label>Quantity</label>
    
    <#assign quantities = pv.product.quantities>
    <#include "quantity-select.ftlh">
</div>
    
<!-- that will form its own complete div -->
<#assign readyToOrder = pv.product.readyToOrder>
<#assign available = pv.product.available>
<#assign messages = pv.product.availability.messages>
<#include "availability.ftlh">

<!-- Applicable Promotions -->
<#assign promotions = pv.product.promotions![]>
<#include "promotions.ftlh">

<!-- cart and price -->
<#assign pid = pv.product.id >
<#assign addToCartUrl = "TOBEREPLACEDFROMSOURCE_DARN">
<#assign price = pv.product.price> 
<#assign readyToOrder = pv.product.readyToOrder>
<#include "prices-and-cart.ftlh">

