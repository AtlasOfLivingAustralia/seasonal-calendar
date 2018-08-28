/**
 * Angular service for recording navigation items across disparate controllers and tabs.
 * It works with the managedTab & navigationAnchor directives to support cross tab navigation via a table of contents.
 */
profileEditor.factory('navService', ['$filter', '$rootScope', '$document', function ($filter, $rootScope, $document) {

    $rootScope.nav = [];
    var orderBy = $filter("orderBy");
    var tabs = {};

    /**
     * Registers a tab so that the navService can initialise and select it
     * @param name the tab's (unique name)
     * @param selectionCallback a function that will cause the tab to be selected when invoked.
     * @param initCallback a function that will initialise the tab (without selecting it) when invoked.
     */
    function registerTab(name, selectionCallback, initCallback) {
        tabs[name] = {
            select:selectionCallback,
            init:initCallback
        };
    }

    /**
     * Tells each registered tab to initialise it's contents in the background.
     */
    function initialiseTabs() {
        for (var tab in tabs) {
            tabs[tab].init();
        }
    }

    /**
     * Returns true if at least one navigation anchor has been registered with this service that is on the
     * tab of interest.
     * @param tabName the tab to check
     * @returns {boolean}
     */
    function hasContent(tabName) {
        var hasContent = false;
        angular.forEach($rootScope.nav, function(item) {
            if (item.tab === tabName) {
                hasContent = true;
            }
        });
        return hasContent;
    }

    /**
     * Returns the navigation item identified by the supplied key.
     * @param key
     * @returns {*}
     */
    function getItem(key) {
        var matchingItem = null;
        angular.forEach($rootScope.nav, function(item) {
            if (item.key === key) {
                matchingItem = item;
            }
        });
        return matchingItem;
    }

    /**
     * Registers a navigation point with this service.
     * @param label Human viewable label for the anchor, used in the table of contents.
     * @param key a unique key for the anchor, must match the anchor name.
     * @param category
     * @param tab the tab the anchor is displayed on.
     * @param onDisplay a callback to be invoked the first time the navigation item is visible.  Used to
     * defer initialisation of contents on tab that need to be visible / have a sensible size when initialised.
     * (e.g maps and the key player)
     */
    function add(label, key, anchor, category, tab, onDisplay) {

        if (!getItem(key)) {
            var item = {label: label, key: key, anchor: anchor, category: category, tab:tab};

            $rootScope.nav.push(item);
            $rootScope.nav = orderBy($rootScope.nav, 'label');

            // If the item is not on a tab, it is already displayed.  Otherwise save the callback until the tab is
            // displayed.
            if (onDisplay && !tab) {
                onDisplay();
            }
            else {
                item.onDisplay = onDisplay;
            }

        }
    }

    /**
     * Deregisters an item from display.
     * @param key identifies which item to remove/deregister.
     */
    function remove(key) {
        var index = -1;
        angular.forEach($rootScope.nav, function(item, idx) {
            if (item.key === key) {
                index = idx;
            }
        });

        if (index > -1) {
            $rootScope.nav.splice(index, 1);
        }
    }

    /**
     * Programatically navigates to the anchor identified by the key paramter.  First selects the correct tab, then
     * scrolls to the anchor.
     * @param key
     */
    function navigateTo(key) {
        var item = getItem(key);
        if (item) {

            if (item.tab && tabs[item.tab]) {
                tabs[item.tab].select().then(function() {
                    scrollTo(item.anchor);
                });
            }
            else {
                scrollTo(item.anchor);
            }
        }
    }

    function scrollTo(anchor) {
        var target = document.getElementById(anchor) || document.getElementsByName(anchor)[0];
        if(target && target.getBoundingClientRect) {
            setTimeout(function() {
                var offset = 30;
                var duration = 700;
                $document.scrollToElement(target, offset, duration);
            }, 0);
        }
    }

    /**
     * Callback from a managedTab to let the navService know it has been selected.  This is to allow any
     *  initialisation code to be run that requires the component to be visible / have a size.
     * @param tabName the name of the tab that was selected.
     */
    function tabSelected(tabName) {
        angular.forEach($rootScope.nav, function(item) {
            if (item.tab == tabName && item.onDisplay) {
                item.onDisplay();
                delete item.onDisplay;
            }
        });
    }

    /**
     * Public API
     */
    return {
        add: add,
        remove: remove,
        registerTab: registerTab,
        initialiseTabs:initialiseTabs,
        navigateTo: navigateTo,
        hasContent: hasContent,
        tabSelected: tabSelected
    }
}]);