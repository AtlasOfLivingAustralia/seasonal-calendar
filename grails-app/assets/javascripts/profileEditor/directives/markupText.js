/**
 * This directive will take a block of text that may contain html and:
 *
 * 1) look for any img tag and wrap add an ng-click handler to display the image in the image viewer (assumes the images
 * have a url containing the image id in the format http://.../.../image/[imageid].[ext]&...)
 * 2) compile the modified html to allow angularjs directives (e.g. ng-click) to work
 *
 * Usage: <div markup-text="[expression]"></div>
 */
profileEditor.directive('markupText', function ($compile, util) {
    // matches elements like <img src=\"http://profiles.ala.org.au/opus/8aa27a28-e38f-4048-8349-9102e5164948/profile/b484380d-4d04-4fcc-bb71-c38f33829ead/image/fa09b7f4-4211-413a-a063-07c7ac6468ae.jpg?type&#61;PRIVATE\" />
    var localImageRegex = new RegExp("<img(.*?)src=(.*?)/image/thumbnail/(" + util.UUID_REGEX_PATTERN + ")(\.[a-z]{3,4}[\?\"'])(.*?)>", "gi");

    // matches elements like <img src="http://images.ala.org.au/image/proxyImageThumbnail?imageId&#61;8ee9969f-3122-43f1-a624-dc423e9b94bb" />
    var remoteImageRegex1 = new RegExp("<img(.*?)src=(.*?)\?imageId(=|(&#61;))(" + util.UUID_REGEX_PATTERN + ")(.*?)>", "gi");
    // http://images.ala.org.au/store/7/b/a/e/0b59036d-f3e9-4c79-bf03-2bee5210eab7/thumbnail
    var remoteImageRegex2 = new RegExp("<img(.*?)src=(.*?)/(" + util.UUID_REGEX_PATTERN + ")/thumbnail(.*?)>", "gi");

    var localImageReplacementPattern = "<a href=\"\" ng-click=\"imageCtrl.showMetadata(\'$3\', true)\" title=\"View details\"><img$1src=$2/image/thumbnail/$3$4$5></a>";
    var remoteImageReplacementPattern1 = "<a href=\"\" ng-click=\"imageCtrl.showMetadata(\'$5\', false)\" title=\"View details\"><img$1src=$2imageId$3$5$6></a>";
    var remoteImageReplacementPattern2 = "<a href=\"\" ng-click=\"imageCtrl.showMetadata(\'$3\', false)\" title=\"View details\"><img$1src=$2/$3/thumbnail$4></a>";

    return function (scope, element, attrs) {
        scope.$watch(attrs.markupText, function (newValue) {
                if (newValue && angular.isDefined(newValue) && newValue.indexOf) {
                    var markedUp = wrapImagesWithLink(newValue);

                    element.html(markedUp);
                    $compile(element.contents())(scope);
                }
            }
        );
    };

    function wrapImagesWithLink(html) {
        if (html.match(localImageRegex)) {
            html = html.replace(localImageRegex, localImageReplacementPattern);
        }
        
        if (html.match(remoteImageRegex1)) {
            html = html.replace(remoteImageRegex1, remoteImageReplacementPattern1);
        }

        if (html.match(remoteImageRegex2)) {
            html = html.replace(remoteImageRegex2, remoteImageReplacementPattern2);
        }

        return html;
    }
});