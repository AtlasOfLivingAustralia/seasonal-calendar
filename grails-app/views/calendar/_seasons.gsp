%{--<!-- ko foreach: seasons -->--}%
<div class="well">
    <div class="row">
        <div class="col-lg-12">
            <h5 class="text-center">
               <strong> <span data-bind="text: $index() + 1 "></span>. Seasons information : <span data-bind="text:seasonName"></span></strong>
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
                            <label for="season-name"><g:message code="season.name"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="season.name"/>', content:'<g:message code="season.name.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <input  data-bind="value: seasonName" type="text" class="form-control" id="season-name" placeholder="Dalay; Ngawu">
                        </div>

                    </div>
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="seasonNameEnglish"><g:message code="season.name.english"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="season.name.english"/>', content:'<g:message code="season.name.english.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <input data-bind="value: seasonNameEnglish" type="text" class="form-control" id="seasonNameEnglish" placeholder="Monsoon season; hot and dry">
                        </div>

                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="col-lg-6">

                        <div class="form-group">
                            <label for="season-month"><g:message code="season.name.months"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="season.name.months"/>', content:'<g:message code="season.name.months.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <input data-validation-engine="validate[required]" data-bind="value: seasonMonths" type="text" class="form-control" id="season-month" placeholder="January, February, March">
                        </div>


                    </div>
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="season-weather"><g:message code="season.weather.icons"/></label>
                            <select id="season-weather" style="width:100%;" data-bind="options: transients.weatherIcons, optionsText:'name', optionsValue:'id', value: weatherIcon, optionsCaption: 'Please select'"></select>
                        </div>

                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-12">
                    <div class="col-lg-12">
                        <div class="form-group">
                            <label for="season-description"><g:message code="season.description"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="season.description"/>', content:'<g:message code="season.description.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <textarea row="4" data-validation-engine="validate[required]" data-bind="value: description" type="text" class="form-control" id="season-description" placeholder=""></textarea>
                        </div>
                    </div>
                </div>
            </div>

            %{--<!-- ko foreach: features -->--}%
            <div ng-controller="SearchController as searchCtrl" ng-init="searchCtrl.searchOptions.includeNameAttributes = true; searchCtrl.search()">
            <hr>
                <div class="row">
                    <div class="col-lg-12">
                        <h5 class="text-center">
                            <strong> <span data-bind="text: $index() + 1 "></span>. Feature information : <span data-bind="text:featureName"></span></strong>
                        </h5>

                    </div>
                </div>
                <div class="form-group text-right">
                    <button type="submit"  data-bind="click: $parent.deleteFeature" class="btn btn-default btn-sm text-left"><i class="fa fa-close"></i> Delete Feature</button>
                </div>

                <div class="row" ng-repeat="profile in searchCtrl.searchResults.items | orderBy: 'fullName'">
                    <div class="col-lg-6">
                        <div class="form-group">
                            <label for="featureName"><g:message code="feature.name"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="feature.name"/>', content:'<g:message code="feature.name.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <input ng-model="profile.fullName" type="text" class="form-control" id="featureName" placeholder="Example: Danggalaba - Saltwater Crocodile">
                        </div>
                        <div class="form-group">
                            <label for="featureNameEnglish"><g:message code="feature.name.english"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" ßdata-bind="popover: {title:'<g:message code="feature.name.english"/>', content:'<g:message code="feature.name.english.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <input data-bind="value: featureNameEnglish" type="text" class="form-control" id="featureNameEnglish" placeholder="">
                        </div>

                        <div class="form-group">
                            <label for="speciesName"><g:message code="feature.species"/></label>
                             </br>
                             <input id="speciesName" class="input-xlarge" type="text" placeholder="Search species" ng-model="profile.scientificName">
                                  %{-- data-bind="value:species.name,
                                            event:{focusout: species.focusLost},
                                            fusedAutocomplete:{
                                                source: transients.bioSearch,
                                                name: species.transients.name,
                                                guid: species.transients.guid,
                                                scientificName: species.transients.scientificName,
                                                commonName: species.transients.commonName
                                            }">--}%

                            <span data-bind="if: species.transients.guid">
                                <a data-bind="attr:{href: species.transients.bioProfileUrl}" target="_blank"><button class="btn btn-sm btn-info"><i class="fa fa-info-circle"></i></button></a>
                            </span>

                        </div>


                        <div class="form-group">
                            <label for="featureDescription"><g:message code="feature.description"/><span class="req-field"></span></label>
                            <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="feature.description"/>', content:'<g:message code="feature.description.content"/>'}">
                                <i class="fa fa-question-circle"></i>
                            </button>
                            <textarea rows="4" data-validation-engine="validate[required]" data-bind="value: description" class="form-control" id="featureDescription" placeholder="Feature description"></textarea>
                        </div>
                    </div>


                    <div class="col-lg-6" ng-init="searchCtrl.loadImageForProfile(profile.uuid)">
                        <label for="imageUrl"><g:message code="feature.image.url"/></label>
                        <button  href="#" class="helphover btn btn-link color-scheme" data-bind="popover: {title:'<g:message code="feature.image.url"/>', content:'<g:message code="feature.image.url.content"/>'}">
                            <i class="fa fa-question-circle"></i>
                        </button>
                        %{--<!-- ko foreach: thumbImages -->--}%

                        <div class="form-group">
                            <div class="col-lg-10">
                            <input ng-model="profile.image.url"  type="text" data-validation-engine="validate[custom[url]]" class="form-control" id="imageUrl" placeholder="Image url">
                            </div>
                            <div class="col-lg-2">
                            <button data-bind="click: $parent.deleteThumbImageUrl" type="submit" class="btn btn-sm btn-default">x</button>
                            </div>
                        </div>
                        %{--<!-- /ko -->--}%

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
            </div>
            %{--<!-- /ko -->--}%

            <div class="form-group text-right">
                <button type="submit"  data-bind="click: addFeature" class="btn btn-default text-right"><i class="fa fa-plus"></i> Add new feature</button>
            </div>

        </div>
    </div>
</div>
%{--<!-- /ko -->--}%

<div class="row">
    <div class="col-lg-12">
        <div class="pull-right">
            <button type="submit" data-bind="click: add" class="btn btn-default"><i class="fa fa-plus"></i> Add new seasons</button>
        </div>
        <div class="margin-bottom-5"></div>
    </div>
</div>
