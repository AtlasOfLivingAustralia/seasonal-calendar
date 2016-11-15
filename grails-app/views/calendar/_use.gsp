<div class="row">
    <div class="col-lg-12">
        <p class="text-center feature-title color-scheme">
            <g:message code="calendar.use"/>
        </p>
    </div>
</div>


<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="project-limitations"><g:message code="calendar.use.limitations"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: limitations"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.use.limitations"/>', content:'<g:message code="calendar.use.limitations.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <textarea data-bind="value: limitations" rows="4" type="text" class="form-control" id="project-limitations" placeholder=""></textarea>
                    </g:else>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label><g:message code="calendar.use.licence"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: license"></h5>
                    </g:if>
                    <g:else>
                        <select id="project-licenses" style="width:100%;" data-bind="options: transients.licenses, optionsText:'name', optionsValue:'id', value: license, optionsCaption: 'Please select'"></select>
                    </g:else>

                </div>
            </div>

        </div>
    </div>


</div>