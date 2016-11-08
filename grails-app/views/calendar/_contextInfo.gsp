<div id="projectAddEdit">

    <div class="row">
        <div class="col-lg-12">
            <g:render template="info"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/organisation"/>
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
        <div class="col-lg-12 text-center">
            <div style="height: 400px;"id="calendarMap" data-leaflet-img="${request.contextPath}/vendor/leaflet/0.7.3/images"></div>
        </div>
    </div>

    <div class="margin-bottom-1"></div>

</div>

