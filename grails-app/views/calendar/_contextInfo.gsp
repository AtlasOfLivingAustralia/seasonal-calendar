<div id="projectAddEdit">

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/info"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/who"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/how"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 text-left">
            <g:render template="/calendar/why"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 text-left">
            <g:render template="/calendar/use"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 text-center">
            <div style="height: 400px;"id="calendarMap" data-leaflet-img="${request.contextPath}/vendor/leaflet/0.7.3/images"></div>
        </div>
    </div>

    <div class="margin-bottom-1"></div>

    <div class="row well">
        <div class="col-lg-12 text-left">
            <button data-bind="click: save" type="submit" class="btn btn-default"><i class="fa fa-save"></i> Save</button>
            <button data-bind="showTabOrRedirect: {url:'', tabId: '#season-info-tab'}" type="submit" class="btn btn-default"><i class="fa fa-arrow-circle-o-right"></i> Next</button>
        </div>
    </div>

</div>

