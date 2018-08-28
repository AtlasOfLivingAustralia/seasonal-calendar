/** Reacts to an onerror event from an image to display a supplied backup image */
profileEditor.directive('missingImage', function ($browser) {
    var missingImage = {
        link: function postLink(scope, iElement, iAttrs) {
            iElement.bind('error', function() {
                angular.element(this).attr("src", $browser.baseHref()+iAttrs.missingImage);
            });
        }
    };
    return missingImage;
});