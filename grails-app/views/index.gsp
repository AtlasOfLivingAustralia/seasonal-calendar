<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title>Home Page | Seasonal Calendars</title>

    <r:script disposition="head">
        var scConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            id: "${id}",
            calendarHome: "${createLink(controller: 'calendar', action: 'settings')}",
            addCalendar: "${createLink(controller: 'calendar', action: 'addCalendar')}",
            editCalendar: "${createLink(controller: 'calendar', action: 'editCalendar')}",
            getCalendar: "${createLink(controller: 'calendar', action: 'getCalendar')}",
            deleteCalendar: "${createLink(controller: 'calendar', action: 'delete')}",
            listCalendars: "${createLink(controller: 'calendar', action: 'listCalendars')}",
            previewCalendar:  "${createLink(controller: 'calendar', action: 'detail')}"
        };
    </r:script>
    <r:require modules="seasonal_calendar"/>
</head>
<body>

<!--=== Home Section Starts ===-->
<div id="section-home" class="home-section-wrap center">

    <div class="container home">
        <div class="row">
            <h1 class="well-come">Indigenous Seasonal Calendars</h1>

            <div class="col-md-8 col-md-offset-2">
                <p class="intro-message">
                    <g:message code="calendar.home.intro"/>
                </p>

                <div class="home-buttons">
                    <a href="#section-screenshots" class="fancy-button button-line button-white vertical">
                        Explore
                        <span class="icon">
                            <i class="fa fa-hand-o-right"></i>
                        </span>
                    </a>
                </div>
            </div>
        </div>
    </div>

</div>
<!--=== Home Section Ends ===-->

<!--=== ScreenShots section Starts ===-->
<section id="section-screenshots" class="screenshots-wrap">
    <div class="container screenshots animated" data-animation="fadeInUp" data-animation-delay="1000">

        <div id="calendarList">
            <div class="row porfolio-container">
                <!-- ko foreach: items -->
                    <!-- ko if: calendarStatus() == 'published' -->
                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <div class="screenshot">
                            <div class="photo-box">
                                <img data-bind="attr:{src: imageUrl() ? imageUrl : '${request.contextPath}/images/seasons-300.png' }" alt="" />
                                <div class="photo-overlay">
                                    <h4><span data-bind="text: name"></span></h4>
                                </div>
                                <span class="photo-zoom">
                                    <a data-bind="click: redirectToDetailPage" href="single-project.html" class="view-project"><i class="fa fa-search-plus fa-2x"></i></a>
                                </span>
                            </div>
                        </div>
                        <div class="text-center">
                            <h4 data-bind="text: name"></h4 >
                        </div>
                    </div>
                    <!-- /ko -->
                <!-- /ko -->
           </div>
        </div>


        <div id="portfolio-loader" class="center">
            <div class="loading-circle fa-spin"></div>
        </div> <!--=== Portfolio loader ===-->

        <div id="portfolio-load"></div><!--=== ajax content will be loaded here ===-->

        <div class="col-md-12 center back-button">
            <a class="backToProject fancy-button button-line bell btn-col" href="#">
                Back
                <span class="icon">
                    <i class="fa fa-arrow-left"></i>
                </span>
            </a>
        </div><!--=== Single portfolio back button ===-->
    </div>
</section>
<!--=== ScreenShots section Ends ===-->



<r:script>
    $(window).load(function () {

        ko.applyBindings(SeasonalCalendarsMenuVM(), document.getElementById('calendarList'));

        /* -----------------------------
         Backgroung slider
         ----------------------------- */
        'use strict';
        $.vegas('slideshow', {
            backgrounds:[
                { src:'images/0.jpg', fade:1000 },
                { src:'images/1.JPG', fade:1000 },
                { src:'images/2.jpg', fade:1000 },
                { src:'images/3.JPG', fade:1000 }
            ]
        })();
    });
</r:script>


</body>
</html>