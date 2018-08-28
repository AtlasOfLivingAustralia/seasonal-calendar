
profileEditor.controller('NavigationController', ['navService', '$scope', function (navService) {
    var self = this;
    self.navigateTo = function(key) {
        navService.navigateTo(key);
    };
}]);