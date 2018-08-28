/**
 * Directive to display the floating side bar menu on the profile view/edit page
 */
profileEditor.directive('profileSideBar', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        templateUrl: '/profileEditor/profileSideBar.htm',
        controller: [function () {
            var self = this;

            self.contextPath = $browser.baseHref();
            self.templateId = null;
            self.url = null;

            self.open = function(sidebarTemplate) {
                self.templateId = sidebarTemplate;
                self.url = "/profileEditor/" + sidebarTemplate + ".htm";
                $('#sideBarContents').toggleClass("sidebar-open");
            };

            self.close = function() {
                $(".sidebar").removeClass("sidebar-open");
            };

            self.getUrl = function() {
                return self.url;
            }
        }],
        controllerAs: "sidebar",
        link: function (scope, element, attrs, ctrl) {
        }
    };
});

