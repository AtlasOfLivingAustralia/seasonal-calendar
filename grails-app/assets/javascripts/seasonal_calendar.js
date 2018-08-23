//= require speciesModel.js
//= require_self
var SeasonalCalendarsMenuVM = function () {
    var self = this;

    self.items = ko.observableArray();
    self.populate = function (data) {
        self.items([]);
        self.items($.map(data.calendars ? data.calendars : [], function (obj, i) {
            return new SeasonalCalendarsMenuItems(obj);
        }));
    };

    self.addCalendar = function () {
        window.location.href = scConfig.calendarHome;
    };
    self.transients = {};
    self.transients.isNew = ko.pureComputed(function () {
        var calendarId = scConfig.id;
        return (calendarId == "");
    });


    self.loadMenuItems = function () {
        return $.ajax({
            url: scConfig.onlyMyCalendars ? scConfig.listMyCalendars : scConfig.listCalendars,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.error) {
                }
                else {
                    self.populate(data);
                }
            },
            error: function (data) {
            }
        });
    };

    self.loadMenuItems();
};

var SeasonalCalendarsMenuItems = function (item) {
    var self = this;
    if (!item) item = {};
    self.name = ko.observable(item.calendarName);
    self.calendarId = ko.observable(item.calendarId);
    self.calendarStatus = ko.observable(item.calendarStatus);
    self.imageUrl = ko.observable(item.imageUrl);


    self.redirect = function () {
        window.location.href = scConfig.calendarHome + "/" + self.calendarId();
    };

    self.redirectToDetailPage = function () {
        window.location.href = scConfig.previewCalendar + "/" + self.calendarId();
    };

    self.transients = {};
    self.transients.isActive = ko.pureComputed(function () {
        var calendarId = scConfig.id;
        return (calendarId == self.calendarId());
    });

};

var OrganisationVM = function (organisation) {
    var self = this;
    self.name = ko.observable();
    self.orgDescription = ko.observable();
    self.contributors = ko.observable();
    self.url = ko.observable();
    self.logo = ko.observable();
    self.contactName = ko.observable();
    self.email = ko.observable();
    self.keywords = ko.observable();


    self.loadOrganisation = function (organisation) {
        if (!organisation) organisation = {};

        self.name(organisation.name);
        self.orgDescription(organisation.orgDescription);
        self.contributors(organisation.contributors);
        self.url(organisation.url);
        self.logo(organisation.logo);
        self.contactName(organisation.contactName);
        self.email(organisation.email);
        self.keywords(organisation.keywords);
    };

    self.loadOrganisation(organisation);
};

