/**
 * Directive to display a generic search control e.g. in the page header.
 *
 * Search behaviour will depend on whether the URL contains an OpusId or not.
 *
 * The search action redirects the user to either the Collection landing page's search tab, or the Collection home page
 * search tab, and populates the session with the search term, which the main SearchController will check and execute the
 * search. I.e. this directive does not actually perform a search - it merely sends the user to the appropriate search
 * page.
 */
profileEditor.directive('delegatedSearch', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            layout: '@'
        },
        templateUrl: '/profileEditor/delegatedSearch.htm',
        controller: ['$scope', 'profileService', '$sessionStorage', 'util', '$filter', function ($scope, profileService, $sessionStorage, util, $filter) {
            var self = this;
            self.layout = $scope.layout || 'small';

            self.searchOptions = {
                matchAll: true,
                nameOnly: false,
                includeNameAttributes: true
            };
            self.showOptions = false;

            self.searchTerm = null;
            self.selectedSearch = ($sessionStorage.searches || {}).selectedSearch || 'text';
            self.contextPath = $browser.baseHref();

            self.search = function() {
                if (!self.searchTerm || _.isUndefined(self.searchTerm) || self.searchTerm.trim().length == 0) {
                    return;
                }

                var opusId = util.getEntityId("opus");
                if (!$sessionStorage.delegatedSearches || _.isUndefined($sessionStorage.delegatedSearches)) {
                    $sessionStorage.delegatedSearches = {};
                }

                $sessionStorage.delegatedSearches[opusId ? opusId : 'all'] = {
                    term: self.searchTerm,
                    searchOptions: self.searchOptions
                };

                if(!$sessionStorage.searches){
                    $sessionStorage.searches = {}
                }

                $sessionStorage.searches.selectedSearch = self.selectedSearch;
                if (_.isUndefined(opusId) || opusId == null || !opusId) {
                    util.redirect(util.contextRoot() + "/opus/search");
                } else {
                    util.redirect(util.contextRoot() + "/opus/" + opusId + "/search");
                }
            };

            self.autoCompleteSearchByScientificName = function (prefix) {
                if (util.isSearchSettingFor('scientificname', self.searchOptions)) {
                    var opusId = util.getEntityId("opus");
                    return profileService.profileSearch(opusId, prefix, true, null, true).then(function (data) {
                        // need to have filter to limit the result because of limitTo not working in typehead
                        // https://github.com/angular-ui/bootstrap/issues/1740
                        return $filter('limitTo')($filter("orderBy")(data, "scientificName"), 10);
                    });
                } else {
                    return {};
                }
            };

            self.setSearchOption = function (option) {
                self.selectedSearch = option;
                util.setSearchOptions(option, self.searchOptions);
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

            self.selectedSearch && self.setSearchOption(self.selectedSearch);
        }],
        controllerAs: "delSearchCtrl",
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("layout", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.layout = newValue;
                }
            });
        }
    };
});

