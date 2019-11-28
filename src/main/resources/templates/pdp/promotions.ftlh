<div class="col-md-10 col-12 promotions">
    <#if promotions??>
        <#list promotions as promotion>
            <#if promotion.calloutMsg??>
                <div class="callout" title="${promotion.calloutMsg}">
                    ${promotion.calloutMsg}
                </div>
            </#if>
        </#list>
    </#if>
</div>