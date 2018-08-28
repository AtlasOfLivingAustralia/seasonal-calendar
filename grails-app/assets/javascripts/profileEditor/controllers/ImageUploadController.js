/**
 * Upload image modal dialog controller - just provides a modal wrapper around the image-upload directive
 */
profileEditor.controller("ImageUploadController", function ($scope, opus, $modalInstance, util, image) {
    var self = this;

    self.opus = opus;
    self.image = image;
    self.updateMode = image != null;

    self.uploadUrl = util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/profile/" + util.getEntityId("profile") + "/image/upload";

    self.modalTitle = self.updateMode ? "Update image" : "Upload image";

    self.ok = function () {
        if (self.updateMode) {
            $scope.$broadcast("saveImageChanges");
        } else {
            $scope.$broadcast("performUpload");
        }
    };

    self.uploadComplete = function (imageMetadata) {
        $modalInstance.close(imageMetadata);
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    };
});