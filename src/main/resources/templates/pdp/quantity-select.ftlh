
<select class="quantity-select custom-select form-control">
    <#list quantities as quantity>
        <option 
            <#if quantity.selected>selected="selected"</#if> 
                value="${quantity.value}"
                data-url="${quantity.url}">${quantity.value}
        </option>  
    </#list>
</select>