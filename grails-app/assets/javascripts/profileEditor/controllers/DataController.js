/**
 * Controller for uploading & managing data sets
 */
profileEditor.controller('DataController', ["profileService", "util", "messageService", "Upload", function (profileService, util, messageService, Upload) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.dataSets = [];
    self.contextPath = util.contextRoot();
    self.uploadFile = util.getQueryParameter("uploadFile") === "true" || false;
    self.files = [];
    self.uploadedSandboxFileId = util.getQueryParameter("id");
    self.uploadedSandboxFileName = util.getQueryParameter("fn");

    self.deleteDataSet = function (dataResourceId) {
        var confirm = util.confirm("Are you sure you wish to delete this data set? This operation cannot be undone.");
        confirm.then(function () {
            profileService.deleteDataSet(self.opusId, dataResourceId).then(function () {
                loadDataSets();
            }, function () {
                messageService.alert("An error occurred while deleting the data set");
            });
        });
    };

    self.doUpload = function () {
        Upload.upload({
            url: self.contextPath + "/opus/" + self.opusId + "/data/uploadFile",
            file: self.files[0]
        }).success(function (data) {
            util.redirect(self.contextPath + "/opus/" + self.opusId + "/data/upload?uploadFile=true&fn=" + data.fileName + "&id=" + data.fileId);
            self.uploadedSandboxFileId = data.fileId;
            self.uploadedSandboxFileName = data.fileName;
        }).error(function () {
            messageService.alert("An error occurred while uploading your file.");
        });
    };

    self.fixSandboxUploadUrls = function () {
        // the sandbox upload page defines this global variable
        if (angular.isDefined(window.SANDBOXUPLOAD)) {
            window.SANDBOXUPLOAD.fileId = self.uploadedSandboxFileId;
            window.SANDBOXUPLOAD.uploadStatusUrl = self.contextPath + "/dataCheck/upload/uploadStatus?uid=";
            window.SANDBOXUPLOAD.uploadLink = self.contextPath + "/dataCheck/upload/uploadToSandbox";
            window.SANDBOXUPLOAD.parseColumnsWithFirstLineInfoUrl = self.contextPath + "/dataCheck/upload/parseColumnsWithFirstLineInfo?id=" + self.uploadedSandboxFileId + "&firstLineIsData=";
            window.SANDBOXUPLOAD.viewProcessDataUrl = self.contextPath + "/dataCheck/upload/processData?id=";
            window.SANDBOXUPLOAD.parseColumnsUrl = self.contextPath + "/dataCheck/upload/parseColumns?id=";
        }

        // make sure all ajax requests include the opus id and/or profile id
        if (angular.isDefined(self.opusId) || angular.isDefined(self.profileId)) {
            $.ajaxPrefilter(function (options, originalOptions) {
                if (options.url.indexOf("opusId") == -1) {
                    var combine = "";
                    if (originalOptions.url.indexOf("?") > -1) {
                        combine = "&";
                    } else {
                        combine = "?";
                    }

                    if (angular.isDefined(self.opusId)) {
                        options.url = originalOptions.url + combine + "opusId=" + encodeURIComponent(self.opusId);
                    }
                    if (angular.isDefined(self.profileId)) {
                        options.url = originalOptions.url + combine + "profileId=" + encodeURIComponent(self.profile);
                    }
                }
            });
        }

        if ($("#recognisedDataDiv").is(':visible') && window.init) {
            window.init();
        }
    };

    self.dataSourceOptionChanged = function () {
        var url = self.contextPath + "/opus/" + self.opusId + "/data/upload?uploadFile=" + self.uploadFile;
        self.uploadedSandboxFileName = null;
        self.uploadedSandboxFileId = null;

        util.redirect(url);
    };


    function loadDataSets() {
        profileService.getDataSets(self.opusId).then(function (dataSets) {
            self.dataSets = dataSets;
        });
    }

    loadDataSets();
}])
;