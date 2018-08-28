profileEditor.directive('keepalive', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        controller: ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
            var timeout = 1000 * 60 * 15;

            function ping() {
                $timeout(function () {
                    $http({
                        method: 'GET',
                        url: $browser.baseHref() + "ping"
                    }).then (function() {
                        ping();
                    });
                }, timeout);
            }

            ping();
        }]
    };
});