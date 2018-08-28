profileEditor.directive('profileComparison', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            left: '=',
            right: '=',
            leftTitle: '@',
            rightTitle: '@',
            showEverything: '@'
        },
        templateUrl: '/profileEditor/profileComparison.htm',
        controller: ['$scope', '$filter', 'config', 'profileComparisonService', function ($scope, $filter, config, profileComparisonService) {

            $scope.compareProfiles = function() {
                $scope.diff = profileComparisonService.compareProfiles($scope.left, $scope.right);
            };
            $scope.showEverything = false;
            $scope.getImageUrl = profileComparisonService.getImageUrl;

            $scope.compareProfiles();
        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("left", function(newValue) {
                if (newValue !== undefined) {
                    scope.left = newValue;
                    scope.compareProfiles();
                }
            });
            scope.$watch("right", function(newValue) {
                if (newValue !== undefined) {
                    scope.right = newValue;
                    scope.compareProfiles();
                }
            });
            scope.$watch("showEverything", function(newValue) {
                scope.showEverything = isTruthy(newValue);
            });
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }
});