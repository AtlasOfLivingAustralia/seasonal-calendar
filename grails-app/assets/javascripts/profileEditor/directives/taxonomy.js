profileEditor.directive('taxonomy', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            data: '=',
            opusId: '=',
            layout: '@',
            sIncludeRank: '@includeRank', // the following are never bound to expressions, just literal boolean / int values.
            sShowChildren: '@showChildren', // So we need to accept these values as strings and parse them in the controller.
            sShowChildrenForLastOnly: '@showChildrenForLastOnly',
            sShowInfraspecific: '@showInfraspecific',
            sShowWithProfileOnly: '@showWithProfileOnly',
            sLimit: '@limit'
        },
        bindToController: true,
        templateUrl: function(element, attrs) {
            return '/profileEditor/' + (attrs.layout == 'tree' ? 'taxonomy-tree.htm' : 'taxonomy-horizontal.htm');
        },
        controllerAs: 'taxonomyCtrl',
        controller: function ($scope, config, $modal, messageService, profileService) {
            var self = this;
            self.contextPath = config.contextPath;
            self.showChildren = isTruthy(self.sShowChildren);
            self.showChildrenForLastOnly = isTruthy(self.sShowChildrenForLastOnly);
            self.showInfraspecific = isTruthy(self.sShowInfraspecific);
            self.showWithProfileOnly = isTruthy(self.sShowWithProfileOnly);
            self.includeRank = isTruthy(self.sIncludeRank);
            self.limit = parseInt(self.sLimit) || -1;
            self.pageSize = 15;

            /**
             * Fetch all subordinate taxa (of any rank, not just the immediate children) and populate the 'children'
             * property of the taxon. This is used to display a drown-down list of subordinate taxa.
             */
            self.showAllSubordinateTaxaList = function(taxon) {
                if (_.isUndefined(taxon.children) || !taxon.children) {
                    taxon.loading = true;
                    var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon.rank, taxon.name, self.pageSize, 0);
                    results.then(function (data) {
                            taxon.children = data;
                            taxon.loading = false;
                        },
                        function () {
                            taxon.loading = false;
                            messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                        }
                    );
                }
            };

            /**
             * Executes showAllSubordinateTaxa when the directive is loaded and the last rank in hierarchy is Species.
             * This lets us display the infraspecific taxa on the page at page load, rather than on a click event
             */
            self.initialiseAllSubordinateTaxaList = function() {
                if (self.layout != "tree" && !_.isUndefined(self.taxonomy) && self.showInfraspecific) {
                    var lastTaxon = self.taxonomy[self.taxonomy.length - 1];
                    if (!_.isUndefined(lastTaxon) && lastTaxon.rank == "species") {
                        self.showAllSubordinateTaxaList(lastTaxon);
                    }
                }
            };

            /**
             * Fetch all subordinate taxa (of any rank, not just the immediate children) and display a modal dialog with
             * pagination.
             */
            self.showAllSubordinateTaxaPopup = function (level, scientificName) {
                $modal.open({
                    templateUrl: "/profileEditor/showTaxonChildren.htm",
                    controller: function (profileService, messageService, $modalInstance, taxon, contextPath, opusId) {
                        var modal = this;

                        modal.pageSize = 10;
                        modal.taxon = taxon;
                        modal.opusId = opusId;
                        modal.contextPath = contextPath;
                        modal.totalResults = 0; // TODO

                        modal.loadChildren = function (offset) {
                            if (offset === undefined) {
                                offset = 0;
                            }

                            var results = profileService.profileSearchByTaxonLevelAndName(modal.opusId, taxon.rank, taxon.name, modal.pageSize, offset, {immediateChildrenOnly: true});
                            results.then(function (data) {
                                    modal.profiles = data;
                                },
                                function () {
                                    messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                                }
                            );
                        };

                        function getChildCount() {
                            var results = profileService.profileCountByTaxonLevelAndName(modal.opusId, taxon.rank, taxon.name, {immediateChildrenOnly: true});
                            results.then(function (data) {
                                modal.totalResults = data.total;
                            });
                        }

                        getChildCount();

                        modal.loadChildren(0);

                        modal.cancel = function () {
                            $modalInstance.dismiss("cancel");
                        }
                    },
                    controllerAs: "taxonChildrenCtrl",
                    size: "lg",
                    resolve: {
                        taxon: function () {
                            return {rank: level, name: scientificName};
                        },
                        contextPath: function () {
                            return self.contextPath;
                        },
                        opusId: function () {
                            return self.opusId;
                        }
                    }
                });
            };

            /**
             * Fetch only the immediate children of the specified taxon (e.g. if taxon is a genus, then only get the
             * species, not the subspecies)
             */
            self.loadSubordinateTaxa = function (offset, taxon, openCloseSection) {
                if (openCloseSection) {
                    taxon.expanded = !taxon.expanded || false;

                    if (offset > 0 || taxon.showingCurrentProfileOnly) {
                        return;
                    }
                }

                if (taxon.expanded) {
                    if (_.isUndefined(offset) || offset < 0) {
                        offset = 0;
                    }

                    var results = profileService.profileSearchGetImmediateChildren(self.opusId, taxon.rank, taxon.name, self.pageSize, offset, taxon.filter);
                    taxon.loading = true;
                    results.then(function (data) {
                            if (!_.isUndefined(data) && data.length > 0) {
                                if (offset > 0) {
                                    angular.forEach(data, function(child) {
                                        taxon.children.push(child);
                                    });
                                } else {
                                    taxon.children = data;
                                }
                                taxon.offset = (taxon.offset || 0) + self.pageSize;
                                taxon.showingCurrentProfileOnly = false;

                                taxon.mightHaveMore = data.length >= self.pageSize;
                            } else {
                                if (!_.isUndefined(taxon.filter) && taxon.filter) {
                                    taxon.children = [];
                                }
                                taxon.mightHaveMore = false;
                            }
                            taxon.loading = false;
                        },
                        function () {
                            taxon.loading = false;
                            messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                        }
                    );
                }
            };

            self.filterChanged = function(taxon) {
                taxon.offset = 0;
                if (_.isUndefined(taxon.filter) || taxon.filter.trim().length == 0) {
                    self.loadSubordinateTaxa(0, taxon, false);
                }
            };

            self.hierarchialiseTaxonomy = function() {
                var previous = null;

                if (self.layout == 'tree') {
                    var tmp = angular.copy(self.taxonomy);
                    angular.forEach (tmp, function (next) {
                        if (previous != null) {
                            previous.children = [next];
                            previous.expanded = true;
                            previous.offset = -1;
                            previous.showingCurrentProfileOnly = true;
                            previous.mightHaveMore = true;
                            previous.filter = null;
                        }
                        previous = next;
                    });
                    self.hierarchy = [tmp[0]];
                }
            };

            self.removeRanksWithNoProfile = function() {
                var tmp = angular.copy(self.taxonomy);
                self.taxonomy = [];
                angular.forEach(tmp, function(taxon) {
                    if (taxon.profileId) {
                        self.taxonomy.push(taxon);
                    }
                });
            };

            self.initTaxonomy = function(data) {
                self.taxonomy = angular.copy(data);

                self.initialiseAllSubordinateTaxaList();
            };

            self.initTaxonomy(self.data);

            $scope.$watch("taxonomyCtrl.data", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    self.initTaxonomy(newValue);
                }
            });


            if (self.showInfraspecific) {
                self.initialiseAllSubordinateTaxaList();
            }

            if (self.layout == "tree" && !_.isUndefined(self.taxonomy)) {
                self.hierarchialiseTaxonomy();
            }

            if (self.showWithProfileOnly && !_.isUndefined(self.taxonomy)) {
                self.removeRanksWithNoProfile();
            }
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }
});