<div class="col availability align-self-end global-availability" data-ready-to-order="${pv.product.readyToOrder?c}" data-available="${pv.product.available?c}">
    <div class="row">
        <div class="col-12 col-sm-3">
            <label class="availability">Availability:</label>
        </div>
        <div class="col-12 col-sm-9">
            <ul class="list-unstyled availability-msg">
                <#list pv.product.availability.messages as msg>
                    <li>${msg}</li>
                <#else>
                    <li>Select Styles for Availability</li>
                </#list>
            </ul>
        </div>
    </div>
</div>
    
<div class="col">
    <div class="row align-items-end">
        <!-- Quantity -->
        <div class="col-4">
            <label>Quantity</label>
            <select class="quantity-select custom-select form-control">
                <#list pv.product.quantities as quantity>
                    <option 
                        <#if quantity.selected>selected="selected"</#if> value="${quantity.value}"
                        data-url="${quantity.url}"
                        >
                        ${quantity.value}
                    </option>  
                </#list>
            </select>
        </div>
        <div class="col-8">
            <!-- Prices -->
            <div class="prices">
                <div class="price"> 
                    ${pv.product.price.html?no_esc}
                </div>
            </div>

            <!-- Cart and [Optionally] Apple Pay -->
            <div class="row cart-and-ipay">
                <div class="col-sm-12">
                    <input type="hidden" class="add-to-cart-url" value="/on/demandware.store/Sites-SiteGenesis-Site/en_US/Cart-AddProduct">
                    <button class="add-to-cart-global btn btn-primary" data-pid="${pv.product.id}" <#if pv.product.readyToOrder == false>disabled="disabled"</#if>> 
                        <i class="fa fa-shopping-bag"></i>
                        Add to Cart
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>


