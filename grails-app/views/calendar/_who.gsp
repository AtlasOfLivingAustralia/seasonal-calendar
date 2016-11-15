<div class="row">
    <div class="col-lg-12">
        <p class="text-center feature-title color-scheme">
            <g:message code="calendar.who"/>
        </p>
    </div>
</div>

<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="org-name"><g:message code="calendar.organisation.name"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: organisation.name"></h5>
                    </g:if>
                    <g:else>
                        <input data-bind="value: organisation.name" type="text" class="form-control" id="org-name" placeholder="">
                    </g:else>
                </div>
            </div>

            <div class="col-lg-6">
                <label for="contributors"><g:message code="calendar.contributors"/></label>
                <g:if test="${readonly}">
                    <h5 data-bind="text: organisation.contributors"></h5>
                </g:if>
                <g:else>
                    <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.contributors"/>', content:'<g:message code="calendar.contributors.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <input data-bind="value: organisation.contributors" type="text" class="form-control" id="contributors" placeholder="">
                </g:else>
            </div>

        </div>
    </div>


    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-name"><g:message code="calendar.contact.name"/></label>
                    <g:if test="${readonly}">
                        <h5  data-bind="text: organisation.contactName"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.contact.name"/>', content:'<g:message code="calendar.contact.name.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <input  data-bind="value: organisation.contactName" type="text" class="form-control" id="contact-name" placeholder="">
                    </g:else>

                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-email">Contact email: </label>
                    <g:if test="${readonly}">
                        <h5><button class="btn btn-link" data-bind="attr:{'mailto:':  organisation.email}"><h5 data-bind="text: organisation.email"></h5></button></h5>
                    </g:if>
                    <g:else>
                        <input data-bind="value: organisation.email" type="text" class="form-control" id="contact-email" placeholder="">
                    </g:else>
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-keywords"><g:message code="calendar.keywords"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: organisation.keywords"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.keywords"/>', content:'<g:message code="calendar.keywords.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        <input data-bind="value: organisation.keywords"  type="text" class="form-control" id="organisation-keywords" placeholder="">
                    </g:else>

                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-description"><g:message code="calendar.general"/></label>
                    <g:if test="${readonly}">
                        <h5 data-bind="text: organisation.orgDescription"></h5>
                    </g:if>
                    <g:else>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="calendar.general"/>', content:'<g:message code="calendar.general.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>

                        <textarea data-bind="value: organisation.orgDescription" rows="5" class="form-control" id="organisation-description" placeholder=""></textarea>
                    </g:else>

                </div>
            </div>


        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:if test="${readonly}">
                <div class="col-lg-6">
                    <a data-bind="attr:{'href': organisation.url}"><img width="50px" data-bind="attr:{'src': organisation.logo}"/></a>
                </div>
            </g:if>
            <g:else>
                <div class="col-lg-6">
                    <div class="form-group">
                        <input data-bind="value: organisation.url" type="text" class="form-control" id="org-url" placeholder="">
                    </div>
                </div>

                <div class="col-lg-6">
                    <div class="form-group">
                        <label for="logo-url"><g:message code="calendar.organisation.logo"/></label>
                        <input data-bind="value: organisation.logo" type="text" class="form-control" id="logo-url" placeholder="">
                    </div>
                </div>
            </g:else>
        </div>

    </div>


</div>