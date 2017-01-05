
<div class="row">
    <div class="col-lg-12">
        <p class="text-center feature-title color-scheme">
            <g:message code="calendar.how"/>
        </p>
    </div>
</div>

<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-12">
                <div class="form-group">
                    <label for="how-developed"><g:message code="calendar.how.description"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: how"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme"
                                 data-bind="popover: {title:'<g:message code="calendar.how.description"/>', content:'<g:message code="calendar.how.description.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <textarea data-bind="value: how" rows="4" type="text" class="form-control" id="how-developed" placeholder=""></textarea>
                    </g:else>
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="calendar-reference"><g:message code="calendar.how.references"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: reference"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.how.references"/>', content:'<g:message code="calendar.how.references.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <input data-bind="value: reference" type="text" class="form-control" id="calendar-reference" placeholder="">
                    </g:else>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="reference-link"><g:message code="calendar.how.referenceLink"/></label>
                    <g:if test="${readonly}">
                        <h5><button class="btn btn-link" data-bind="attr:{'href': referenceLink}"><h5>Reference</h5></button></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.how.referenceLink"/>', content:'<g:message code="calendar.how.referenceLink.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <input  data-bind="value: referenceLink" data-validation-engine="validate[custom[url]]" type="text" class="form-control" id="reference-link" placeholder="">
                    </g:else>
                </div>
            </div>

        </div>
    </div>

</div>