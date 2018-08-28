/**
 * Compares two profiles
 */
profileEditor.service('profileComparisonService', function ($http, util, $cacheFactory, config, $log) {
    function compare(left, right) {
        var d = new diff_match_patch();
        var diff = d.diff_main(right ? right : "", left ? left : "");
        var changed = false;
        angular.forEach(diff, function(d) {
            if (d[0] != 0) {
                changed = true;
            }
        });
        return {changed: changed, comp: d.diff_prettyHtml(diff), left: left, right: right};
    }

    function compareLists(left, right, keyField, compareFields) {
        var comparisons = [];
        var changed = false;
        angular.forEach(right, function(rightItem) {
            var oldInNew = false;
            var comparison = null;
            angular.forEach(left, function(leftItem) {
                if (leftItem[keyField] == rightItem[keyField]) {
                    comparison = {};
                    comparison[keyField] = leftItem[keyField];
                    angular.forEach(compareFields, function(compareField) {
                        comparison[compareField] = compare(leftItem[compareField], rightItem[compareField]);
                        if (comparison[compareField].changed) {
                            changed = true;
                        }
                    });
                    comparisons.push(comparison);
                    oldInNew = true;
                }
            });

            if (!oldInNew) {
                comparison = {};
                comparison[keyField] = null;
                angular.forEach(compareFields, function(compareField) {
                    comparison[compareField] = compare(null, rightItem[compareField]);
                    if (comparison[compareField].changed) {
                        changed = true;
                    }
                });
                comparisons.push(comparison);
            }
        });

        angular.forEach(left, function(leftItem) {
            var newInOld = false;
            angular.forEach(right, function(rightItem) {
                if (leftItem[keyField] == rightItem[keyField]) {
                    newInOld = true;
                }
            });

            if (!newInOld) {
                var comparison = {};
                comparison[keyField] = leftItem[keyField];
                angular.forEach(compareFields, function(compareField) {
                    comparison[compareField] = compare(leftItem[compareField], null);
                    if (comparison[compareField].changed) {
                        changed = true;
                    }
                });
                comparisons.push(comparison);
            }
        });

        return {changed: changed, comp: comparisons};
    }

    function getAttachmentFields(left, right) {
        var fields = [];

        if (!_.isUndefined(left.attachments) && left.attachments.length > 0) {
            fields = Object.keys(left.attachments[0]);
        } else if (!_.isUndefined(right.attachments) && right.attachments.length > 0) {
            fields = Object.keys(right.attachments[0])
        }

        return fields;
    }

    return {
        compareProfiles: function(left, right) {
            var diff = {changed:false};

            if (left && right) {
                diff.fullName = compare(left.fullName, right.fullName);
                diff.matchedName = {
                    fullName: compare(left.matchedName ? left.matchedName.fullName : null,
                        right.matchedName ? right.matchedName.fullName : null)
                };

                diff.authorship = compareLists(left.authorship, right.authorship, "category", ["text"]);
                diff.bibliography = compareLists(left.bibliography, right.bibliography, "plainText", ["plainText"]);

                var documentFields = ["name", "attribution", "licence", "embeddedContent"];
                diff.documents = compareLists(left.documents, right.documents, "documentId", documentFields);
                diff.links = compareLists(left.links, right.links, "uuid", ["url", "title", "description"]);
                diff.bhl = compareLists(left.bhl, right.bhl, "uuid", ["url", "title", "description"]);
                diff.primaryImage = compare(left.primaryImage, right.primaryImage);
                if ((!left.attributes && !right.attributes) || (left.attributes.length == 0 && right.attributes.length == 0)) {
                    diff.lastAttributeChange = compare(left.lastAttributeChange, right.lastAttributeChange);
                }
                diff.attributes = compareLists(left.attributes, right.attributes, "title", ["plainText"]);
                diff.specimenIds = compare(
                    left.specimenIds ? left.specimenIds.join(", ") : "",
                    right.specimenIds ? right.specimenIds.join(", ") : "");
                diff.imageSettings = compareLists(left.imageSettings, right.imageSettings, "imageId", ["displayOption"]);
                diff.nslNomenclatureIdentifier = compare(left.nslNomenclatureIdentifier, right.nslNomenclatureIdentifier);
                diff.scientificName = compare(left.scientificName, right.scientificName);
                diff.archivedDate = compare(left.archivedDate, right.archivedDate);
                diff.privateMode = compare(left.privateMode+"", right.privateMode+"");// convert boolean to string
                var attachmentFields = getAttachmentFields(left, right);
                diff.attachments = compareLists(left.attachments, right.attachments, "uuid", attachmentFields);

                var imageFields = ["license", "originalFileName", "rights", "rightsHolder", "title", "description"];
                diff.privateImages = compareLists(left.privateImages, right.privateImages, "imageId", imageFields);
                diff.stagedImages = compareLists(left.stagedImages, right.stagedImages, "imageId", imageFields);
                diff.showLinkedOpusAttributes = compare(left.showLinkedOpusAttributes?"On":"Off", right.showLinkedOpusAttributes?"On":"Off");
            }

            for (var prop in diff) {
                if( diff.hasOwnProperty( prop ) ) {
                    if (diff[prop].changed) {
                        diff.changed = true;
                    }
                }
            }

            return diff;
        },

        getImageUrl : function(profile, imageId) {
            var url = null;
    
            var privateImage = _.find(profile.privateImages, function(item) { return item.imageId == imageId });
            if (!_.isUndefined(privateImage)) {
                var extension = privateImage.originalFileName.substring(privateImage.originalFileName.lastIndexOf("."));
                url = config.contextPath + "/opus/" + profile.opusId + "/profile/" + profile.uuid + "/image/" + imageId + extension + "?type=PRIVATE";
            } else {
                var stagedImage = _.find(profile.stagedImages, function(item) { return item.imageId == imageId });
                if (!_.isUndefined(stagedImage)) {
                    var extension = stagedImage.originalFileName.substring(stagedImage.originalFileName.lastIndexOf("."));
                    url = config.contextPath + "/opus/" + profile.opusId + "/profile/" + profile.uuid + "/image/" + imageId + extension + "?type=STAGED";
                } else {
                    url = config.imageServiceUrl + "/image/proxyImageThumbnail?imageId=" + imageId;
                }
            }
    
            return url;
        }
    }
});
