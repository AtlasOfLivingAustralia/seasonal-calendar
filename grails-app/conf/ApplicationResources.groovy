modules = {

    sc_calendar{
        dependsOn 'egret_theme', 'knockout', 'bootbox'
        resource url: [dir:'css/', file:'common.css']
        resource url: [dir:'css/', file:'weather.css']
        resource url: [dir:'js/', file:'common.js']
    }

    seasonal_calendar {
        resource url: [dir:'js/', file:'seasonal_calendar.js']
    }

    egret_theme {
        dependsOn 'jquery'

        resource url: [dir:'egret/css/', file:'animate.css']
        resource url: [dir:'egret/css/', file:'baraja.css']
        resource url: [dir:'egret/css/', file:'bootstrap.css']
        resource url: [dir:'egret/css/', file:'color-switcher.css']
        resource url: [dir:'egret/css/', file:'fancy-buttons.css']
        resource url: [dir:'egret/css/', file:'font-awesome.min.css']
        resource url: [dir:'egret/css/', file:'jquery.bxslider.css']
        resource url: [dir:'egret/css/', file:'jquery.vegas.css']
        resource url: [dir:'egret/css/', file:'linea-icon.css']
        resource url: [dir:'egret/css/', file:'parallax-slider.css']
        resource url: [dir:'egret/css/', file:'responsive.css']
        resource url: [dir:'egret/css/', file:'style.css']
        resource url: [dir:'egret/js/', file:'bootstrap.min.js']
        resource url: [dir:'egret/js/', file:'bootstrapValidator.min.js']
        resource url: [dir:'egret/js/', file:'color-switcher.js']
        resource url: [dir:'egret/js/', file:'jquery.ajaxchimp.min.js']
        resource url: [dir:'egret/js/', file:'jquery.appear.js']
        resource url: [dir:'egret/js/', file:'jquery.baraja.js']
        resource url: [dir:'egret/js/', file:'jquery.bxslider.min.js']
        resource url: [dir:'egret/js/', file:'jquery.easing.1.3.js']
        resource url: [dir:'egret/js/', file:'jquery.fitvids.js']
        resource url: [dir:'egret/js/', file:'jquery.nav.js']
        resource url: [dir:'egret/js/', file:'jquery.nicescroll.min.js']
        resource url: [dir:'egret/js/', file:'jquery.vegas.min.js']
        resource url: [dir:'egret/js/', file:'modernizr.js']
        resource url: [dir:'egret/css/', file:'font-awesome.min.css']
        resource url: [dir:'egret/js/', file:'custom.js']
        resource url: [dir:'/egret/css/schemes/', file:'red.css']
    }

    lazyload {
        dependsOn 'jquery'
        resource url: [dir:'js', file:'jquery.lazyload.js']
    }

    knockout {
        resource url: [dir:'vendor/knockoutjs/3.3.0/', file:'knockout-3.3.0.debug.js']
        resource url: [dir:'vendor/knockoutjs/3.3.0/', file:'knockout-3.3.0.min.js']
        resource url: [dir:'vendor/knockoutjs/3.3.0/', file:'knockout.mapping-latest.js']
        resource url: [dir:'js/', file:'knockout_custombindings.js']
    }

    jqueryValidationEngine {
        dependsOn 'jquery'
        resource url: [dir:'vendor/jquery.validationEngine/2.6.2/', file:'jquery.validationEngine-en.js']
        resource url: [dir:'vendor/jquery.validationEngine/2.6.2/', file:'jquery.validationEngine.js']
        resource url: [dir:'vendor/jquery.validationEngine/2.6.2/', file:'validationEngine.jquery.css']
    }

    jquery {
        resource url: [dir:'egret/js/', file:'jquery-1.11.1.min.js']
    }

    bootbox {
        resource url: [dir:'vendor/bootbox/3.2.0', file:'bootbox.min.js']
    }
}