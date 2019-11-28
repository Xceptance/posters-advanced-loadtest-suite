<!-- Availability -->
<div class="availability col-md-10 col-12 product-availability" data-ready-to-order="${readyToOrder?c}" data-available="${available?c}">
    <div class="row">
        <div class="col-12 col-sm-3">
            <label class="availability">Availability:</label>
        </div>

        <div class="col-12 col-sm-9">
            <ul class="list-unstyled availability-msg">
                <#list messages as msg>
                    <li>${msg}</li>
                <#else>
                    <li>Select Styles for Availability</li>
                </#list>
            </ul>
        </div>
    </div>
</div>

