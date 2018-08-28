/**
 * Opus controller
 */
profileEditor.controller('ShareRequestController', function (profileService, util, messageService) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.requestingOpusId = util.getPathItem(util.LAST);
    self.alreadyRespondedTo = true;

    profileService.getOpus(self.opusId).then(function(data) { self.opus = data });
    profileService.getOpus(self.requestingOpusId).then(function(data) {
        self.requestingOpus = data;

        angular.forEach(self.requestingOpus.requestedSupportingOpuses, function(request) {
            if (request.uuid == self.opusId && request.requestStatus == 'REQUESTED') {
                self.alreadyRespondedTo = false;
            }
        });
    });

    self.respond = function(approve) {
        var message;
        if (approve) {
            message = "Are you sure you wish to grant access to all data in your collection?";
        } else {
            message = "Are you sure you wish to deny access to the data within your collection?";
        }
        var confirm = util.confirm(message);
        confirm.then(function() {
            var promise = profileService.respondToSupportingCollectionRequest(self.opusId, self.requestingOpusId, approve ? "accept" : "decline");
            promise.then(function() {
                messageService.success("You have successfully responded to the request to share your collection information. You may close this page.");
            }, function() {
                messageService.alert("An error has occurred while responding to the request to share your collection information");
            })
        });
    };
});
