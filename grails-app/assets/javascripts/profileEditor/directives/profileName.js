profileEditor.directive('profileName', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            name: '=',
            rank: '=',
            valid: '=',
            currentProfileId: '=',
            manuallyMatchedGuid: '=',
            focus: '=',
            manualHierarchy: '=',
            editMode: '@'
        },
        templateUrl: '/profileEditor/profileNameCheck.htm',
        controller: ['$scope', 'profileService', 'util', 'config', '$http', '$filter', function ($scope, profileService, util, config, $http, $filter) {
            $scope.nameCheck = null;
            $scope.opusId = util.getEntityId("opus");

            $scope.ranks = _.values(util.RANK);

            var orderBy = $filter("orderBy");
            
            $scope.resetNameCheck = function () {
                $scope.nameCheck = null;
                $scope.errors = [];
                $scope.warnings = [];
                $scope.valid = false;
                $scope.manuallyMatchedGuid = null;
                $scope.manuallyMatchedName = null;
                $scope.showManualMatch = false;
                $scope.showManualHierarchy = false;
                if (!_.isUndefined($scope.manualHierarchy)) {
                    $scope.manualHierarchy.length = 0;
                }
            };

            $scope.formatName = function (scientificName, nameAuthor, fullName) {
                return util.formatScientificName(scientificName, nameAuthor, fullName);
            };

            $scope.checkName = function () {
                if (!$scope.name) {
                    return;
                }

                $scope.resetNameCheck();

                var promise = profileService.checkName($scope.opusId, $scope.name);
                promise.then(function (report) {
                        $scope.nameCheck = report;

                        $scope.nameCheck.matchedName.formattedName = util.formatScientificName(report.matchedName.scientificName, report.matchedName.nameAuthor, report.matchedName.fullName);

                        $scope.nameCheck.providedNameDuplicate = report.providedNameDuplicates.length > 0 && report.providedNameDuplicates[0].profileId != $scope.currentProfileId;
                        $scope.nameCheck.noMatch = !report.matchedName || !report.matchedName.scientificName;
                        $scope.nameCheck.mismatch = report.matchedName && report.matchedName.scientificName && report.matchedName.scientificName.toLowerCase() != $scope.name.toLowerCase() && report.matchedName.fullName.toLowerCase() != $scope.name.toLowerCase();
                        $scope.nameCheck.matchedNameDuplicate = $scope.nameCheck.mismatch && report.matchedNameDuplicates.length > 0 && report.matchedNameDuplicates[0].profileId != $scope.currentProfileId;
                        if (report.matchedName && !_.isUndefined($scope.manualHierarchy)) {
                            $scope.manualHierarchy.length = 0;
                        }
                        $scope.validate();
                    },
                    function () {
                        $scope.errors.push("An error occurred while checking the name.");
                    }
                );
            };

            $scope.useMatchedName = function () {
                $scope.name = $scope.nameCheck.matchedName.scientificName;

                $scope.checkName();
            };

            $scope.checkMatch = function (match) {
                $scope.name = match.fullName;
                $scope.checkName();
            };

            $scope.toggleManualMatch = function () {
                $scope.showManualMatch = !$scope.showManualMatch;
                $scope.showManualHierarchy = false;
                $scope.manualHierarchy.length = 0;
            };

            $scope.toggleManualHierarchy = function () {
                $scope.showManualHierarchy = !$scope.showManualHierarchy;
                $scope.showManualMatch = false;

                if ($scope.showManualHierarchy && ($scope.manualHierarchy == null || $scope.manualHierarchy.length == 0)) {
                    // add the first entry for the classification, which is the profile itself.
                    $scope.manualHierarchy.push({guid: null, name: $scope.name, rank: null, checked: true});
                } else {
                    $scope.manualHierarchy.length = 0;
                }
            };

            $scope.autocompleteName = function (prefix) {
                $scope.providedHierarchyName = prefix;
                return profileService.autocompleteName(prefix).then(function (response) {
                    return orderBy(response.data.autoCompleteList, "name");
                });
            };

            $scope.autocompeteProfile = function (prefix) {
                $scope.providedHierarchyName = prefix;
                return profileService.profileSearch($scope.opusId, prefix, true).then(function (data) {
                    return orderBy(data, "scientificName");
                });
            };

            $scope.onSelectManualMatch = function (selectedName) {
                $scope.manuallyMatchedGuid = selectedName.guid;
                $scope.manuallyMatchedName = selectedName.name;
            };

            /**
             * Keeps the name of the hierarchy item being edited up to date as the user types
             *
             * @param index The index of the hierarchy item being edited
             */
            $scope.manualHierarchyValueChanged = function (index) {
                $scope.manualHierarchy[index].name = $scope.providedHierarchyName;
                // clearing these as the user types prevents the user from selecting a known name from the autocomplete
                // then modifying it to something unrecognised
                $scope.manualHierarchy[index].guid = null;
                $scope.manualHierarchy[index].rank = null;
                $scope.manualHierarchy[index].checked = false;
            };

            /**
             * Called when a name is selected from the autocomplete, OR when Check Name is called, for the hierarchy fields
             *
             * If a name is selected from the autocompplete, then 'ancestor' will be defined - we need to copy the guid,
             * name and rank from the ancestor to our hierarchy. The Check Name will be disabled
             *
             * If Check Name was selected (i.e. the hierarchy item is an unrecognised name), then we need to add another
             * level to the hierarchy to allow the user to specify the next ancestor.
             *
             * @param index The index of the item in the hierarchy
             * @param ancestor Defined if the user has selected a known name from the autocomplete, undefined if they have entered an unrecognised name
             */
            $scope.onSelectManualHierarchy = function (index, ancestor) {
                var selectionChanged = function () {
                    $scope.manualHierarchy[index].checked = true;
                    $scope.providedHierarchyName = null;
                    $scope.validate();
                };

                var selectItem = function (item) {
                    $scope.manualHierarchy[index].guid = item.profileId || item.guid;
                    $scope.manualHierarchy[index].name = item.scientificName || item.name;
                    $scope.manualHierarchy[index].rank = item.rank;
                    $scope.manualHierarchy[index].profile = _.isUndefined(item.guid);
                    $scope.manualHierarchy[index].originalName = item.originalName;

                    if (index < $scope.manualHierarchy.length - 1) {
                        $scope.manualHierarchy.splice(index + 1);
                    }
                };

                if (!_.isUndefined(ancestor) && !_.isUndefined(ancestor.profileId) && ancestor.profileId != null) {
                    selectItem(ancestor);
                    selectionChanged();
                } else {
                    // 1. see if there is a profile with a matching name (this covers the user typing a profile name but
                    //    not actually selecting it from the autocomplete list
                    profileService.profileSearch($scope.opusId, $scope.providedHierarchyName, false).then(function (data) {
                        if (data && data.length == 1) {
                            selectItem(data[0]);
                            selectionChanged();
                        } else {
                            // 2. If there is no profile with that name, then try to match the name against the name
                            //    index so that we can pull the hierarchy from a valid name
                            var promise = profileService.checkName($scope.opusId, $scope.providedHierarchyName);
                            promise.then(function (report) {
                                    if (report && report.matchedName && report.matchedName.guid) {
                                        var match = {
                                            scientificName: report.matchedName.scientificName,
                                            guid: report.matchedName.guid,
                                            rank: report.matchedName.rank,
                                            originalName: report.providedName
                                        };

                                        selectItem(match)
                                    }

                                    selectionChanged();
                                },
                                function () {
                                    $scope.errors.push("An error occurred while checking the name.");
                                }
                            );
                        }
                    });
                }
            };

            $scope.validate = function () {
                $scope.valid = $scope.nameCheck != null && !$scope.nameCheck.providedNameDuplicate && $scope.validateHierarchy();
            };

            $scope.addHierarchyItem = function () {
                $scope.manualHierarchy.push({guid: null, name: null, rank: null});
            };

            $scope.validateHierarchy = function () {
                var validHierarchy = true;

                if ($scope.manualHierarchy != null && $scope.manualHierarchy.length != 0) {
                    var previousHasGuid = false;
                    $scope.manualHierarchy.forEach(function (hierarchyItem) {

                        // all items in the hierarchy must have had their names checked
                        if (!hierarchyItem.checked) {
                            validHierarchy = false;
                        }
                        // all steps in the hierarchy must have a name and rank
                        if (hierarchyItem.name == null || _.isUndefined(hierarchyItem.name) || _.isEmpty(hierarchyItem.name) ||
                            hierarchyItem.rank == null || _.isUndefined(hierarchyItem.rank) || _.isEmpty(hierarchyItem.rank)) {
                            validHierarchy = false;
                        }
                        // steps in the hierarchy do not need to have a guid, but if one step does then the hierarchy should end there
                        // steps before the one with the guid can have no guid.
                        // the ui should prevent this
                        if (previousHasGuid) {
                            validHierarchy = false;
                        }
                        previousHasGuid = hierarchyItem.guid != null && !_.isEmpty(hierarchyItem.guid);
                    });
                }

                return validHierarchy;
            };

            $scope.trimManualHierarchy = function (index) {
                $scope.manualHierarchy.splice(index);
                if ($scope.manualHierarchy.length == 0) {
                    $scope.toggleManualHierarchy();
                }
            };

            $scope.$watch("manualHierarchy", function(newVal, oldVal) {
                if (!_.isUndefined(newVal) && newVal != null) {
                    if ((_.isUndefined(oldVal) || oldVal == null) && newVal.length > 0 && $scope.editMode) {
                        // set the valid flag to true on the initial load when in edit mode for a profile with a manual
                        // classification so that the classification can be displayed correctly
                        $scope.nameCheck = {providedNameDuplicate: false};
                        $scope.showManualHierarchy = true;
                    }

                    $scope.validate();
                }
            }, true);
        }],
        link: function (scope, element, attrs, ctrl) {

        }
    }
});