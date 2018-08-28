/**
 * Standardised dirty checking for a profile.
 */
profileEditor.directive('dirtyCheck', function ($browser) {
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {
            if (scope.changed) {
                elem.addClass("ng-dirty");
            }
        },
        scope: {
            changed: "="
        }
    }
});