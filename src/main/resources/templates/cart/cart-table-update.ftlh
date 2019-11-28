<#-- Ok, do we have an empty cart? -->
<#if data.numItems == 0>
    <#include "cart-empty.ftlh">
<#else>
    <#include "cart-table-full-update.ftlh">
    <#-- Update the cart quantity -->
    <#include "cart-item-count-update.ftlh">
</#if>