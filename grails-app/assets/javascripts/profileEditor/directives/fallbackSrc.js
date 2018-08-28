profileEditor.directive('fallbackSrc', function ($log) {
  return{
    restrict: 'A',
    link: function postLink(scope, element, attrs) {
      element.bind('error', function (e) {
        angular.element(this).attr("src", attrs.fallbackSrc);
      });
    }
  }
});
