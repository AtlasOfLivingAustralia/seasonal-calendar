/**
 * Trigger a function call when the enter key is pressed
 */
profileEditor.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

/**
 * Display a loading spinner
 */
profileEditor.directive('loading', function () {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            state: '='
        },
        template: '<div ng-if="state"><span class="fa fa-spin fa-spinner"></span>&nbsp;Loading...</div>'
    }
});

/**
 * Display a span with an icon and optional text to indicate some sort of status
 */
profileEditor.directive('statusIndicator', function () {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            iconClass: '@',
            text: '@',
            title: '@'
        },
        template: '<span class="fa {{iconClass}} {{colorClass}} padding-left-1" title="{{title}}"><span class="status-marker-small" title="{{title}}">{{text}}</span></span>'
    }
});

/**
 * Fires an event when the user clicks anywhere except the element containing this directive
 */
profileEditor.directive('clickOff', function ($parse, $document) {
    return {
        compile: function ($element, attr) {
            // Parse the expression to be executed whenever someone clicks _off_ this element.
            var fn = $parse(attr["clickOff"]);
            return function (scope, element, attr) {
                // add a click handler to the element that stops the event propagation.
                element.bind("click", function (event) {
                    event.stopPropagation();
                });
                angular.element($document[0].body).bind("click", function (event) {
                    scope.$apply(function () {
                        fn(scope, {$event: event});
                    });
                });
            };
        }
    };
});