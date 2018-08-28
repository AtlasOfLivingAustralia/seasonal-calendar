profileEditor.directive('closeModal', function () {
    return {
        restrict: 'E',
        require: [],
        scope: {
            close: '&'
        },
        template: '<a href="" class="pull-right" ng-click="close()"><span class="fa fa-close"></span></a>'
    };
});