/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService, $filter, $sessionStorage) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.defaultSearchOptions = {
        matchAll: true,
        nameOnly: false,
        includeNameAttributes: false,
        hideStubs: true,
        searchAla: true,
        searchNsl: true
    };

    self.searchOptions = _.clone(self.defaultSearchOptions);
    self.searchResults = {};
    self.selectedSearch = util.getSearchTypeFromOptions(self.searchOptions);
    self.pageSize = 25;
    self.searchTerm = "";

    self.search = function (pageSize, offset) {
        self.searchOptions.offset = offset || 0;
        self.searchOptions.pageSize = pageSize || self.pageSize;

        var searchResult = profileService.search(self.opusId, self.searchTerm, self.searchOptions);
        searchResult.then(function (data) {
                self.searchResults = data;

                angular.forEach(self.searchResults.items, function (profile) {
                    profile.image = {
                        status: 'not-checked',
                        type: {}
                    };
                });

                persistSearchOptions();
                cacheSearchResult(self.searchResults);
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    function cacheSearchResult(result) {
        if (!$sessionStorage.searches) {
            $sessionStorage.searches = {};
        }
        if (!$sessionStorage.searches[self.opusId ? self.opusId : 'all']) {
            $sessionStorage.searches[self.opusId ? self.opusId : 'all'] = {};
        }
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].result = result;
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].term = self.searchTerm;
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].options = self.searchOptions;
    }

    function persistSearchOptions() {
        if (!$sessionStorage.searches) {
            $sessionStorage.searches = {
               options: {}
            };
        }

        if(!$sessionStorage.searches.options){
            $sessionStorage.searches.options = {};
        }

        $sessionStorage.searches.selectedSearch = self.selectedSearch;
        $sessionStorage.searches.options[self.selectedSearch] = {
            matchAll  : self.searchOptions.matchAll,
            hideStubs : self.searchOptions.hideStubs
        };
    }

    function restoreUserConfigurableSearchOptions() {
        if($sessionStorage.searches && $sessionStorage.searches.options && $sessionStorage.searches.options[self.selectedSearch]){
            angular.extend(self.searchOptions, $sessionStorage.searches.options[self.selectedSearch]);
        }
    }

    function restoreAllSearchOptions() {
        if($sessionStorage.searches){
            if($sessionStorage.searches.selectedSearch){
                self.selectedSearch = $sessionStorage.searches.selectedSearch;
                util.setSearchOptions(self.selectedSearch, self.searchOptions);
            }

            restoreUserConfigurableSearchOptions()
        }
    }

    self.retrieveCachedOrDelegatedSearch = function() {
        if ($sessionStorage.delegatedSearches && $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'] != null) {
            var delegatedSearch = $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'];
            self.searchTerm = delegatedSearch.term || "";
            self.searchOptions = delegatedSearch.searchOptions ? delegatedSearch.searchOptions : _.clone(self.searchOptions);
            if($sessionStorage.searches && $sessionStorage.searches.selectedSearch){
                self.selectedSearch = $sessionStorage.searches.selectedSearch;
            } else {
                self.selectedSearch = util.getSearchTypeFromOptions(self.searchOptions);
            }

            restoreUserConfigurableSearchOptions();
            delete $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'];

            self.search();
        } else {
            var cachedResult = $sessionStorage.searches ? $sessionStorage.searches[self.opusId ? self.opusId : 'all'] : {};
            if (cachedResult) {
                self.searchResults = cachedResult.result;
                self.searchTerm = cachedResult.term || "";
                self.searchOptions = cachedResult.options ? cachedResult.options : _.clone(self.searchOptions);
                self.selectedSearch = util.getSearchTypeFromOptions(self.searchOptions);
            }
        }
    };

    self.clearSearch = function () {
        self.searchResults = {};
        self.searchTerm = "";

        $sessionStorage.searches[self.opusId ? self.opusId : 'all'] = {};

        self.resetSearchOptions();
    };

    self.resetSearchOptions = function() {
        self.searchOptions = _.clone(self.defaultSearchOptions);
        util.setSearchOptions(self.selectedSearch, self.searchOptions);

    };

    self.loadImageForProfile = function (profileId) {
        var profile = $filter('filter')(self.searchResults.items, {uuid: profileId}, true)[0];

        if (profile.image.status == 'not-checked') {
            var future = profileService.getPrimaryImage(profile.opusId, profileId);
            profile.image.status = 'checking';
            future.then(function (data) {
                if (data) {
                    profile.image.url = data.thumbnailUrl;
                    profile.image.type = data.type;
                }
                profile.image.status = 'checked';
            });
        }
    };

    self.setSearchOption = function (option) {
        self.selectedSearch = option;
        self.searchOptions = _.clone(self.defaultSearchOptions);
        util.setSearchOptions(option, self.searchOptions);
        restoreUserConfigurableSearchOptions();
    };

    self.retrieveCachedOrDelegatedSearch();

    if (!self.searchResults) {
        restoreAllSearchOptions();
        self.search();
    }

    self.autoCompleteSearchByScientificName = function (prefix) {
        if (util.isSearchSettingFor('scientificname', self.searchOptions)) {
            return profileService.profileSearch(self.opusId, prefix, true, self.sortOption, true).then(function (data) {
                // need to have filter to limit the result because of limitTo not working in typehead
                // https://github.com/angular-ui/bootstrap/issues/1740
                return $filter('limitTo')($filter("orderBy")(data, "scientificName"), 10);
            });
        } else {
            return {};
        }
     };

    self.formatName = function (profile) {
        if (!profile) {
            return null;
        }

        var fullName  = profile.matchedName && profile.scientificName == profile.matchedName.scientificName ? profile.fullName : null;
        return util.formatScientificName(profile.scientificName, profile.nameAuthor, fullName);

    };

    self.formatReason = function (profile) {
        if (!profile || !profile.matchInfo || !profile.matchInfo.reason) {
            return null;
        }

        var matchInfo = profile.matchInfo;

        if (matchInfo.reason == 'accname') {
            return "accepted name"
        } else if (matchInfo.reason == 'internalmatch') {
            var formattedMatchedName = util.formatScientificName(matchInfo.matchName.scientificName, matchInfo.matchName.nameAuthor, matchInfo.matchName.fullName);
            return "internally matched to " + formattedMatchedName;
        } else if (matchInfo.reason == 'nslaccname') {
            var formattedNSLName = util.formatScientificName(matchInfo.nslmatchname.canonicalName, matchInfo.nslmatchname.scientificNameAuthorship, "");
            return "accepted name for " + formattedNSLName;
        } else {
            return "";
        }
    };

    self.isCommonName = function () {
        return util.isSearchSettingFor('commonname', self.searchOptions);
    };

    self.isScientificName = function () {
        return util.isSearchSettingFor('scientificname', self.searchOptions);
    };

    self.isContainingText = function () {
        return util.isSearchSettingFor('text', self.searchOptions);
    };
});