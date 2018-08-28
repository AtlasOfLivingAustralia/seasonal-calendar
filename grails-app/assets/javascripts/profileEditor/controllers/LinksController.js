/**
 *  Links controller
 */
profileEditor.controller('LinksEditor', function (profileService, util, messageService) {
    var self = this;
    
    self.links = [];

    self.init = function (edit) {
        self.readonly = edit != 'true';

        loadLinks();
    };

    function loadLinks() {
        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);
        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;
                self.links = data.profile.links;
            },
            function () {
                messageService.alert("An error occurred while retrieving the links.");
            }
        );
    }

    self.addLink = function (form) {
        self.links.unshift({uuid: null, url: "http://", description: "", title: ""});
        if (form) {
            form.$setDirty();
        }
    };

    self.deleteLink = function (idx, form) {
        self.links.splice(idx, 1);
        if (form) {
            form.$setDirty();
        }
    };

    self.saveLinks = function (form) {
        var promise = profileService.updateLinks(self.opusId, self.profile.uuid, JSON.stringify({profileId: self.profile.uuid, links: self.links}));
        promise.then(function () {
                messageService.success("Links successfully updated.");
                loadLinks();
                if (form) {
                    form.$setPristine();
                }
            },
            function () {
                messageService.alert("An error occurred while updating the links.");
            }
        );
    };
});