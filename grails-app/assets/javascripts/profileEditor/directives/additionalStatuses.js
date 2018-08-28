(function() {
    "use strict";
    function AdditionalStatusesController($scope, profileService, messageService) {
        var self = this;

        // self.opus set by directive attribute

        function setStatuses() {
            var statuses;
            statuses = self.opus ? self.opus.additionalStatuses || [] : [];
            self.additionalStatuses = statuses.slice();
        }
        $scope.$watch(function() { return self.opus }, setStatuses);
        setStatuses();

        self.appendStatus = function() {
            self.additionalStatuses.push('');
            self.AdditionalStatusesForm.$setDirty();
        };

        self.changeStatus = function(index) {
            if (!self.additionalStatuses[index]) {
                removeStatus(index);
            }
        };

        self.deleteStatus = function(index) {
            removeStatus(index);
            self.AdditionalStatusesForm.$setDirty();
        };

        function removeStatus(index) {
            self.additionalStatuses.splice(index, 1);
        }

        self.saveAdditionalStatuses = function() {
            var statuses = self.additionalStatuses;
            profileService.updateAdditionalStatuses(self.opus.uuid, statuses).then(function() {
                self.opus.additionalStatuses = statuses;
                self.AdditionalStatusesForm.$setPristine();
                messageService.success("Additional Statuses updated", true);
            }, function() {
                messageService.alertStayOn("Failed to save additional statuses");
            });
        }
    }

    // TODO Angular 1.5 directive -> component, scope -> bindToController
    profileEditor.directive('additionalStatuses', function () {
        return {
            restrict: 'AE',
            scope: {
                opus: '='
            },
            controller: AdditionalStatusesController,
            controllerAs: '$ctrl',
            bindToController: true,
            templateUrl: '/profileEditor/additionalStatuses.htm'
        };
    });
})();