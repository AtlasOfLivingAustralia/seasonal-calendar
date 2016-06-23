var SeasonalCalendarsMenuVM = function() {
    var self = this;

    self.items = ko.observableArray();
    self.populate = function(data){
        self.items([]);
        self.items($.map(data.calendars ? data.calendars : [], function (obj, i) {
            return new SeasonalCalendarsMenuItems(obj);
        }));
    };

    self.addCalendar = function(){
        window.location.href = scConfig.calendarHome;
    };
    self.transients = {};
    self.transients.isNew = ko.pureComputed(function() {
        var calendarId = scConfig.id;
        return (calendarId == "");
    });


    self.loadMenuItems = function() {
        return $.ajax({
            url: scConfig.listCalendars,
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

var SeasonalCalendarsMenuItems = function (item){
    var self = this;
    if(!item) item = {};
    self.name = ko.observable(item.name);
    self.calendarId = ko.observable(item.calendarId);
    self.calendarStatus = ko.observable(item.calendarStatus);
    self.imageUrl = ko.observable(item.imageUrl);


    self.redirect = function(){
        window.location.href = scConfig.calendarHome + "/" + self.calendarId();
    };

    self.redirectToDetailPage = function(){
        window.location.href = scConfig.previewCalendar + "/" + self.calendarId();
    };

    self.transients = {};
    self.transients.isActive = ko.pureComputed(function() {
        var calendarId = scConfig.id;
        return (calendarId == self.calendarId());
    });
};

var SeasonalCalendarVM = function(){
    var self = this;
    self.calendarId = ko.observable();
    self.calendarName = ko.observable();
    self.imageUrl = ko.observable();
    self.description = ko.observable();
    self.calendarStatus = ko.observable();
    self.externalLink = ko.observable();
    self.seasons = ko.observableArray();
    self.multimedia = ko.observable();
    self.transients = {};
    self.transients.iframe = ko.pureComputed(function() {
        if(self.multimedia()){
            return buildiFrame(self.multimedia());
        }

        return;
    });
    self.populateCalendar = function(calendar){
        self.calendarId(calendar.calendarId);
        self.calendarName(calendar.calendarName);
        self.description(calendar.description);
        self.externalLink(calendar.externalLink);
        self.imageUrl(calendar.imageUrl);
        self.multimedia(calendar.multimedia);
        self.calendarStatus((calendar.calendarStatus ? calendar.calendarStatus : 'unpublished'));
        self.seasons($.map(calendar.seasons ? calendar.seasons : [], function (obj, i) {
            return new SeasonVM(obj);
        }));
    };

    self.previewCalendar = function(calendar){
        window.open(scConfig.previewCalendar+"/"+ self.calendarId());
    };

    self.deleteCalendar = function(){
        var url = scConfig.deleteCalendar+"/"+self.calendarId();
        var divId = "seasonal-calendar-result";

        self.calendarStatus('deleted');
        return $.ajax({
            url: url,
            type: 'POST',
            data: self.seasonAsJson(),
            contentType: 'application/json',
            success: function (data) {
                if (data.error) {
                    showAlert("Error: "+data.error, "alert-danger",divId);
                }
                else {
                    showAlert("Successfully deleted, redirecting...", "alert-success",divId);
                    setInterval(function(){
                        window.location.href = scConfig.calendarHome;
                    }, 3000);
                }
            },
            error: function (data) {
                showAlert("Error: "+data, "alert-danger",divId);
            }
        });

    };

    self.loadCalendar = function(){
        var calendarId = scConfig.id;

        if(calendarId) {
            var url = scConfig.getCalendar+"/"+ calendarId;
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

    self.add = function(){
        self.seasons.push(new SeasonVM());
    };

    self.publish = function() {
        if (!$('#calendar-validation').validationEngine('validate')) {
            return;
        }
        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() :  scConfig.addCalendar;
        var divId = "seasonal-calendar-result";
        self.calendarStatus('published');
        return $.ajax({
            url: url,
            type: 'POST',
            data: self.seasonAsJson(),
            contentType: 'application/json',
            success: function (data) {
                if (data.error) {
                    showAlert("Error: "+data.error, "alert-danger",divId);
                }
                else {
                    if(self.calendarId()){
                        showAlert("Successfully published", "alert-success",divId);
                    } else{
                        showAlert("Successfully published, reloading the page...", "alert-success",divId);
                        window.location.href = scConfig.calendarHome + "/" + data.calendarId;
                    }
                }
            },
            error: function (data) {
                showAlert("Error: "+data, "alert-danger",divId);
            }
        });
    };

    self.unpublish = function() {
        if (!$('#calendar-validation').validationEngine('validate')) {
            return;
        }
        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() :  scConfig.addCalendar;
        var divId = "seasonal-calendar-result";

        self.calendarStatus('unpublished');
        return $.ajax({
            url: url,
            type: 'POST',
            data: self.seasonAsJson(),
            contentType: 'application/json',
            success: function (data) {
                if (data.error) {
                    showAlert("Error: "+data.error, "alert-danger",divId);
                }
                else {
                    if(self.calendarId()){
                        showAlert("Successfully unpublished", "alert-success",divId);
                    } else{
                        showAlert("Successfully unpublished, reloading the page...", "alert-success",divId);
                        window.location.href = scConfig.calendarHome + "/" + data.calendarId;
                    }
                }
            },
            error: function (data) {
                showAlert("Error: "+data, "alert-danger",divId);
            }
        });
    };

    self.save = function(){
        if (!$('#calendar-validation').validationEngine('validate')) {
            return;
        }
        var url = self.calendarId() ? scConfig.editCalendar + "/" + self.calendarId() :  scConfig.addCalendar;
        var divId = "seasonal-calendar-result";
        return $.ajax({
            url: url,
            type: 'POST',
            data: self.seasonAsJson(),
            contentType: 'application/json',
            success: function (data) {
                if (data.error) {
                    showAlert("Error: "+data.error, "alert-danger",divId);
                }
                else {
                    if(self.calendarId()){
                        showAlert("Successfully updated", "alert-success",divId);
                    } else{
                        showAlert("Successfully added, reloading the page...", "alert-success",divId);
                        window.location.href = scConfig.calendarHome + "/" + data.calendarId;
                    }
                }
            },
            error: function (data) {
                showAlert("Error: "+data, "alert-danger",divId);
            }
        });
    };

    self.deleteSeason = function(season){
        self.seasons.remove(season);
    };

    self.seasonAsJson = function (){
        var jsData = {};
        jsData.calendarId = self.calendarId();
        jsData.calendarName = self.calendarName();
        jsData.imageUrl = self.imageUrl();
        jsData.description = self.description();
        jsData.calendarStatus = self.calendarStatus();
        jsData.externalLink = self.externalLink();
        jsData.multimedia = self.multimedia();
        jsData.seasons = ko.mapping.toJS(self.seasons, {ignore:['transients']});
        return JSON.stringify(jsData, function (key, value) { return value === undefined ? "" : value; });
    };

    self.loadCalendar();

};

var SeasonVM = function (seasons){
    var self = this;
    if(!seasons) seasons = {};

    self.seasonName = ko.observable();
    self.description = ko.observable();
    self.months = ko.observable();
    self.weatherIcon = ko.observable();
    self.categories = ko.observableArray();

    self.transients = {};
    self.transients.weatherIcons =  [
        {id: 'sunny', name: 'Sunny'},
        {id: 'cloudy', name: 'Cloudy'},
        {id: 'rainy', name: 'Rainy'},
        {id: 'snowy', name: 'Snowy'},
        {id: 'rainbow', name: 'Rainbow'},
        {id: 'starry', name: 'Starry'},
        {id: 'stormy', name: 'Stormy'}
    ];
    self.addCategory = function(){
        self.categories.push(new CategoryVM());
    };

    self.deleteCategory = function(category){
        self.categories.remove(category);
    };

    self.loadSeason = function(seasons){
        self.seasonName(seasons.seasonName);
        self.description(seasons.description);
        self.months(seasons.months);
        self.weatherIcon(seasons.weatherIcon);
        self.categories($.map(seasons.categories ? seasons.categories : [], function (obj, i) {
            return new CategoryVM(obj);
        }));
    };

    self.loadSeason(seasons);
};

var CategoryVM = function(category) {
    var self = this;
    if(!category) category = {};

    self.categoryName = ko.observable();
    self.description = ko.observable();
    self.speciesName = ko.observable();
    self.speciesLink = ko.observable();
    self.thumbImages = ko.observableArray();
    self.images = ko.observableArray();
    self.loadCategory = function(category){
        self.categoryName(category.categoryName);
        self.description(category.description);
        self.speciesName(category.speciesName);
        self.speciesLink(category.speciesLink);
        self.thumbImages($.map(category.thumbImages ? category.thumbImages : [], function (obj, i) {
            return new ImageUrl(obj);
        }));
    };

    self.transients = {};
    self.transients.shortDescription = ko.pureComputed(function(){
        var limit = 100;
        if(self.description().length > limit) {
            return self.description().slice(0,limit) + "...";
        } else{
            return self.description();
        }
    });
    self.transients.id = ko.observable(Math.random().toString(36).substr(2, 36));

    self.addThumbImageUrl = function(){
        self.thumbImages.push(new ImageUrl())
    };

    self.deleteThumbImageUrl = function(url){
        self.thumbImages.remove(url);
    };

    self.loadCategory(category);
};

var ImageUrl = function(image){
    var self = this;
    if(!image) image = {};
    self.url = ko.observable(image.url);
};