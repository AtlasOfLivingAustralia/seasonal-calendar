<div class="row">
    <div class="col-lg-12">
        <h1 class="text-center " data-bind="text: calendarName"></h1>
        <p class="text-center" data-bind="text: description"></p>
    </div>
</div>

<div class="row">
    <div class="col-lg-12">
        <g:render template="who" model="[readonly: true]"/>
    </div>
</div>


<div class="row">
    <div class="col-lg-12">
        <g:render template="how" model="[readonly: true]"/>
    </div>
</div>


<div class="row">
    <div class="col-lg-12">
        <g:render template="why" model="[readonly: true]"/>
    </div>
</div>



<div class="row">
    <div class="col-lg-12">
        <g:render template="use" model="[readonly: true]"/>
    </div>
</div>


<div class="row">
    <div class="col-lg-12">
        <div style="height: 400px;"id="calendarMap" data-leaflet-img="${request.contextPath}/vendor/leaflet/0.7.3/images"></div>
    </div>
</div>
<div class="margin-bottom-5"></div>