var SeasonalCalendarVM = function () {
    var self = this;
    self.calendarId = ko.observable();
    self.calendarName = ko.observable();
    self.imageUrl = ko.observable();
    self.description = ko.observable();
    self.calendarStatus = ko.observable();
    self.externalLink = ko.observable();
    self.multimedia = ko.observable();
    self.how = ko.observable();
    self.why = ko.observable();
    self.license = ko.observable();
    self.limitations = ko.observable();
    self.reference = ko.observable();
    self.referenceLink = ko.observable();

    self.organisation = new OrganisationVM();
    self.seasons = ko.observableArray();
    var mapReadonly = scConfig.mapReadonly ? true : false;
    self.map = new ALA.Map("calendarMap", {
        drawControl: !mapReadonly,
        singleMarker: false,
        useMyLocation: false,
        allowSearchLocationByAddress: false,
        allowSearchRegionByAddress: false,
        draggableMarkers: false,
        showReset: true
    });

    self.map.subscribe(listenToMapChanges);
    function listenToMapChanges() {
        // Not required for now.
    };

    self.transients = {};

    self.transients.iframe = ko.pureComputed(function () {
        if (self.multimedia()) {
            return buildiFrame(self.multimedia());
        }
        return;
    });

    self.populateCalendar = function (calendar) {
        self.calendarId(calendar.calendarId);
        self.calendarName(calendar.calendarName);
        self.description(calendar.description);
        self.externalLink(calendar.externalLink);
        self.imageUrl(calendar.imageUrl);
        self.multimedia(calendar.multimedia);
        self.calendarStatus((calendar.calendarStatus ? calendar.calendarStatus : 'unpublished'));
        self.how(calendar.how);
        self.why(calendar.why);
        self.license(calendar.license);
        self.limitations(calendar.limitations);
        self.reference(calendar.reference);
        self.referenceLink(calendar.referenceLink);

        self.organisation.loadOrganisation(calendar.organisation);
        self.seasons($.map(calendar.seasons ? calendar.seasons : [], function (obj, i) {
            return new SeasonVM(obj);
        }));

        //Load geoJSON sites
        if(calendar.sites){
            self.map.setGeoJSON(calendar.sites);
        }
    };

    self.transients.licenses = [
        {id: 'creative-commons', name: 'Creative Commons Attribution-NonCommercial-NoDerivatives (CC-BY-NC-ND)'},
        {id: 'copyright-all', name: 'Copyright-All rights reserved'},
        {id: 'cc-by', name: 'Creative Commons Attribution (CC-BY)'},
        {id: 'cc-by-nc', name: 'Creative Commons Attribution-NonCommercial (CC-BY-NC)'},
        {id: 'cc-by-sa', name: 'Creative Commons Attribution-ShareAlike (CC-BY-SA)'}
    ];

    self.previewCalendar = function (calendar) {
        window.open(scConfig.previewCalendar + "/" + self.calendarId());
    };

    self.deleteCalendar = function () {
        var url = scConfig.deleteCalendar + "/" + self.calendarId();
        var divId = "seasonal-calendar-result";

        bootbox.confirm({
            message: 'Are you sure you want to delete?',
            buttons: {
                'cancel': {
                    label: 'No',
                    className: 'btn-default'
                },
                'confirm': {
                    label: 'Yes',
                    className: 'btn-danger'
                }
            },

            callback: function(result) {
                if (result) {
                    self.calendarStatus('deleted');
                    return $.ajax({
                        url: url,
                        type: 'POST',
                        data: self.calendarAsJson(),
                        contentType: 'application/json',
                        success: function (data) {
                            if (data.error) {
                                showAlert("Error: " + data.error, "alert-danger", divId);
                            }
                            else {
                                showAlert("Successfully deleted, redirecting...", "alert-success", divId);
                                setInterval(function () {
                                    window.location.href = scConfig.calendarHome;
                                }, 3000);
                            }
                        },
                        error: function (data) {
                            debugger;
                            showAlert("Error code: " + data.responseText, "alert-danger", divId);
                        }
                    });
                }
            }
        });
    };

    self.validateNames = function() {
        var seasonErrorCounter = 0;
        var featureErrorCounter = 0;
        var message = "";

        ko.utils.arrayForEach(self.seasons(), function(season) {
            if(!season.seasonName() && !season.seasonNameEnglish()){
                seasonErrorCounter++;
            }

            ko.utils.arrayForEach(season.features(), function(feature) {
                if(!feature.featureNameEnglish() && !feature.featureName()){
                    featureErrorCounter++;
                }
            });
        });


        if(seasonErrorCounter > 0) {
            message = "Season name(Language) or Season name (English) is required.";
        }
        if(featureErrorCounter > 0) {
            if(message) {
                message = message + "<br>";
            }
            message = message + "Feature name(Language) or Feature name (English) is required"
        }

        return message;
    };

    self.loadCalendar = function () {
        var calendarId = scConfig.id;

        if (calendarId) {
            var url = scConfig.getCalendar + "/" + calendarId;
            return $.ajax({
                url: url,
                type: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    if (data.error) {
                    }
                    else {
                        self.populateCalendar(data.calendar);
                    }
                },
                error: function (data) {
                }
            });
        }
    };

    self.add = function () {
        self.seasons.push(new SeasonVM());
    };

    self.publish = function () {
        var divId = "seasonal-calendar-result";
        if(!self.validate('publish')) return;

        var errMsg = self.validateNames();
        if(errMsg) {
            showAlert(errMsg, "alert-danger", divId);
            return;
        }

        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() : scConfig.addCalendar;
        bootbox.confirm({
            message: 'Are you sure you want to publish?',
            buttons: {
                'cancel': {
                    label: 'No',
                    className: 'btn-default'
                },
                'confirm': {
                    label: 'Yes',
                    className: 'btn-danger'
                }
            },

            callback: function(result) {
                if (result) {
                    self.calendarStatus('published');
                    return $.ajax({
                        url: url,
                        type: 'POST',
                        data: self.calendarAsJson(),
                        contentType: 'application/json',
                        success: function (data) {
                            if (data.error) {
                                showAlert("Error: " + data.error, "alert-danger", divId);
                            }
                            else {
                                if (self.calendarId()) {
                                    showAlert("Successfully published", "alert-success", divId);
                                } else {
                                    showAlert("Successfully published, reloading the page...", "alert-success", divId);
                                    window.location.href = scConfig.calendarHome + "/" + data.calendarId;
                                }
                            }
                        },
                        error: function (data) {
                            showAlert("Error: " + data, "alert-danger", divId);
                        }
                    });
                }
            }
        });

    };
    self.validate = function(call){
        if(!call) call = "";
        var divId = "seasonal-calendar-result";
        var success = true;

        if (!$('#calendar-validation').validationEngine('validate')) {
            success = false;
        }

        if(!self.calendarName()) {
            showAlert("Calendar name is required.", "alert-danger", divId);
            success = false;
        }

        if(call =="publish") {
            if(self.seasons().length == 0) {
                showAlert("Minimum one season is requied .", "alert-danger", divId);
                success = false;
            }
        }

        return success;
    };
    self.unpublish = function () {
        if(!self.validate()) return;

        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() : scConfig.addCalendar;
        var divId = "seasonal-calendar-result";



        bootbox.confirm({
            message: 'Are you sure you want to unpublish the calendar?',
            buttons: {
                'cancel': {
                    label: 'No',
                    className: 'btn-default'
                },
                'confirm': {
                    label: 'Yes',
                    className: 'btn-danger'
                }
            },

            callback: function(result) {
                if (result) {
                    self.calendarStatus('unpublished');
                    return $.ajax({
                        url: url,
                        type: 'POST',
                        data: self.calendarAsJson(),
                        contentType: 'application/json',
                        success: function (data) {
                            if (data.error) {
                                showAlert("Error: " + data.error, "alert-danger", divId);
                            }
                            else {
                                if (self.calendarId()) {
                                    showAlert("Successfully unpublished", "alert-success", divId);
                                } else {
                                    showAlert("Successfully unpublished, reloading the page...", "alert-success", divId);
                                    window.location.href = scConfig.calendarHome + "/" + data.calendarId;
                                }
                            }
                        },
                        error: function (data) {
                            showAlert("Error: " + data, "alert-danger", divId);
                        }
                    });
                }
            }
        });
    };

    self.save = function () {
        if(!self.validate()) return;

        var divId = "seasonal-calendar-result";
        var errMsg = self.validateNames();
        if(errMsg) {
            showAlert(errMsg, "alert-danger", divId);
            return;
        }

        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() : scConfig.addCalendar;
        return $.ajax({
            url: url,
            type: 'POST',
            data: self.calendarAsJson(),
            contentType: 'application/json',
            success: function (data) {
                debugger;
                if (data.error) {
                    showAlert("Error: " + data.error, "alert-danger", divId);
                }
                else {
                    if (self.calendarId()) {
                        showAlert("Successfully updated calendar and seasons details.", "alert-success", divId);
                    } else {
                        showAlert("Successfully added, reloading the page...", "alert-success", divId);
                        window.location.href = scConfig.calendarHome + "/" + data.id;
                    }
                }
            },
            error: function (data) {
                showAlert("Error: " + data.responseText, "alert-danger", divId);
            }
        });
    };

    self.deleteSeason = function (season) {
        self.seasons.remove(season);
    };

    self.calendarAsJson = function () {
        var jsData = {};
        jsData.calendarId = self.calendarId();
        jsData.calendarName = self.calendarName();
        jsData.imageUrl = self.imageUrl();
        jsData.description = self.description();
        jsData.calendarStatus = self.calendarStatus();
        jsData.externalLink = self.externalLink();
        jsData.multimedia = self.multimedia();
        jsData.how = self.how();
        jsData.why = self.why();
        jsData.license = self.license();
        jsData.limitations = self.limitations();
        jsData.reference = self.reference();
        jsData.referenceLink = self.referenceLink();

        jsData.seasons = ko.mapping.toJS(self.seasons, {ignore: ['transients']});
        jsData.organisation = ko.mapping.toJS(self.organisation, {ignore: ['transients']});
        jsData.sites = self.map.getGeoJSON();
        return JSON.stringify(jsData, function (key, value) {
            return value === undefined ? "" : value;
        });
    };

    self.loadCalendar();
};

