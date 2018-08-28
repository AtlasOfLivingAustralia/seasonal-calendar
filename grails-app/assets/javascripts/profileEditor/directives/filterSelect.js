(function() {
    "use strict";
    function FilterSelectController($timeout, profileService, messageService, filterConfig) {
        var self = this;

        self.listItems = angular.copy(self.lists == null ? filterConfig.lists : self.lists);
        self.noSelection = { dataResourceUid: '', listName: '' };
        // self.listItems.unshift(self.noSelection);

        if (self.value == null) {
            self.value =  filterConfig.value;
        }

        if (self.value == null) {
            self.value = '';
        }

        function setSelected(drUid) {
            return _.find(self.listItems, function(value) { return value.dataResourceUid === drUid });
        }

        self.selected = setSelected(self.value);

        self.onFocus = function (e) {
            $timeout(function () {
                $(e.target).trigger('input');
            });
        };

        function update(uid) {
            profileService.updateFlorulaList(filterConfig.opusId, uid).then(
                function(data) {
                    messageService.success("Filter updated");
                },
                function(error) {
                    messageService.alert("Filter update failed");
                }
            );
        }

        self.save = function() {
            update(self.selected.dataResourceUid);
        };
        self.clear = function() {
            self.selected = self.noSelection;
            update('');
        };
    }

    // TODO Angular 1.5 directive -> component, scope -> bindToController
    profileEditor.directive('filterSelect', function () {
        return {
            restrict: 'AE',
            scope: {
                lists: '=?', // optional params so they can also be provided via filterConfig
                value: '=?'
            },
            controller: FilterSelectController,
            controllerAs: '$ctrl',
            bindToController: true,
            templateUrl: '/profileEditor/filterSelect.htm'
        };
    });
})();