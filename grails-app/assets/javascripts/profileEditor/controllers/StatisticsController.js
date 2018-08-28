/**
 * Statistics controller
 */
profileEditor.controller('StatisticsController', function (profileService, util) {
	var self = this;
	self.opusId = util.getEntityId("opus");
	self.statistics = [];

	self.initStatistics = function () {
		if (!self.opusId) {
			return;
		}

		var promise = profileService.getStatistics(self.opusId);
		promise.then(function (data) {
			self.statistics = data;
		});
	};

	self.initStatistics();
});
