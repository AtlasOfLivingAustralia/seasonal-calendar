profileEditor.directive('autoFocus', function ($timeout) {
    return function (scope, element, attrs) {
        scope.$watch(attrs.autoFocus, function (newValue) {
                $timeout(function () {
                    newValue && element[0].focus();
                });
            }, true);
    };
});