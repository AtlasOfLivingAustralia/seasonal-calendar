profileEditor.directive('keyPlayer', function ($browser) {
    return {
        restrict: 'E',
        require: ['?keyId', '?profileUrl', '?keybaseUrl'],
        scope: {
            keyId: '=',
            taxonName: '=',
            opusId: '=',
            onlyIncludeItems: '=?',
            profileUrl: '@',
            keybaseUrl: '@',
            keyLookupUrl: '@'
        },
        templateUrl: '/profileEditor/keyplayer.htm',
        controllerAs: "keyplayer",
        controller: ['$scope', '$http', '$compile', '$window', function ($scope, $http, $compile, $window) {
            var self = this;
            self.format = 'player';

            self.keyId = $scope.keyId;
            self.currentKeyId = $scope.keyId;

            $scope.contextPath = $browser.baseHref();

            self.initialised = false;
            self.bracketedInitialised = false;
            self.indentedInitialised = false;

            self.onlyIncludeItems = null;

            self.loadKey = function (keyId) {
                self.error = null;
                self.hasKey = keyId != null;

                if (keyId) {
                    var settings = {
                        ajaxDataType: "jsonp",
                        baseUrl: $scope.keybaseUrl,
                        key: keyId,
                        title: false,
                        source: true,
                        reset: true,
                        onLoad: keybaseOnLoad,
                        resultDisplay: resultDisplay
                    };

                    // Disable this feature to remove filter for keys https://github.com/AtlasOfLivingAustralia/profile-hub/issues/514
                 /*   if (self.onlyIncludeItems) {
                        settings.filterItemNames = self.onlyIncludeItems.names;
                        settings.filterItemGuids = self.onlyIncludeItems.guids;
                    }
                */
                    var action;
                    var initialised = false;
                    if (self.format == 'bracketed') {
                        action = 'bracketedKey';
                        settings.bracketedKeyDiv = '.keybase-key-bracketed';
                        settings.bracketedKeyDisplay = bracketedKeyDisplay;
                        initialised = self.bracketedInitialised;
                    } else if (self.format == 'indented') {
                        action = 'indentedKey';
                        settings.indentedKeyDiv = '.keybase-key-indented';
                        settings.indentedKeyDisplay = indentedKeyDisplay;
                        initialised = self.indentedInitialised;
                    } else {
                        action = 'player';
                        settings.playerDiv = ".keybase-key-player";
                        settings.titleDiv = ".keybase-key-title";
                        settings.sourceDiv = ".keybase-key-source";
                        settings.discardedItemsDisplay = buildInteractiveListItems;
                        settings.remainingItemsDisplay = buildInteractiveListItems;
                        initialised = self.initialised;
                    }

                    if (!initialised || self.currentKeyId != keyId) {
                        self.loading = true;
                        $.fn.keybase(action, settings);
                        if (self.format == 'bracketed') {
                            self.bracketedInitialised = true;
                        } else if (self.format == 'indented') {
                            self.indentedInitialised = true;
                        } else {
                            self.initialised = true;
                        }
                    }
                }

                self.currentKeyId = keyId;
            };

            self.setFormat = function (format) {
                self.error = null;
                self.format = format;
                self.loadKey(self.currentKeyId);
            };

            self.viewItem = function (name) {
                self.error = null;

                $http.get($scope.profileUrl + "/" + name).success(function () {
                    $window.location = $scope.profileUrl + "/" + name;
                }).error(function (responseData, statusCode) {
                    if (statusCode == 404) {
                        self.error = " A profile page does not exist for " + name;
                    }
                });
            };

            self.loadKeyFromName = function (taxonName) {
                self.taxonName = taxonName;
                self.loading = true;
                $http.get($scope.keyLookupUrl + "?opusId=" + $scope.opusId + "&scientificName=" + taxonName, {cache: true})
                    .success(function (data) {
                            self.keyId = data.keyId;
                            if (data.keyId) {
                                self.loadKey(data.keyId);
                                self.hasKey = true;
                            } else {
                                console.log("No key found for " + taxonName + " in opus " + $scope.opusId);
                                self.hasKey = false;
                            }
                            self.loading = false;
                        }
                    ).error(function () {
                        console.log("Failed to load key for taxon " + taxonName);
                        self.error = "Unable to connect to KeyBase.";
                        self.hasKey = false;
                        self.loading = false;
                    }
                );
            };

            function resultDisplay(result, resultDiv) {
                var text = '<a href="" ng-click="keyplayer.viewItem(\'' + result[0].item_name + '\')">' + result[0].item_name + '</a>'

                $(resultDiv).eq(0).children('div').eq(0).html($compile('<div class="keybase-player-result">Result: <b>' + text + '</b></div>')($scope));
            }

            function keybaseOnLoad(json) {
                self.projectName = json.project.project_name;
                self.projectIcon = $scope.contextPath + "assets/keybase-logo-blue-80.png";
                if ((/\.(gif|jpg|jpeg|tiff|png)$/i).test(json.project.project_icon)) {
                    self.projectIcon = json.project.project_icon;
                }

                self.keyName = json.key_name;

                self.loading = false;
                self.initialised = true;

                // the keyplayer plugin changes the DOM outside of Angular's digest cycle,
                // so make sure all bindings are applied
                $scope.$apply();
            }

            function buildListItem(item) {
                var entity = '<a href="" ng-click="keyplayer.viewItem(\'' + item.item_name + '\')">' + item.item_name + '</a>';

                if (item.to_key) {
                    entity += '<a href="" ng-click="keyplayer.loadKey(\'' + item.to_key + '\')" title="Load key for ' + item.item_name + '"><span class="keybase-player-tokey"></span></a>';
                }

                if (item.link_to_item_name) {
                    entity += ': ';
                    entity += '<a href="" ng-click="keyplayer.viewItem(\'' + item.link_to_item_name + '\')">' + item.link_to_item_name + '</a>';

                    if (item.link_to_key) {
                        entity += '<a href="" ng-click="keyplayer.loadKey(\'' + item.link_to_key + '\')" title="Load key for ' + item.link_to_item_name + '"><span class="keybase-player-tokey"></span></a>';
                    }
                }

                return entity;
            }

            function buildInteractiveListItems(items, itemsDiv) {
                var list = [];
                $.each(items, function (index, item) {
                    var entity = '<li>' + buildListItem(item) + '</li>';
                    list.push(entity);
                });

                $(itemsDiv).eq(0).find('.keybase-num-remaining').eq(0).html(items.length);

                $(itemsDiv).eq(0).children('div').eq(0).html($compile('<ul>' + list.join('') + '</ul>')($scope));
            }

            function bracketedKeyDisplay(json) {
                var bracketed_key = $.fn.keybase.getters.bracketedKey();
                var settings = $.fn.keybase.getters.settings();
                var html = '<div class="keybase-bracketed-key">';
                var couplets = bracketed_key[0].children;
                for (var i = 0; i < couplets.length; i++) {
                    var couplet = couplets[i];
                    var leads = couplet.children;
                    html += '<div class="keybase-couplet" id="l_' + leads[0].parent_id + '"><a name="l_' + leads[0].parent_id + '"></a>';
                    for (var j = 0; j < leads.length; j++) {
                        var lead = leads[j];
                        var items = lead.children;
                        html += '<div class="keybase-lead">';
                        html += '<span class="keybase-from-node">' + lead.fromNode + '</span>';
                        html += '<span class="keybase-lead-text">' + lead.title;
                        if (lead.toNode !== undefined) {
                            html += '<span class="keybase-to-node"><a du-smooth-scroll="l_' + lead.lead_id + '" target="_self">' + lead.toNode + '</a></span>';
                        }
                        else {
                            var toItem = items[0].children[0];
                            var item = JSPath.apply('.items{.item_id==' + toItem.item_id + '}', json)[0];
                            html += '<span class="keybase-to-item">';
                            html += buildListItem(item);
                            html += '</span>';
                        }
                        html += '</span>';
                        html += '</div>';
                    }
                    html += '</div>';

                }
                html += '</div>';
                $(settings.bracketedKeyDiv).html($compile(html)($scope));
                $(settings.bracketedKeyDiv).prepend($compile('<div class="keybase-bracketed-key-filter"><span class="keybase-player-filter"><a href="#"><i class="fa fa-filter fa-lg"></i></a></span></div>')($scope));

                var contentHeight = $(settings.bracketedKeyDiv).offset().top + $(settings.bracketedKeyDiv).height();
                if (contentHeight > $(window).height() - 60) {
                    $('body').css('height', contentHeight);
                }
            }

            function indentedKeyDisplay(json) {
                var indentedKeyHtml = '<div class="keybase-indented-key">';
                indentedKeyHtml += displayIndentedKeyCouplet($.fn.keybase.getters.indentedKey()[0].children[0]);

                indentedKeyHtml += '</div>';

                $($.fn.keybase.getters.settings().indentedKeyDiv).html($compile(indentedKeyHtml)($scope));
                $($.fn.keybase.getters.settings().indentedKeyDiv).prepend('<div class="keybase-indented-key-filter"><span class="keybase-player-filter"><a href="#"><i class="fa fa-filter fa-lg"></i></a></span></div>');

                $.fn.keybase.getters.settings().onIndentedKeyComplete();
            }

            function displayIndentedKeyCouplet(couplet) {
                var leads = couplet.children;
                var indentedKeyHtml = '<ul class="keybase-couplet">';
                for (var i = 0; i < leads.length; i++) {
                    var lead = leads[i];
                    indentedKeyHtml += '<li>';
                    indentedKeyHtml += '<div class="keybase-lead">';
                    indentedKeyHtml += '<span class="keybase-from-node">' + lead.fromNode + '</span>';
                    indentedKeyHtml += '<span class="keybase-lead-text">' + lead.title + '</span>';

                    var child = lead.children[0];
                    if (child.title === "Couplet") {
                        indentedKeyHtml += '</div>';
                        indentedKeyHtml += displayIndentedKeyCouplet(child);
                    } else {
                        var item = JSPath.apply('.items{.item_id==' + child.children[0].item_id + '}', $.fn.keybase.getters.jsonKey())[0];
                        indentedKeyHtml += '<span class="keybase-to-item">';

                        indentedKeyHtml += buildListItem(item);

                        indentedKeyHtml += '</span>';
                        indentedKeyHtml += '</div>';
                    }
                    indentedKeyHtml += '</li>';
                }

                indentedKeyHtml += '</ul>';

                return indentedKeyHtml;
            }

            self.reload = function() {
                if (self.keyId) {
                    self.loadKey(self.keyId);
                } else if (self.taxonName) {
                    self.loadKeyFromName(self.taxonName);
                }
            }

        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("keyId", function (keyId) {
                if (!_.isUndefined(keyId)) {
                    scope.keyplayer.keyId = keyId;
                    scope.keyplayer.loadKey(scope.keyplayer.keyId);
                }
            });

            scope.$watch("taxonName", function (taxonName) {
                if (!_.isUndefined(taxonName)) {
                    scope.keyplayer.taxonName = taxonName;
                    scope.keyplayer.loadKeyFromName(scope.keyplayer.taxonName);
                }
            });

            if (scope.onlyIncludeItems) scope.keyplayer.onlyIncludeItems = scope.onlyIncludeItems;
            scope.$watch("onlyIncludeItems", function(onlyIncludeNames, oldOnlyIncludeNames) {
                if (!_.isEqual(onlyIncludeNames, oldOnlyIncludeNames)) {
                  scope.keyplayer.onlyIncludeItems = onlyIncludeNames;
                  //scope.keyplayer.reload();
                }
            })
        }
    };
});