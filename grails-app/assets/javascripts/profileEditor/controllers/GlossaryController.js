/**
 * Glossary controller
 */
profileEditor.controller('GlossaryController', function (profileService, util, config, messageService, $filter, $location, $anchorScroll, $modal) {
    var self = this;

    self.glossary = {items: []};
    self.opusId = util.getEntityId("opus");
    self.opus = null;
    self.newItem = null;
    self.newFile = null;

    self.initialisePrefix = function () {
        self.prefix = util.getQueryParameter("page");

        if (!self.prefix) {
            self.prefix = "a";
        }
        self.page = self.prefix.charAt(0);
    };

    self.initialisePrefix();


    var orderBy = $filter("orderBy");

    self.upload = function () {
        if (self.newFile) {
            var confirmed = util.confirm("Are you sure you wish to replace your existing glossary with the contents of this file?");

            confirmed.then(function () {
                var formData = new FormData();
                formData.append("file", self.newFile[0]);

                var promise = profileService.uploadGlossary(self.opusId, formData);
                messageService.info("Uploading glossary...");
                promise.then(function () {
                    self.newFile = null;
                    messageService.success("Your new glossary has been successfully uploaded.")
                }, function () {
                    messageService.alert("An error occurred while uploading the glossary.");
                });
            });
        }
    };

    self.deleteGlossaryItem = function (index) {
        var confirmed = util.confirm("Are you sure you wish to delete this item?");

        confirmed.then(function () {
            var item = self.glossary.items[index];

            var promise = profileService.deleteGlossaryItem(self.opusId, item.uuid);
            promise.then(function () {
                    self.glossary.items.splice(index, 1);
                    messageService.success("Item successfully deleted.")
                },
                function () {
                    messageService.alert("An error occurred while deleting the glossary item.");
                }
            );
        })
    };

    self.loadGlossary = function (prefix) {
        if (!self.opusId) {
            return;
        }

		var opusPromise = profileService.getOpus(self.opusId);
		opusPromise.then(function (data) {
			self.opus = data;
		});

        if (prefix) {
            self.prefix = prefix;
            self.page = self.prefix.charAt(0);
        }

        var promise = profileService.getGlossary(self.opusId, self.prefix);
        messageService.info("Loading glossary...");
        promise.then(function (data) {
                console.log(data.items.length + " glossary items retrieved");

                self.glossary = data;

                self.glossary.items = orderBy(self.glossary.items, "term");

                $location.search('page', self.prefix);
            },
            function () {
                messageService.alert("An error occurred while retrieving the glossary.")
            }
        );
    };

    self.editGlossaryItem = function (index) {
        var popup = $modal.open({
            templateUrl: "/profileEditor/editItemPopup.htm",
            controller: "GlossaryModalController",
            controllerAs: "glossaryModalCtrl",
            size: "md",
            resolve: {
                item: function () {
                    return angular.copy(self.glossary.items[index]);
                }
            }
        });

        popup.result.then(function (updatedItem) {
            self.glossary.items[index] = updatedItem;
        });
    };

    self.addGlossaryItem = function () {
        var popup = $modal.open({
            templateUrl: "/profileEditor/editItemPopup.htm",
            controller: "GlossaryModalController",
            controllerAs: "glossaryModalCtrl",
            size: "md",
            resolve: {
                item: function () {
                    return {term: null, description: null, uuid: null};
                }
            }
        });

        popup.result.then(function (updatedItem) {
            self.prefix = updatedItem.term.substring(0, 1);
            self.page = self.prefix;

            self.loadGlossary(self.prefix)
        });
    };
});


/**
 * Edit Glossary Item modal dialog controller
 */
profileEditor.controller("GlossaryModalController", function (profileService, messageService, util, $modalInstance, item) {
    var self = this;

    self.item = item;
    self.error = null;

    self.ok = function () {
        var promise = profileService.saveGlossaryItem(util.getEntityId("opus"), self.item.uuid, self.item);
        promise.then(function (data) {
                if (!self.item.uuid) {
                    self.item.uuid = data.uuid;
                }
                $modalInstance.close(self.item);
            },
            function () {
                self.error = "An error occurred while saving the item."
            }
        );
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    }
});