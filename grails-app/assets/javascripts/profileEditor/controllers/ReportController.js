/**
 * Controller for handling reports
 */
profileEditor.controller('ReportController', function (profileService, util, config, messageService) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.pageSize = 25;
    self.isCountOnly = false;

    self.reports = [
        {id: "mismatchedNames", name: "Mismatched Names"},
        {id: "draftProfiles", name: "Draft Profiles"},
        {id: "recentChanges", name: "Recent Updates"},
        {id: "recentComments", name: "Recent Comments" },
        {id: "archivedProfiles", name: "Archived Profiles" }
     ];

    self.selectedReport = null;
    self.reportData = null;

    self.periods = [
        {id: 'today', name: 'Today'},
        {id: 'last7Days', name: 'Last 7 Days'},
        {id: 'last30Days', name: 'Last 30 days'},
        {id: 'custom', name: 'Custom'}
    ];

    self.selectedPeriod = self.periods[2];

    self.datePickerOptions = { datepickerAppendToBody: true, showButtonBar: false };

    self.dates = {
        to: undefined,
        from: undefined
    };

    // controls date picker dialog box to popup
    self.isToOpen = false;
    self.isFromOpen = false;

    self.contextPath = function () {
        return config.contextPath;
    };

    self.reset = function() {
        self.selectedPeriod = self.periods[2];

        self.dates = {
            to: undefined,
            from: undefined
        };

        self.selectedReport = null;
        self.loading = true;
        self.reportData = null;
    };

    self.loadReport = function (reportId, offset) {
        if (self.selectedReport && self.selectedReport.id != reportId) {
            self.reset();
        }

        angular.forEach(self.reports, function (report) {
            if (report.id === reportId) {
                self.selectedReport = report;
            }
        });

        if (offset === undefined) {
            offset = 0;
        }

        var start = self.dates.from ? self.dates.from.getTime() : null,
            end = self.dates.to ? self.dates.to.getTime() : null;

        var future = profileService.loadReport(self.opusId, self.selectedReport.id, self.pageSize, offset, self.selectedPeriod.id, start, end, self.isCountOnly);

        future.then(function (data) {
            self.reportData = data;
            self.loading = false;
        }, function () {
            messageService.alert("An error occurred while producing the report.");
        })
    };

    /**
     * load report all options except custom. Custom requires start and from date. If it is filled, then the report is
     * loaded.
     * @param p
     */
    self.setPeriod = function (p) {
        self.selectedPeriod = p;
        self.clearDates();

        switch (p.id) {
            case 'custom':
                self.reportData = {};
                break;
            case 'last30Days':
            case 'last7Days':
            case 'today':
                self.loadReport(self.selectedReport.id, 0);
                break;
        }
    };

    self.toggleCountOnly = function() {
        self.isCountOnly = !self.isCountOnly;
    };

    self.loadCustomDateReport = function () {
        self.loadReport(self.selectedReport.id, 0);
    };

    self.open = function (popup, $event) {
        $event.stopPropagation();
        $event.preventDefault();

        switch (popup) {
            case 'to':
                self.isToOpen = true;
                self.isFromOpen = false;
                break;
            case 'from':
                self.isFromOpen = true;
                self.isToOpen = false;
                break;
        }
    };

    /**
     * checks if start and end dates are entered and if end is greater than equal to start date
     * @returns {boolean}
     */
    self.checkFormValid = function () {
        return (self.dates.to instanceof Date && self.dates.from instanceof Date) && (self.dates.to >= self.dates.from);
    };

    self.clearDates = function () {
        self.dates.to = self.dates.from = undefined;
    };
});