var SeasonVM = function (seasons) {
    var self = this;
    if (!seasons) seasons = {};

    self.seasonName = ko.observable();
    self.seasonNameEnglish = ko.observable();
    self.description = ko.observable();
    self.seasonMonths = ko.observable();
    self.weatherIcon = ko.observable();
    self.features = ko.observableArray();

    self.transients = {};
    self.transients.weatherIcons = [
        {id: 'sunny', name: 'Sunny'},
        {id: 'cloudy', name: 'Cloudy'},
        {id: 'rainy', name: 'Rainy'},
        {id: 'snowy', name: 'Snowy'},
        {id: 'rainbow', name: 'Rainbow'},
        {id: 'starry', name: 'Starry'},
        {id: 'stormy', name: 'Stormy'}
    ];
    self.addFeature = function () {
        self.features.push(new FeatureVM());
    };

    self.deleteFeature = function (feature) {
        self.features.remove(feature);
    };

    self.loadSeason = function (seasons) {
        self.seasonName(seasons.seasonName);
        self.description(seasons.description);
        self.seasonMonths(seasons.seasonMonths);
        self.seasonNameEnglish(seasons.seasonNameEnglish);
        self.weatherIcon(seasons.weatherIcon);
        self.features($.map(seasons.features ? seasons.features : [], function (obj, i) {
            return new FeatureVM(obj);
        }));
    };

    self.loadSeason(seasons);
};

