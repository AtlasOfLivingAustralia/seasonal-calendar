/**
 * Angular service for managing transient user interface messages (alerts, warnings, etc)
 */
profileEditor.factory('messageService', function ($rootScope) {

    var SUCCESS = "success";
    var ALERT = "danger";
    var WARNING = "warning";
    var INFO = "info";


    function success(message, leaveExisting) {
        msg(SUCCESS, message, leaveExisting);
    }

    function alert(message, leaveExisting) {
        msg(ALERT, message, leaveExisting);
    }

    function info(message, leaveExisting) {
        msg(INFO, message, leaveExisting);
    }

    function warning(message, leaveExisting) {
        msg(WARNING, message, leaveExisting);
    }

    function msg(type, message, leaveExisting) {
        $rootScope.$emit('displayAlerts', [{type: type, msg: message}], leaveExisting);
    }


    function alertStayOn(message) {
        msgStayOn(ALERT, message);
    }


    function msgStayOn(type, message) {
        $rootScope.$emit('stayOnDisplayAlerts', [{type: type, msg: message}]);
    }

    /**
     * Public API
     */
    return {
        success: success,
        warning: warning,
        info: info,
        alert: alert,
        alertStayOn: alertStayOn,

        SUCCESS: SUCCESS,
        ERROR: ALERT,
        INFO: INFO,
        WARNING: WARNING
    }
});