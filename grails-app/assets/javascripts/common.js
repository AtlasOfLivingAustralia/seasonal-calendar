/**
 Bootstrap Alerts -
 Function Name - showAlert()
 Inputs - message,alerttype,target
 Example - showalert("Invalid Login","alert-error","alert-placeholder")
 Types of alerts -- "alert-error","alert-success","alert-info"
 Required - You only need to add a alert_placeholder div in your html page wherever you want to display these alerts "<div id="alert_placeholder"></div>"
 **/
function showAlert(message, alerttype, target) {

    $('#'+target).append('<div id="alertdiv" class="alert ' +  alerttype + '"><a class="close" data-dismiss="alert">X</a><p align="center">'+message+'</p></div>')
    var scrollPos = $('#'+target) ? $('#'+target).offset().top -100: 0;
    if(scrollPos){
        $(window).scrollTop(scrollPos);
    }
    setTimeout(function() {
        $("#alertdiv").fadeOut(1000);
        setTimeout(function() {
            $("#alertdiv").remove();
        },1000);
    }, 5000);
}

function isUrlAndHostValid(url) {
    var allowedHost = ['fast.wistia.com','embed-ssl.ted.com', 'www.youtube.com', 'player.vimeo.com'];
    return (url && isUrlValid(url) && $.inArray(getHostName(url), allowedHost) > -1)
};

function isUrlValid(url) {
    return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(url);
};

function getHostName(href) {
    var l = document.createElement("a");
    l.href = href;
    return l.hostname;
};

function buildiFrame(embeddedVideo){
    var html = $.parseHTML(embeddedVideo);
    var iframe = "";
    if(html){
        for(var i = 0; i < html.length; i++){
            var element = html[i];
            var attr = $(element).attr('src');
            if(typeof attr !== typeof undefined && attr !== false){
                var height =  element.getAttribute("height") ?  element.getAttribute("height") : "315";
                iframe = isUrlAndHostValid(attr)  ? '<iframe width="100%" src ="' +  attr + '" height = "' + height + '"/></iframe>' : "";
            }
            return iframe;
        }
    }
    return iframe;
};

