<div class="col-12 prices-add-to-cart-actions prices">
    <!-- Prices -->
    <div class="prices">
        ${price.html}
    </div>
    
    <!-- Cart and [Optionally] Apple Pay -->
    <div class="row cart-and-ipay">
        <div class="col-sm-12">
            <input class="add-to-cart-url" value="${addToCartUrl}" type="hidden">
            <button class="add-to-cart btn btn-primary" data-pid="${pid}" 
                <#if readyToOrder == false>disabled="disabled"</#if>>
                <i class="fa fa-shopping-bag"></i>Add to Cart
            </button>
        </div>
    </div>
</div>
