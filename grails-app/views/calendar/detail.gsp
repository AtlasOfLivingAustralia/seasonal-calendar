<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title> New | Seasonal Calendars</title>

    <r:script disposition="head">
        var scConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            id: "${id}",
            mapReadonly: true,
            calendarHome: "${createLink(controller: 'calendar', action: 'settings')}",
            addCalendar: "${createLink(controller: 'calendar', action: 'addCalendar')}",
            editCalendar: "${createLink(controller: 'calendar', action: 'editCalendar')}",
            getCalendar: "${createLink(controller: 'calendar', action: 'getCalendar')}",
            deleteCalendar: "${createLink(controller: 'calendar', action: 'delete')}",
            listCalendars: "${createLink(controller: 'calendar', action: 'listCalendars')}"
        };
    </r:script>
    <r:require modules="seasonal_calendar, map"/>
</head>

<body>

<div class="margin-bottom-1"></div>
<!--=== Features section Starts ===-->
<div class="icon-sc-rain"></div>

<div id="detailCalendar">
    <g:render template="detailTabs"/>
</div>

<r:script>
    $(window).load(function () {
        ko.applyBindings(SeasonalCalendarVM(), document.getElementById('detailCalendar'));
    });
</r:script>


</body>

</html>