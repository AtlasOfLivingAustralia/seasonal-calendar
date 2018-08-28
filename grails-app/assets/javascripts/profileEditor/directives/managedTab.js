/**
 * The managedTab directive is designed to work with the ui-bootstrap-tpls tabset and tab directive.
 * It replaces the tabs contents with a ng-include which is activated via setting the src
 * when the tab "select" event is fired.
 */
profileEditor.directive('managedTab', ['$templateCache', 'navService', '$q', function ($templateCache, navService, $q) {

    var count = 0;
    var templateNamePrefix = "managed-tab-template-";

    var compile = function(tElement, tAttrs, transclude) {

        // Give each tabs contents a unique key in the template cache.
        count++;
        var templateName = templateNamePrefix+count;
        var tabName = tAttrs.heading || 'tab-'+count;

        // Bind to the tabs public API for detecting select events so we can use it to start our lazy load.
        // Save any existing callback to execute after we load our content.
        var existingOnSelectAttribute = tElement.attr("select");
        // Override the select attribute to call our method.
        tAttrs.select = "managedTabCtrl.doSelect()";
        tAttrs.active = "managedTabCtrl.tabActive";

        // Remove the original tab content from the DOM and save it as a template.  This template will be loaded
        // when the tab select event is fired.
        $templateCache.put(templateName, tElement.html());
        tElement.contents().remove();

        var placeholderElement = angular.element('<div ng-include src="managedTabCtrl.tabTemplate" onload="managedTabCtrl.finishedLoading()"></div>');
        var loadingElement = angular.element('<loading state="managedTabCtrl.loading"></loading>');
        tElement.append(placeholderElement);
        tElement.append(loadingElement);


        return function(scope, element, attrs) {
            scope.managedTabCtrl = {

                loading: true,
                name:tabName,
                initialised:false,
                initialise: function() {
                    this.defer = this.defer || $q.defer();
                    this.tabTemplate = templateName;
                    return this.defer.promise;
                },
                afterSelect: function() {
                    navService.tabSelected(tabName);
                    scope.$parent.$eval(existingOnSelectAttribute);
                },
                finishedLoading: function() {
                    this.loading = false;
                    this.initialised = true;

                    if (this.defer) {
                        this.defer.resolve();
                        this.defer = null;
                    }
                },
                doSelect: function() {
                    if (!this.initialised) {
                        this.initialise().then(this.afterSelect());
                    }
                    else {
                        this.afterSelect();
                    }
                },
                activateTab: function() {
                    this.defer = this.defer || $q.defer();

                    if (this.tabActive) {
                        this.defer.resolve();
                    }
                    this.tabActive = true;
                    return this.defer.promise;
                },
                backgroundInitialise: function() {
                    // This is to allow tabs to defer initialisation until they are selected. (e.g the key player
                    // needs to be visible before it loads content).
                    if (!attrs['selectToInitialise']) {
                        this.initialise();
                    }
                },
                hasContent: function() {
                    return navService.hasContent(tabName);
                }
            };

            navService.registerTab(tabName,
                function() {return scope.managedTabCtrl.activateTab.apply(scope.managedTabCtrl)},
                function() {scope.managedTabCtrl.backgroundInitialise.apply(scope.managedTabCtrl)});
        };
    };

    return {
        restrict: 'A',
        require: 'tab',
        scope:true,
        compile: compile
    }
}]);