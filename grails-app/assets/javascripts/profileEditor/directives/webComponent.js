/**
 * Directive for embedding content from external (CORS-enabled) sites into the application view using Web Components.
 *
 * This directive allows you to :
 * - select which element(s) of the source html to include;
 * - exclude specific elements (optional)
 * - invoke the onloadCallback function (if provided) after the component has loaded
 *
 * NOTE: To use this you MUST include the Web Components polyfill from https://github.com/webcomponents/webcomponentsjs
 * to ensure cross browser support.
 */
profileEditor.directive('webComponent', function () {
    return {
        restrict: 'E',
        require: [],
        scope: {
            url: "@",
            contentSelectors: "@",
            excludeSelectors: "@",
            onloadCallback: "="
        },
        link: function (scope, element) {
            var div = document.createElement("div");
            div.setAttribute("class", "launcher-panel");
            div.classList.add("hidden");

            element.append(div);

            var link = document.createElement("link");

            link.setAttribute("rel", "import");
            link.setAttribute("href", scope.url);

            link.onload = function () {
                if (angular.isDefined(scope.excludeSelectors)) {
                    var excludes = scope.excludeSelectors.split(",");
                    angular.forEach(excludes, function (exclude) {
                        var elements = link.import.querySelectorAll(exclude.trim());
                        angular.forEach(elements, function (element) {
                            element.remove();
                        });
                    });
                }

                var selectors = scope.contentSelectors.split(",");
                angular.forEach(selectors, function (selector) {
                    element.append(link.import.querySelector(selector.trim()));
                });

                if (angular.isDefined(scope.onloadCallback)) {
                    scope.onloadCallback();
                }
            };

            div.appendChild(link);
        }
    };
});