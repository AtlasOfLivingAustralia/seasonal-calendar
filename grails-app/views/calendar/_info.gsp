<div class="row">
    <div class="col-lg-12">
        <h4 class="text-center text-info">
            <strong><g:message code="calendar.name"/><span data-bind="text:calendarName"></span></strong>
        </h4>
        <p class="text-center">
            <span data-bind="if: calendarStatus() == 'unpublished'"><small class="label label-danger">Unpublished</small></span>
            <span data-bind="if: calendarStatus() == 'published'"><small class="label label-success">Published</small></span>

        </p>
    </div>
</div>


<div class="well">
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="calendarName"><g:message code="calendar.name"/>
                        <span class="req-field"></span></label>
                    <input data-validation-engine="validate[required]" data-bind="value: calendarName" type="text" class="form-control" id="calendarName" placeholder="Example: Larrakia">
                </div>
            </div>
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="description"><g:message code="calendar.description"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.description"/>', content:'<g:message code="calendar.description.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <textarea rows="4" data-bind="value: description" type="text" class="form-control" id="description" placeholder="Description"></textarea>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="calendarImage"><g:message code="calendar.imageurl"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.imageurl"/>', content:'<g:message code="calendar.imageurl.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <input data-bind="value: imageUrl" type="text" class="form-control" id="calendarImage" placeholder="">
                </div>
            </div>
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="externalUrl"><g:message code="calendar.website"/></label>
                    <button  href="#" class="helphover btn btn-link" data-bind="popover: {title:'<g:message code="calendar.website"/>', content:'<g:message code="calendar.website.content"/>'}">
                        <i class="fa fa-question-circle"></i>
                    </button>
                    <input data-bind="value: externalLink" type="text" class="form-control" id="externalUrl" placeholder="">
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-12">
                <div class="form-group">
                    <label for="multimedia">Promotional Video :</label>
                    <textarea rows="4" data-bind="value: multimedia" class="form-control" id="multimedia" placeholder="Example: <iframe width='560' height='315' src='https://www.youtube.com/embed/j1bR-0XBfcs' frameborder='0' allowfullscreen></iframe> (Allowed host: Youtube, Vimeo, Ted, Wistia.)"></textarea>
                </div>
            </div>
            <div class="col-lg-6"></div>
        </div>
    </div>

</div>