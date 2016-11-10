<div class="row">
    <div class="col-lg-12">
        <p class="text-center text-info">
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
                    <input data-bind="value: organisation.name" type="text" class="form-control" id="org-name" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <label for="contributors"><g:message code="calendar.contributors"/></label>
                <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.contributors"/>', content:'<g:message code="calendar.contributors.content"/>'}">
                    <i class="fa fa-question-circle"></i>
                </button>
                <input data-bind="value: organisation.contributors" type="text" class="form-control" id="contributors" placeholder="">

            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="org-url"><g:message code="calendar.organisation.url"/></label>
                    <input data-bind="value: organisation.url" type="text" class="form-control" id="org-url" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="logo-url"><g:message code="calendar.organisation.logo"/></label>
                    <input data-bind="value: organisation.logo" type="text" class="form-control" id="logo-url" placeholder="">
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-name"><g:message code="calendar.contact.name"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.contact.name"/>', content:'<g:message code="calendar.contact.name.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <input  data-bind="value: organisation.contactName" type="text" class="form-control" id="contact-name" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-email">Contact email: </label>
                    <input data-bind="value: organisation.email" type="text" class="form-control" id="contact-email" placeholder="">
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-description"><g:message code="calendar.general"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.general"/>', content:'<g:message code="calendar.general.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <textarea data-bind="value: organisation.orgDescription" rows="5" class="form-control" id="organisation-description" placeholder=""></textarea>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-keywords"><g:message code="calendar.keywords"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.keywords"/>', content:'<g:message code="calendar.keywords.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <input data-bind="value: organisation.keywords"  type="text" class="form-control" id="organisation-keywords" placeholder="">
                </div>
            </div>

        </div>
    </div>

</div>