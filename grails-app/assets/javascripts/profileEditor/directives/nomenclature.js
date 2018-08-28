profileEditor.directive('nomenclature', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            nslNameId: '=',
            nslNomenclatureId: '=',
            readonly: '@'
        },
        templateUrl: '/profileEditor/nomenclature.htm',
        controller: ['$scope', '$http', 'profileService', function ($scope, $http, profileService) {
            $scope.selectedReference = null;
            $scope.nslNomenclatureId = null;
            $scope.loading = false;
            $scope.viewInNslLink = null;
            var IGNORE_STATUSES = ["legitimate", "[n/a]"];

            $scope.loadConcepts = function () {
                $scope.loading = true;
                if (!$scope.references && $scope.nslNameId !== null) {
                    profileService.getNomenclatureList($scope.nslNameId).
                        then(function (resp) {
                            $scope.references = resp;

                            $scope.selectedReference = _.find($scope.references, function (ref) { return ref.instanceId == $scope.nslNomenclatureId });

                            $scope.loading = false;
                        }
                    );
                }
            };

            $scope.toggleSynonomy = function() {
                $scope.showSynonomy = !$scope.showSynonomy;
            };
        }
        ],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("nslNameId", function (newValue) {
                if (newValue !== undefined) {
                    scope.nslNameId = newValue;
                    scope.loadConcepts();
                }
            });
            scope.$watch("nslNomenclatureId", function (newValue) {
                if (newValue !== undefined) {
                    scope.nslNomenclatureId = newValue;

                    if (!scope.selectedReference && scope.nslNameId) {
                        scope.loadConcepts();
                    }
                }
            });
            scope.$watch("selectedReference", function (newValue) {
                if (newValue !== undefined) {
                    scope.selectedReference = newValue;
                    scope.nslNomenclatureId = newValue == null ? null : newValue.instanceId;
                }
            }, true);
            scope.$watch("readonly", function (newValue) {
                scope.readonly = isTruthy(newValue);
            });
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }

});