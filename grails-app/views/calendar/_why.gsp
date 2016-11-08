<div class="row">
    <div class="col-lg-12">
        <p class="text-center text-danger">
            c. <strong>WHY</strong> - for what purpose and for whom the calendar was created
        </p>
    </div>
</div>

<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="why-developed">Why the calendar was developed</label>
                    <textarea data-bind="value: why" rows="4" type="text" class="form-control" id="why-developed" placeholder=""></textarea>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="project-limitations">Limitations: </label>
                    <textarea data-bind="value: limitations" rows="4" type="text" class="form-control" id="project-limitations" placeholder=""></textarea>
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label>Licenses:</label>
                    <select id="project-licenses" style="width:100%;" data-bind="options: transients.licenses, optionsText:'name', optionsValue:'id', value: license, optionsCaption: 'Please select'"></select>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                </div>
            </div>

        </div>
    </div>

</div>