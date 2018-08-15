<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title> New | Seasonal Calendars</title>

    <script type="text/javascript">
        var scConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            id: "${id}",
            mapReadonly: true,
            calendarHome: "${createLink(controller: 'calendar', action: 'settings')}",
            addCalendar: "${createLink(controller: 'calendar', action: 'addCalendar')}",
            editCalendar: "${createLink(controller: 'calendar', action: 'editCalendar')}",
            getCalendar: "${createLink(controller: 'calendar', action: 'getCalendar')}",
            deleteCalendar: "${createLink(controller: 'calendar', action: 'delete')}",
            listCalendars: "${createLink(controller: 'calendar', action: 'listCalendars')}",
            bieUrl: "${grailsApplication.config.bie.url}"
        };
    </script>
    <asset:stylesheet src="ala-map.css" />
    <asset:javascript src="seasonal_calendar.js" asset-defer="" />
    <asset:javascript src="ala-map-no-jquery-us.js" asset-defer="" />
</head>

<body>

<div class="margin-bottom-1"></div>
<!--=== Features section Starts ===-->
<div class="icon-sc-rain"></div>

<div id="detailCalendar">
    <g:render template="detailTabs"/>
</div>

<asset:script type="text/javascript">
    $(window).load(function () {
        ko.applyBindings(SeasonalCalendarVM(), document.getElementById('detailCalendar'));
    });
</asset:script>


</body>

</html>