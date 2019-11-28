<#-- Ok, do we have an empty cart? -->
<#if data.basket.numItems == 0>
    <#include "cart-empty.ftlh">
<#else>
    <#include "cart-table-full.ftlh">
</#if>                                