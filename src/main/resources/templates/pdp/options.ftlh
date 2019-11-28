<div class="col-md-10 col-12 product-options">
    <#list options as option>
        <div class="row product-option" data-option-id="${option.id}">
            <div class="col-8">
                <label>${option.name}</label>
                <select class="options-select custom-select form-control">
                    <#list option.values as v>
                        <option 
                            <#if option.selectedValueId == v.id>selected="selected"</#if> 
                                data-value-id="${v.id}"
                                value="${v.url}">[${v.price}] ${v.displayValue}
                        </option>
                    </#list>
                </select>
            </div>
        </div>
    </#list>

    <!-- Quantity Drop Down Menu -->
    <div class="col-4 quantity">
        <label>Quantity</label>
        
        <#include "quantity-select.ftlh">
    </div>  
</div>