<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title>Home Page | Seasonal Calendars</title>

    %{--<asset:script type="text/javascript">
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
    </asset:script>
    <asset:javascript src="seasonal_calendar.js" asset-defer="" />--}%
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
                    <a href="#section-services" class="fancy-button button-line button-white vertical">
                        Learn more
                        <span class="icon">
                            <i class="fa fa-info-circle"></i>
                        </span>
                    </a>
                </div>
            </div>
        </div>
    </div>

</div>
<!--=== Home Section Ends ===-->

<!--=== ScreenShots section Starts ===-->
<section id="section-screenshots" class="screenshots-wrap step-even">
    <div class="container screenshots animated" data-animation="fadeInUp" data-animation-delay="1000">

        <div id="calendarList">
            <div class="row porfolio-container" ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
                <div ng-repeat="opus in opusCtrl.opusList | orderBy: 'title'" class="col-md-4 col-sm-4 col-xs-6">
                    <div class="screenshot">
                        <div class="photo-box">
                            <a href="${request.contextPath}/calendar/{{opus.shortName ? opus.shortName : opus.uuid}}"
                               target="_self">
                                <img class="img-responsive collection-thumbnail thumbnail" src=""
                                     ng-src="{{opus.thumbnailUrl | default:'${asset.assetPath(src: "seasons-300.png")}' }}"
                                     alt="{{opus.title}} logo" title="{{opus.title}}"></a>
                                <h4 class="font-xxsmall" style="width: 160px;"><a
                                        href="${request.contextPath}/opus/{{opus.shortName ? opus.shortName : opus.uuid}}"
                                        target="_self"><strong>{{opus.title}}</strong></a></h4>

                            %{--<img data-bind="attr:{src: imageUrl() ? imageUrl : '${assetPath(src: 'seasons-300.png')}" alt="" />
                            <div class="photo-overlay">
                                <h4><span data-bind="text: name"></span></h4>
                            </div>
                            <span class="photo-zoom">
                                <a data-bind="click: redirectToDetailPage" href="single-project.html" class="view-project"><i class="fa fa-search-plus fa-2x"></i></a>
                            </span>--}%
                        </div>
                    </div>

  %{--              <!-- ko foreach: items -->
                    <!-- ko if: calendarStatus() == 'published' -->
                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <div class="screenshot">
                            <div class="photo-box">
                                <img data-bind="attr:{src: imageUrl() ? imageUrl : '${assetPath(src: 'seasons-300.png')}" alt="" />
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
                <!-- /ko -->--}%
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

<section id="section-services" class="section-step step-even">
    <div class="container services">
        <div class="row">

            <div class="col-md-10 col-md-offset-1 center section-title">
                <h3>More...</h3>
            </div>

            <!-- Single Service Starts -->
            <div class="col-md-12 col-sm-12 service animated" data-animation="fadeInLeft" data-animation-delay="700">
                <div class="service-desc">
                    <h4 class="service-title color-scheme">Ongoing development</h4>
                    <p class="service-description justify">
                        This is a test site where we are developing with Indigenous knowledge holders and researchers an information platform that is useful for a two-way sharing of Indigenous seasonal ecological knowledge, including bio-cultural indicators, use and management practices and cultural context. It is an attempt to bridge different knowledge systems and aims to respect and recognise the value of Indigenous knowledge and give prominence to it along side western science biodiversity knowledge.
                    </p>
                    <p>
                        The platform is still at a prototype stage as we work out and improve design and interface features, but also the connectivity and visualization of different types of information and data.
                    </p>
                    <p>
                        It is important that each indigenous seasonal calendar is seen in context - the context or purpose for which it was created and operates, as well as how Indigenous knowledge holders want it acknowledged and used, so as to ensure that the calendar's cultural integrity is maintained and is not misinterpreted or misused. Therefore it is important to read and respect the About section of a calendar and to engage directly with the owners of the calendar to understand more.
                    </p>
                    <p>
                        This platform forms part of a broader collaborative program of work by the ALA and partners regarding <a href="http://www.ala.org.au/ala-and-indigenous-ecological-knowledge-iek/" target="_blank">Indigenous Ecological Knowledge</a>.
                    </p>
                    <p>
                        Your <b>feedback and interest</b> in participating is very much welcome and encouraged - please contact Stephanie. Or just login and have a play and let us know what you want think. Share any ideas on where we can improve.
                    </p>
                    <p>
                        <b>Areas for further development</b> of the information platform include: optional circular representation of a calendar such as the <a href="http://www.larrakia.csiro.au" target="_blank"> Larrakia calendar</a> a map on the Home page so can see where the different calendars are from; information connected to other parts of the ALA (eg Species Profile pages); more structured administration function such as levels of access and editing, together with document management; a suite of traditional knowledge labels/notices that attach to information fields and media files.
                    </p>
                </div>
            </div>
            <!-- Single Service Ends -->

        </div>
    </div>
</section>
<!--=== Services section Ends ===-->

<asset:script type="text/javascript">
    $(window).load(function () {

      //  ko.applyBindings(SeasonalCalendarsMenuVM(), document.getElementById('calendarList'));

        /* -----------------------------
         Backgroung slider
         ----------------------------- */
        'use strict';
        $.vegas('slideshow', {
            backgrounds:[
                { src: '${assetPath(src: '0.jpg')}', fade:1000 },
                { src: '${assetPath(src: '1.JPG')}', fade:1000 },
                { src: '${assetPath(src: '2.jpg')}', fade:1000 },
                { src: '${assetPath(src: '3.JPG')}', fade:1000 }
            ]
        })();
    });
</asset:script>


</body>
</html>