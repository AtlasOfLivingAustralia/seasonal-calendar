/**
 * Standard format for a button to save a form/subform. Using this directive will ensure that the button is structured
 * consistently, has consistent behaviour around disabling and marking dirty changes etc, and that it will work with the
 * saveAllButton directive.
 */
profileEditor.directive('saveButton', function ($browser) {
    return {
        restrict: 'E',
        templateUrl: '/profileEditor/saveButton.htm',
        replace: true,
        scope: {
            disabled: "=",
            form: "=?",
            dirty: "=",
            show: "=ngShow",
            hide: "=ngHide",
            btnClass: "@",
            label: "@"
        },
        controller: function ($scope) {
            $scope.isDisabled = function () {
                return (angular.isDefined($scope.disabled) && $scope.disabled)
                    || !$scope.isDirty() || $scope.isInvalid();
            };

            $scope.isInvalid = function () {
                return $scope.form && $scope.form.$invalid;
            };

            $scope.isDirty = function () {
                return ($scope.form && $scope.form.$dirty)
                    || (angular.isDefined($scope.dirty) && $scope.dirty);
            };
        }
    };
});