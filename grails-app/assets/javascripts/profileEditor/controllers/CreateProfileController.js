/**
 * Controller for handling creating a new profile (via a modal popup)
 */
profileEditor.controller('CreateProfileController', function (profileService, $modalInstance, opusId, duplicateExisting) {
    var self = this;

    self.opusId = opusId;
    self.scientificName = "";
    self.error = null;
    self.manuallyMatchedGuid = "";
    self.validName = false;
    self.manualHierarchy = [];

    self.duplicateExisting = duplicateExisting;
    self.profileToCopy = null;
    self.profiles = [];

    self.cancel = function () {
        $modalInstance.dismiss("Cancelled");
    };

    self.ok = function() {
        var future;

        if (self.duplicateExisting && self.validExistingProfileSelection()) {
            future = profileService.duplicateProfile(self.opusId, self.profileToCopy.profileId, self.scientificName, self.manuallyMatchedGuid, self.manualHierarchy);
        } else if (!self.duplicateExisting) {
            future = profileService.createProfile(self.opusId, self.scientificName, self.manuallyMatchedGuid, self.manualHierarchy);
        } else {
            return;
        }

        future.then(function (profile) {
                if (profile) {
                    $modalInstance.close(profile);
                } else {
                    self.error = "An error occurred while creating the profile.";
                }
            },
            function () {
                self.error = "An error occurred while creating the profile.";
            }
        );
    };

    self.searchByScientificName = function () {
        var searchResult = profileService.profileSearch(self.opusId, self.profileToCopy, true);
        searchResult.then(function (data) {
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.valid = function() {
        return !self.duplicateExisting || self.validExistingProfileSelection();
    };

    self.validExistingProfileSelection = function () {
        return !_.isUndefined(self.profileToCopy) && self.profileToCopy != null && !_.isUndefined(self.profileToCopy.profileId) && self.profileToCopy.profileId != null;
    }
});