profileEditor.directive('vocabularyEditor', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            vocabId: '=',
            vocabName: '@',
            allowReordering: '@',
            allowMandatory: '@',
            allMandatory: '@',
            allowCategories: '@',
            helpUrl: '@'
        },
        templateUrl: '/profileEditor/vocabularyEditor.htm',
        controller: ['$scope', 'profileService', 'util', 'messageService', '$modal', '$filter', function ($scope, profileService, util, messageService, $modal, $filter) {

            $scope.opusId = util.getEntityId("opus");
            $scope.saving = false;
            $scope.newVocabTerm = null;
            $scope.vocabulary = null;
            $scope.replacements = [];
            $scope.allowReordering = true;
            $scope.allowMandatory = true; // allow the user to choose whether a term is mandatory or optional
            $scope.allowCategories = true; // allow the user to categorise terms as Name, Summary, etc
            $scope.allMandatory = false; // force all terms to be mandatory

            var capitalize = $filter("capitalize");
            var orderBy = $filter("orderBy");

            $scope.addVocabTerm = function (form) {
                if ($scope.newVocabTerm) {
                    if (termExists($scope.newVocabTerm)) {
                        messageService.alert("The specified term already exists. Terms must be unique across the attribute vocabulary.");
                    } else {
                        $scope.vocabulary.terms.push({termId: "",
                            name: capitalize($scope.newVocabTerm),
                            order: $scope.vocabulary.terms.length,
                            required: $scope.allMandatory ? true : false,
                            containsName: false,
                            summary: false
                        });
                        $scope.newVocabTerm = "";
                        sortVocabTerms();
                        form.$setDirty();
                    }
                }
            };

            $scope.removeVocabTerm = function (index, form) {
                var promise = profileService.findUsagesOfVocabTerm($scope.opusId, $scope.vocabId, $scope.vocabulary.terms[index].termId);
                promise.then(function (data) {
                        if (data.usageCount == 0) {
                            var deletedItemOrder = $scope.vocabulary.terms[index].order;
                            $scope.vocabulary.terms.splice(index, 1);

                            angular.forEach($scope.vocabulary.terms, function(term) {
                                if (term.order > deletedItemOrder) {
                                    term.order = term.order - 1;
                                }
                            });

                            sortVocabTerms();

                            form.$setDirty();
                        } else {
                            showRemoveTermPopup(data, index, form)
                        }
                    },
                    function() {
                        messageService.alert("An error occurred while checking if the term is in use.");
                    });
            };

            function showRemoveTermPopup(data, existingTermIndex, form) {
                var popup = $modal.open({
                    templateUrl: "/profileEditor/removeTermPopup.htm",
                    controller: "RemoveTermController",
                    controllerAs: "removeTermCtrl",
                    size: "md",
                    resolve: {
                        usageData: function() {
                            return data
                        },
                        existingTerm: function() {
                            return $scope.vocabulary.terms[existingTermIndex];
                        },
                        terms: function() {
                            var terms = angular.copy($scope.vocabulary.terms);
                            var deletedItemOrder = terms[existingTermIndex].order;
                            terms.splice(existingTermIndex, 1);

                            angular.forEach(terms, function(term) {
                                if (term.order > deletedItemOrder) {
                                    term.order = term.order - 1;
                                }
                            });
                            angular.forEach($scope.vocabulary.terms, function(term) {
                                if (term.order > deletedItemOrder) {
                                    term.order = term.order - 1;
                                }
                            });
                            return terms;
                        },
                        form: function() {
                            return form;
                        }
                    }
                });

                popup.result.then(function(data) {
                    $scope.replacements.push({vocabId: $scope.vocabId, existingTermId: data.existing.termId, newTermName: data.new.name});

                    form.$setDirty();
                });
            }

            $scope.termIsInReplacementList = function(term) {
                var match = false;
                angular.forEach($scope.replacements, function(item) {
                    if (item.existingTermId == term.termId) {
                        match = true;
                    }
                });
                return match
            };

            $scope.editVocabTerm = function(index, form) {
                var popup = $modal.open({
                    templateUrl: "/profileEditor/editTermPopup.htm",
                    controller: "VocabModalController",
                    controllerAs: "vocabModalCtrl",
                    size: "md",
                    resolve: {
                        term: function() {
                            return angular.copy($scope.vocabulary.terms[index]);
                        }
                    }
                });

                popup.result.then(function(updatedTerm) {
                    if (termExists(updatedTerm.name)) {
                        messageService.alert("The specified term already exists. Terms must be unique across the attribute vocabulary.");
                    } else {
                        $scope.vocabulary.terms[index] = updatedTerm;
                        form.$setDirty();
                    }
                });
            };

            function termExists(termToCheck) {
                var result = false;
                for (var i = $scope.vocabulary.terms.length - 1; i >= 0; i--) {
                    if ($scope.vocabulary.terms[i].name === capitalize(termToCheck)) {
                        result = true;
                        break;
                    }
                }
                return result;
            }

            $scope.saveVocabulary = function (form) {
                var promise = profileService.updateVocabulary($scope.opusId, $scope.vocabId, $scope.vocabulary);
                promise.then(function () {
                        if ($scope.replacements.length > 0) {
                            var promise = profileService.replaceUsagesOfVocabTerm($scope.opusId, $scope.vocabId, $scope.replacements);
                            promise.then(function() {
                                console.log("Replacements saved");

                                $scope.loadVocabulary(form);
                                messageService.success("Vocabulary successfully updated.");
                            })
                        } else {
                            $scope.loadVocabulary(form);
                            messageService.success("Vocabulary successfully updated.");
                        }
                    },
                    function () {
                        messageService.alert("An error occurred while updating the vocabulary.");
                    }
                );
            };

            $scope.loadVocabulary = function(form) {
                messageService.info("Loading vocabulary...");
                $scope.replacements = [];

                var promise = profileService.getOpusVocabulary($scope.opusId, $scope.vocabId);
                promise.then(function (data) {
                        $scope.vocabulary = data;

                        sortVocabTerms();

                        if (form) {
                            form.$setPristine()
                        }
                    },
                    function () {
                        messageService.alert("An error occurred while loading the vocabulary.");
                    }
                );
            };

            $scope.summaryChanged = function(selectedIndex) {
                $scope.vocabulary.terms.forEach(function (term, index) {
                    if (index != selectedIndex) {
                        term.summary = false;
                    }
                });
            };

            $scope.moveTermUp = function(index, form) {
                if (index > 0) {
                    $scope.vocabulary.terms[index].order = $scope.vocabulary.terms[index].order - 1;
                    $scope.vocabulary.terms[index - 1].order = $scope.vocabulary.terms[index - 1].order + 1;

                    sortVocabTerms();

                    form.$setDirty();
                }
            };

            $scope.moveTermDown = function(index, form) {
                if (index < $scope.vocabulary.terms.length) {
                    $scope.vocabulary.terms[index].order = $scope.vocabulary.terms[index].order + 1;
                    $scope.vocabulary.terms[index + 1].order = $scope.vocabulary.terms[index + 1].order - 1;

                    sortVocabTerms();

                    form.$setDirty();
                }
            };

            function sortVocabTerms() {
                if ($scope.allowReordering) {
                    $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "order");
                } else {
                    $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "name");
                }
                angular.forEach($scope.vocabulary.terms, function(term, index) {
                    term.order = index;
                });
            }

            $scope.sortAlphabetically = function(form) {
                $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "name");
                angular.forEach($scope.vocabulary.terms, function(term, index) {
                    term.order = index;
                });

                form.$setDirty();
            };
        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("vocabId", function(newValue) {
                if (newValue !== undefined) {
                    scope.loadVocabulary();
                }
            });
            scope.$watch("allowMandatory", function(newValue) {
                scope.allowMandatory = isTruthy(newValue);
            });
            scope.$watch("allowCategories", function(newValue) {
                scope.allowCategories = isTruthy(newValue);
            });
            scope.$watch("allowReordering", function(newValue) {
                scope.allowReordering = isTruthy(newValue);
            });

            function isTruthy(str) {
                return str == true || str === "true"
            }
        }
    }
});


/**
 * Edit Vocab Term modal dialog controller
 */
profileEditor.controller('VocabModalController', function ($modalInstance, term) {
    var self = this;

    self.term = term;

    self.ok = function() {
        if (term.name.length > 0) {
            $modalInstance.close(self.term);
        }
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});

/**
 * Replace Vocab Term modal dialog controller
 */
profileEditor.controller('RemoveTermController', function ($modalInstance, usageData, existingTerm, terms) {
    var self = this;

    self.terms = terms;
    self.usageCount = usageData.usageCount;
    self.existingTerm = existingTerm;
    self.newTerm = null;

    self.ok = function() {
        $modalInstance.close({existing: self.existingTerm, new: self.newTerm});
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});