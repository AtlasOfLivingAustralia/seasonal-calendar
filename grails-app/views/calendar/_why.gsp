<div class="row">
    <div class="col-lg-12">
        <p class="text-center text-info">
            <g:message code="calendar.why"/>
        </p>
    </div>
</div>

<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-12">
                <div class="form-group">
                    <label for="why-developed"><g:message code="calendar.why.description"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.why.description"/>', content:'<g:message code="calendar.why.description.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <textarea data-bind="value: why" rows="4" type="text" class="form-control" id="why-developed" placeholder=""></textarea>
                </div>
            </div>

        </div>
    </div>
</div>