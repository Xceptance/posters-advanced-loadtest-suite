<!-- that will form its own complete div -->
<#assign readyToOrder = pv.product.readyToOrder>
<#assign available = pv.product.available>
<#assign messages = pv.product.availability.messages>
<#include "availability.ftlh">

<#assign quantities = pv.product.quantities>
<#assign options = pv.product.options>
<#include "options.ftlh">

<!-- Applicable Promotions -->
<#assign promotions = pv.product.promotions![]>
<#include "promotions.ftlh">

<!-- cart and price -->
<#assign pid = pv.product.id >
<#assign addToCartUrl = "TOBEREPLACEDFROMSOURCE_DARN">
<#assign price = pv.product.price> 
<#assign readyToOrder = pv.product.readyToOrder>
<#include "prices-and-cart.ftlh">
