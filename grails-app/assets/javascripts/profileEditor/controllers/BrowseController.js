/**
 * Browse profiles by taxonomy controller
 */
profileEditor.controller('BrowseController', function (profileService, util, messageService) {
    var self = this;

    self.pageSizes = [5, 10, 25, 50, 100];
    self.sortOptions = ["name", "taxonomy"];
    self.sortOption = "taxonomy";
    self.pagesToShow = 10;
    self.pageSize = 25;

    self.MAX_FACET_ITEMS = 25;

    self.orderedTaxonLevels = [
        {key: "kingdom", name: "Kingdom", order: 0},
        {key: "phylum", name: "Phylum", order: 1},
        {key: "class", name: "Class", order: 2},
        {key: "subclass", name: "Subclass", order: 3},
        {key: "order", name: "Order", order: 4},
        {key: "family", name: "Family", order: 5},
        {key: "genus", name: "Genus", order: 6},
        {key: "unknown", name: "Unknown Rank", order: 7, help: "This category lists all profiles which do not have a matched name"}
    ];

    self.filters = {};

    self.opusId = util.getEntityId("opus");
    self.taxonResults = {};
    self.taxonLevels = [];
    self.profiles = [];
    self.selectedTaxon = {};
    self.contextPath = util.contextRoot();

    self.searchByScientificName = function (wildcard) {
        if (!self.searchTerm || typeof self.searchTerm == 'undefined' || self.searchTerm.length == 0) {
            self.profiles = [];
        } else {
            if (wildcard === undefined) {
                wildcard = true
            }

            self.selectedTaxon = {};

            var searchResult = profileService.profileSearch(self.opusId, self.searchTerm, wildcard, self.sortOption);
            searchResult.then(function (data) {
                    self.profiles = data;
                },
                function () {
                    messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
                }
            );
        }
    };

    self.getTaxonLevels = function () {
        var levels = profileService.getTaxonLevels(self.opusId);
        levels.then(function (data) {
                self.taxonLevels = data;
            },
            function () {
                messageService.alert("An error occurred while determining taxon levels.");
            });
    };

    self.searchByTaxonLevel = function (level, offset) {
        if (offset === undefined) {
            offset = 0;
        }

        var result = profileService.profileSearchByTaxonLevel(self.opusId, level, self.filters[level], self.MAX_FACET_ITEMS, offset);
        result.then(function (data) {
            if (!self.taxonResults[level]) {
                self.taxonResults[level] = {};
            }
            if (self.filters[level] && offset == 0) {
                self.taxonResults[level] = data;
            } else {
                angular.extend(self.taxonResults[level], data);
            }
        }, function () {
            messageService.alert("Failed to perform taxon level search.");
        });
    };

    self.searchByTaxon = function (taxon, scientificName, count, offset) {
        self.selectedTaxon = {level: taxon, name: scientificName, count: count, profileExist: false};

        if (offset === undefined) {
            offset = 0;
        }

        self.searchTerm = null;

        var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon, scientificName, self.pageSize, offset, { sortBy: self.sortOption});
        results.then(function (data) {
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );

        var getProfile = profileService.getProfile(self.opusId, self.selectedTaxon.name, true);
        getProfile.then(function (data) {
            // It is possible that by the time ajax query returns, the selected taxon was changed by the user.
            // Adding a scientific name check to prevent this scenario.
            if(self.selectedTaxon &&  data.profile && (self.selectedTaxon.name == data.profile.scientificName)) {
                self.selectedTaxon.profileExist = true;
            }
        });
    };

    self.changeSortOrder = function() {
        self.page = 0;
        self.offset = 0;

        if (self.searchTerm) {
            self.searchByScientificName();
        } else if (self.selectedTaxon != {}) {
            self.searchByTaxon(self.selectedTaxon.level, self.selectedTaxon.name, self.selectedTaxon.count, 0);
        }
    }
});