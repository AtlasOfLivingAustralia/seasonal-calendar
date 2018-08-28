(function() {
    "use strict";
    function ProfileStatusController($scope, profileService, messageService) {
        var self = this;

        // self.opus set by directive attribute
        // self.profile set by directive attribute
        // self.readonly set by directive attribute

        // TODO i18n
        var baseStatuses = [
            'Partial',
            'Legacy'
        ];

        function setStatuses() {
            var statuses;
            statuses = self.opus ? self.opus.additionalStatuses || [] : [];
            self.statuses = baseStatuses.concat(statuses);
        }
        $scope.$watch(function() { return self.opus }, setStatuses);
        setStatuses();

        self.isReadOnly = self.readonly();
        self.isEdittable = !self.isReadOnly;


        self.saveStatus = function() {
            // var status = self.status;
            profileService.setStatus(self.profile, self.profile.profileStatus).then(function() {
                //self.profile.profileStatus = status;
                messageService.success("Status updated", true);
                self.StatusForm.$setPristine();
            }, function() {
                messageService.alertStayOn("Failed to save status");
            });
        }
    }

    // TODO Angular 1.5 directive -> component, scope -> bindToController
    profileEditor.directive('profileStatus', function () {
        return {
            restrict: 'AE',
            scope: {
                opus: '=',
                profile: '=',
                readonly: '='
            },
            controller: ProfileStatusController,
            controllerAs: '$ctrl',
            bindToController: true,
            templateUrl: '/profileEditor/profileStatus.htm'
        };
    });
})();