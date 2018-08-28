/**
 * Angular service for embedding external content that supports the oEmbed standard
 */
profileEditor.factory('embedService', ['$http', '$q', '$sce', function ($http, $q, $sce) {

  var services = [
    {
      "type": "video",
      "name": "YouTube",
      // "api": "https://www.youtube.com/oembed", // no CORS, FFS Google :(
      "api": "https://noembed.com/embed",
      "patterns": [
        /https?:\/\/(?:[^\.]+\.)?youtube\.com\/watch\/?\?(?:.+&)?v=([^&]+)/i,
        /https?:\/\/(?:[^\.]+\.)?(?:youtu\.be|youtube\.com\/embed)\/([a-zA-Z0-9_-]+)/i
      ]
    },
    {
      "type": "video",
      "name": "TED Talks",
      "api": "https://www.ted.com/services/v1/oembed.json",
      //"api": "https://www.ted.com/talks/oembed.json",
      "patterns": [
        /https?:\/\/ted\.com\/talks\/.*/i,
        /https?:\/\/((?:www|embed)\.)?ted\.com\/talks\/.*/i
      ]
    },
    {
      "type": "audio",
      "name": "SoundCloud",
      "api": "http://soundcloud.com/oembed",
      "patterns": [
        /https?:\/\/soundcloud\.com\/.*/i,
        /https?:\/\/soundcloud\.com\/.*\/.*/i
      ]
    },
    {
      "type": "video",
      "name": "Wistia",
      "api": "http://fast.wistia.com/oembed",
      "patterns": [
        /https?:\/\/(.+)?(wistia.com|wi.st)\/.*/i
      ]
    },
    {
      "type": "video",
      "name": "Vimeo",
      "api": "https://vimeo.com/api/oembed.json",
      "patterns": [
        /https?:\/\/(?:www\.)?vimeo\.com\/.+/i,
        /https?:\/\/vimeo\.com\/.*/i,
        /https?:\/\/vimeo\.com\/album\/.*\/video\/.*/i,
        /https?:\/\/vimeo\.com\/channels\/.*\/.*/i,
        /https?:\/\/vimeo\.com\/groups\/.*\/videos\/.*/i,
        /https?:\/\/vimeo\.com\/ondemand\/.*\/.*/i,
        /https?:\/\/player\.vimeo\.com\/video\/.*/i
      ]
    }
  ];

  // var servicesMap = _.indexBy(services, 'name');

  return {
    describe: function(url) {
      var service = this.findService(url);

      if (service) {
        return $http.get(service.api, {params: { url: url } }).then(function (response) {
          if (response.data.html) {
            response.data.html = $sce.trustAsHtml(response.data.html);
          }
          return { service: service, embed: response.data};
        });
      } else {
        return $q.reject({error: "No matching service"});
      }
    },
    findService: function(url) {
      return _.find(services, function(service) {
        return _.any(service.patterns, function(pattern) {
          return url.search(pattern) != -1;
        });
      });
    }
  }
}]);