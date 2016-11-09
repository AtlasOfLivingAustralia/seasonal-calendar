<!-- ko foreach: seasons -->
<div class="well">
    <div class="row">
        <div class="col-lg-12">
            <h5 class="text-center">
               <strong> <span data-bind="text: $index() + 1 "></span>. Seasons info : <span data-bind="text:seasonName"></span></strong>
            </h5>
            <div class="form-group text-right">
                <button type="submit"  data-bind="click: deleteSeason" class="btn btn-default btn-sm text-left"><i class="fa fa-close"></i> Delete Season</button>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">

            <div class="row">
                <div class="col-lg-12">
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="season-name">Season name: <span class="req-field"></span></label>
                            <input data-validation-engine="validate[required]" data-bind="value: seasonName" type="text" class="form-control" id="season-name" placeholder="Example: Dalay">
                        </div>

                    </div>
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="season-description">Season Description: <span class="req-field"></span></label>
                            <input data-validation-engine="validate[required]" data-bind="value: description" type="text" class="form-control" id="season-description" placeholder="Example: Monsoon Season">
                        </div>

                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="month">English month: <span class="req-field"></span></label>
                            <input data-validation-engine="validate[required]" data-bind="value: months" type="text" class="form-control" id="month" placeholder="Example: January, February, March">
                        </div>


                    </div>
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="month">Weather icons:</label>
                            <select style="width:100%;" data-bind="options: transients.weatherIcons, optionsText:'name', optionsValue:'id', value: weatherIcon, optionsCaption: 'Please select'"></select>
                        </div>

                    </div>
                </div>
            </div>

            <!-- ko foreach: features -->
            <hr>
            <div class="row">
                <div class="col-lg-12">
                    <h5 class="text-center">
                        <strong> <span data-bind="text: $index() + 1 "></span>. Feature info : <span data-bind="text:featureName"></span></strong>
                    </h5>

                </div>
            </div>
            <div class="form-group text-right">
                <button type="submit"  data-bind="click: $parent.deleteFeature" class="btn btn-default btn-sm text-left"><i class="fa fa-close"></i> Delete Feature</button>
            </div>

            <div class="row">
                <div class="col-lg-6">
                    <div class="form-group">
                        <label for="featureName">Feature name: <span class="req-field"></span></label>
                        <input data-validation-engine="validate[required]" data-bind="value: featureName" type="text" class="form-control" id="featureName" placeholder="Example: Danggalaba - Saltwater Crocodile">
                    </div>

                    <div class="form-group">
                        <label for="featureDescription">Feature description: <span class="req-field"></span></label>
                        <textarea data-validation-engine="validate[required]" data-bind="value: description" class="form-control" id="featureDescription" placeholder="Feature description"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="speciesName">Species name:</label>
                        <input data-bind="value: speciesName" type="text" class="form-control" id="speciesName" placeholder="Species name">
                    </div>

                    <div class="form-group">
                        <label for="speciesLink">ALA species link:</label>
                        <input data-bind="value: speciesLink" type="text" class="form-control" id="speciesLink" placeholder="Species link">
                    </div>
                </div>



                <div class="col-lg-6">
                    <label for="imageUrl">Images url:</label>
                    <!-- ko foreach: thumbImages -->
                    <div class="form-group">
                        <div class="col-lg-10">
                        <input  data-bind="value: url" type="text" class="form-control" id="imageUrl" placeholder="Image url">
                        </div>
                        <div class="col-lg-2">
                        <button data-bind="click: $parent.deleteThumbImageUrl" type="submit" class="btn btn-sm btn-default">x</button>
                        </div>
                    </div>
                    <!-- /ko -->

                    <div class="form-group">
                        <div class="col-lg-10">
                            <button data-bind="click: addThumbImageUrl" type="submit" class="btn btn-sm btn-default"><i class="fa fa-plus"></i> Add image url</button>
                        </div>
                        <div class="col-lg-2">

                        </div>
                    </div>
                </div>

            </div>

            <hr>
            <!-- /ko -->

            <div class="form-group text-right">
                <button type="submit"  data-bind="click: addFeature" class="btn btn-default text-right"><i class="fa fa-plus"></i> Add new feature</button>
            </div>

        </div>
    </div>
</div>
<!-- /ko -->

<div class="row">
    <div class="col-lg-12">
        <div class="pull-right">
            <button type="submit" data-bind="click: add" class="btn btn-default"><i class="fa fa-plus"></i> Add new seasons</button>
        </div>
        <div class="margin-bottom-5"></div>
    </div>
</div>
