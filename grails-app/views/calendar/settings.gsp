<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title> New | Seasonal Calendars</title>

    <script type="text/javascript">
        var scConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            id: "${id}",
            calendarHome: "${createLink(controller: 'calendar', action: calendarManagementHome)}",
            addCalendar: "${createLink(controller: 'calendar', action: 'addCalendar')}",
            editCalendar: "${createLink(controller: 'calendar', action: 'editCalendar')}",
            getCalendar: "${createLink(controller: 'calendar', action: 'getCalendar')}",
            deleteCalendar: "${createLink(controller: 'calendar', action: 'delete')}",
            listCalendars: "${createLink(controller: 'calendar', action: 'listCalendars')}",
            previewCalendar:  "${createLink(controller: 'calendar', action: 'detail')}",
            speciesSearchUrl: "${createLink(controller: 'calendar', action: 'searchBie')}",
            bieUrl: "${grailsApplication.config.bie.url}",
            listMyCalendars: "${createLink(controller: 'calendar', action: 'listMyCalendars')}",
            previewCalendar:  "${createLink(controller: 'calendar', action: 'detail')}",
            onlyMyCalendars: true//${onlyMyCalendars}
        };
    </script>
    <asset:stylesheet src="jqueryValidationEngine.css" />
    <asset:stylesheet src="ala-map.css" />
    <asset:javascript src="jqueryValidationEngine.js" asset-defer="" />
%{--    <asset:javascript src="seasonal_calendar.js" asset-defer="" />--}%
    <asset:javascript src="ala-map-no-jquery-us.js" asset-defer="" />

</head>
<body>

<div class="margin-bottom-10"></div>

<!--=== Features section Starts ===-->
<div class="icon-sc-rain"></div>
<div class="container validationEngineContainer" id="calendar-validation">
    <div class="row">
        <div class="col-lg-3"></div>
        <div class="col-lg-9">
            <div class="section-title section-text text-center">
                <g:if test="${userIsAdmin && !onlyMyCalendars}">
                    <h3>Manage Calendars</h3>
                </g:if>
                <g:else>
                    <h3>My Calendars</h3>
                </g:else>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-3 well">
            <div id="calendarList" ng-controller="OpusController as opusCtrl" >
                <ul>
                    <li><i class="fa fa-plus"></i><button class="btn btn-link" data-bind="click: addCalendar">Add New Calendar <span data-bind="if: transients.isNew"><i class="fa fa-chevron-right"></i></span></button></li>
                    <div ng-repeat="opus in opusCtrl.opusList | orderBy: 'title'">
                        <li><i class="fa fa-edit"></i><a class="btn btn-link" href="${createLink(uri: '')}/calendar/settings/{{opus.uuid}}"><span>{{opus.title ? opus.title : opus.uuid}}</span></a></li>
                    %{--<!-- ko foreach: items -->--}%
                        %{--<li><i class="fa fa-edit"></i><button class="btn btn-link" data-bind="click: redirect"><span data-bind="text: name"></span> <span data-bind="if: transients.isActive"><i class="fa fa-chevron-right"></i></span></button></li>--}%
                    </div>
                    %{--<!-- /ko -->--}%
                </ul>
            </div>
        </div>

        <div class="col-lg-9">
            <div class="row">
                <div class="col-lg-12 text-left">
                    <div id="seasonal-calendar-result"></div>
                </div>
            </div>

                <g:render template="/calendar/tabs"/>
        </div>
    </div>

    <div class="margin-bottom-10"></div>
</div>

<!--=== Features section Ends ===-->
<asset:script type="text/javascript">
    $(window).load(function () {
        //ko.applyBindings(SeasonalCalendarVM(), document.getElementById('calendarAddEdit'));
        //ko.applyBindings(SeasonalCalendarsMenuVM(), document.getElementById('calendarList'));
        $('#calendar-validation').validationEngine();
        $('.helphover').popover({animation: true, trigger:'hover'});
    });
</asset:script>

</body>
</html>