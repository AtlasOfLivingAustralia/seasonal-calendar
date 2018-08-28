/**
 * Directive to display a 'save all' button on the page which, when clicked, will find all buttons with the 'save-button'
 * class (see saveButton.js for a directive to create standard save buttons) in 'dirty' forms and trigger the ng-click
 * event handler.
 */
profileEditor.directive('saveAll', function ($browser, $timeout) {
    var dirtySaveButtonSelector = "button.save-button.dirty-form";

    return {
        restrict: 'E',
        require: [],
        templateUrl: '/profileEditor/saveAllButton.htm',
        controllerAs: "saveAllCtrl",
        controller: ['$scope', '$timeout', function($scope, $timeout) {
            var self = this;
            $scope.saveAllCtrl.dirtySaveButtons = false;

            self.saveAll = function() {
                if ($scope.saveAllCtrl.dirtySaveButtons) {
                    var buttons = $(dirtySaveButtonSelector);

                    $timeout(function() {
                        angular.forEach(buttons, function(button) {
                            angular.element($(button)).triggerHandler("click");
                        });
                    });
                }
            };

            self.watchForms = function() {
                $scope.$apply();

                $scope.$watch(function() {
                    return $(dirtySaveButtonSelector).length > 0;
                }, function(newVal) {
                    self.dirtySaveButtons = newVal;
                });
            }
        }],
        link: function postLink(scope) {
            // only start watching for dirty forms after the initial page load has finished so we don't get a race
            // condition with certain fields (mainly the CKEditor fields) that set the $dirty flag during page load.
            // No change would be made within 10 seconds of loading the page.
            $timeout(scope.saveAllCtrl.watchForms, 10000);
        }

    };
});