var FeatureVM = function (feature) {
    var self = this;
    if (!feature) feature = {};

    self.featureName = ko.observable();
    self.featureNameEnglish = ko.observable();
    self.description = ko.observable();
    self.speciesName = ko.observable();
    self.species = new SpeciesViewModel();
    self.speciesLink = ko.observable();
    self.thumbImages = ko.observableArray();
    self.images = ko.observableArray();
    self.loadFeature = function (feature) {
        self.featureName(feature.featureName);
        self.featureNameEnglish(feature.featureNameEnglish);
        self.description(feature.description);
        self.speciesName(feature.speciesName);
        self.speciesLink(feature.speciesLink);
        self.species.loadSpecies(feature.species);
        self.thumbImages($.map(feature.thumbImages ? feature.thumbImages : [], function (obj, i) {
            return new ImageUrl(obj);
        }));
    };

    self.transients = {};
    self.transients.bioSearch = ko.observable(scConfig.speciesSearchUrl);
    self.transients.shortDescription = ko.pureComputed(function () {
        var limit = 100;
        if (self.description().length > limit) {
            return self.description().slice(0, limit) + "...";
        } else {
            return self.description();
        }
    });
    self.transients.id = ko.observable(Math.random().toString(36).substr(2, 36));

    self.addThumbImageUrl = function () {
        self.thumbImages.push(new ImageUrl())
    };

    self.deleteThumbImageUrl = function (url) {
        self.thumbImages.remove(url);
    };

    self.loadFeature(feature);
};

var ImageUrl = function (image) {
    var self = this;
    if (!image) image = {};
    self.url = ko.observable(image.url);
};



var SeasonalCalendarsImageIdentification = function () {
    var self = this;
    self.imageUrl = ko.observable("http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=2cbfcbf0-ae37-4f36-87b9-95a54b4ffdff");
    self.jsonResult = ko.observable();
    self.concepts = ko.observableArray();
    self.loading = ko.observable();
    self.matched = ko.observable(0);
    self.loadTime = ko.observable();
    self.load = function () {
        var ajaxTime = new Date().getTime();
        if(self.loading()) {
            return;
        }
        self.matched(0);
        self.loadTime();
        var data = '{"imageUrl": "'+self.imageUrl().trim()+'"}';
        self.loading(true);
        self.concepts([]);
        $.ajax({
            cache: false,
            url: scConfig.imagePostUrl,
            type: 'POST',
            data: data,
            contentType: 'application/json',
            success: function (data) {
                debugger;
                console.log(data);
                if(data.aiResult) {
                    var concepts = data.aiResult[0];
                    self.concepts($.map(concepts ? concepts : [], function (obj, i) {
                        var obj = new SeasonalCalendarsImageIdentificationConcepts(obj);
                        if(obj.value() >= 0.2) {
                            self.matched(1);
                        }
                        return obj;
                    }));

                    self.jsonResult(concepts);
                } else {
                    self.matched(2);
                    self.jsonResult("Error");
                }

                if(self.matched() == 0) {
                    self.matched(2);
                }
            },
            complete:function() {
                var totalTime = new Date().getTime() - ajaxTime;
                console.log(totalTime);
                self.loadTime(totalTime);
                self.loading(false);
            },
            error: function (data) {
                showAlert("Error: " + data.responseText, "alert-danger", "");
            }
        });
    };
};

var SeasonalCalendarsImageIdentificationConcepts = function (o){
    var self = this;
    self.id = ko.observable(o.id);
    self.name = ko.observable(o.name);
    self.value = ko.observable(o.value);
    self.value(self.value().toFixed(20));

    //Parse
    self.commonName = ko.observable();
    self.scientificName = ko.observable();

    var array = self.id().split('|');
    if(array.length > 0){
        self.commonName(array[0].replace("_", " ").toUpperCase());
    }
    if(array.length >= 1){
        self.scientificName(array[1].replace("_", " ").toUpperCase());
    }
};