<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-sc"/>
    <title> New | Seasonal Calendars</title>

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
    <r:require modules="jqueryValidationEngine, seasonal_calendar"/>
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
                <h3>Seasonal calendar</h3>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-3 well">
            <div id="calendarList">
                <ul>
                    <li><i class="fa fa-plus"></i><button class="btn btn-link" data-bind="click: addCalendar">Add New Calendar <span data-bind="if: transients.isNew"><i class="fa fa-chevron-right"></i></span></button></li>
                    <!-- ko foreach: items -->
                    <li><i class="fa fa-edit"></i><button class="btn btn-link" data-bind="click: redirect"><span data-bind="text: name"></span> <span data-bind="if: transients.isActive"><i class="fa fa-chevron-right"></i></span></button></li>
                    <!-- /ko -->
                </ul>
            </div>
        </div>

        <div class="col-lg-9">
            <div id="calendarAddEdit">
                <div class="row">
                    <div class="col-lg-12">
                        <g:render template="info"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-12">
                        <g:render template="seasons"/>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-12 text-left">
                        <div id="seasonal-calendar-result"></div>
                     </div>
                </div>

                <div class="row well">
                    <div class="col-lg-12">
                        <div class="col-lg-6 text-left">
                            <button data-bind="click: save" type="submit" class="btn btn-default"><i class="fa fa-save"></i> Save</button>

                            <span data-bind="if: calendarStatus() == 'unpublished'">
                                <button type="submit" data-bind="click: publish" class="btn btn-success"><i class="fa fa-check"></i> Publish</button>
                            </span>
                            <span data-bind="if: calendarStatus() == 'published'">
                                <button type="submit" data-bind="click: unpublish" class="btn btn-danger"><i class="fa fa-close"></i> Unpublish</button>
                            </span>

                        </div>

                        <div class="col-lg-6 text-right">
                            <span data-bind="if: calendarStatus() == 'unpublished' || calendarStatus() == 'published'">
                                <button type="submit" data-bind="click: previewCalendar" class="btn btn-info"><i class="fa fa-eye"></i> Preview</button>
                                <button type="submit" data-bind="click: deleteCalendar" class="btn btn-danger"><i class="fa fa-close"></i> Delete</button>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="margin-bottom-10"></div>
</div>

<!--=== Features section Ends ===-->

<r:script>
    $(window).load(function () {
        ko.applyBindings(SeasonalCalendarVM(), document.getElementById('calendarAddEdit'));
        ko.applyBindings(SeasonalCalendarsMenuVM(), document.getElementById('calendarList'));
        $('#calendar-validation').validationEngine();
    });
</r:script>

</body>
</html>