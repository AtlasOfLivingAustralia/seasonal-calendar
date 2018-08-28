/**
 * Directive to display tags/pills
 */
profileEditor.directive('tag', function ($browser) {
    return {
        restrict: 'E',
        templateUrl: '/profileEditor/tag.htm',
        replace: true,
        scope: {
            tag: "=",
            locked: "@",
            remove: '=',
            form: '='
        },
        link: function postLink(scope) {
            scope.$watch("tag", function(newVal) {
                if (!_.isUndefined(newVal)) {
                    // if the 'lightness' value of the HSL (Hue, Saturation, Lightness) data for the background colour
                    // is light, then set the text to black, otherwise use white. The 'lightness' value is a number
                    // between 0 (dark) and 1 (light).
                    var lightness = Color(newVal.colour || "#ffffff").hslData()[2];
                    if (lightness < 0.5) {
                        scope.foregroundColour = "white";
                    } else {
                        scope.foregroundColour = "black";
                    }

                    // if the background is really light, then add a grey border
                    if (lightness > 0.9) {
                        scope.borderColour = "#AAB3B9";
                    } else {
                        scope.borderColour = newVal.colour;
                    }
                }
            });
        }
    };
});