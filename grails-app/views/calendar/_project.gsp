<div id="projectAddEdit">

    <div class="row">
        <div class="col-lg-12">
            <g:render template="info"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/projectWho"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <g:render template="/calendar/projectHow"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 text-left">
            <g:render template="/calendar/projectWhy"/>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 text-center">
            <div style="height: 400px;"id="defaultMap" data-leaflet-img="${request.contextPath}/vendor/leaflet/0.7.3/images"></div>
        </div>
    </div>

    <div class="margin-bottom-1"></div>

    <div class="well">
        <div class="row">
            <div class="col-lg-12">
                <div class="col-lg-6 text-left">
                    <button type="submit" class="btn btn-info"><i class="fa fa-save"></i> Save</button>
                    <button type="submit" class="btn btn-default"><i class="fa fa-arrow-right"></i> Next</button>
                </div>

                <div class="col-lg-6 text-right">

                </div>
            </div>
        </div>
    </div>

</div>
<r:script>
    $(window).load(function () {
        new ALA.Map("defaultMap", {});
    });

</r:script>

