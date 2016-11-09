<!-- JUSTIFIED TAB -->
<div id="calendarAddEdit">
<div class="tabs nomargin-top">

    <!-- tabs -->
    <ul class="nav nav-tabs nav-justified">
        <li class="active">
            <a href="#tab_1" data-toggle="tab">
                <h4><i class="fa fa-calendar"></i> Calendar</h4>
            </a>
        </li>
        <li>
            <a href="#tab_2" data-toggle="tab">
                <h4><i class="fa fa-cloud"></i> Seasons</h4>
            </a>
        </li>
    </ul>

    <!-- tabs content -->
    <div class="tab-content">
        <div id="tab_1" class="tab-pane active">
            <g:render template="/calendar/contextInfo"/>
        </div>

        <div id="tab_2" class="tab-pane">
            <g:render template="/calendar/seasons"/>
        </div>
    </div>

</div>

<!-- JUSTIFIED TAB -->

    <div class="row">
        <div class="col-lg-12 text-left">
            <div id="seasonal-calendar-result"></div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12 well">
            <div class="col-lg-6 text-left">
                <button data-bind="click: save" type="submit" class="btn btn-default"><i class="fa fa-save"></i> Save</button>
            </div>

            <div class="col-lg-6 text-right">
                <span data-bind="if: calendarStatus() == 'unpublished'">
                    <button type="submit" data-bind="click: publish" class="btn btn-success"><i class="fa fa-check"></i> Publish</button>
                </span>
                <span data-bind="if: calendarStatus() == 'published'">
                    <button type="submit" data-bind="click: unpublish" class="btn btn-danger"><i class="fa fa-close"></i> Unpublish</button>
                </span>

                <span data-bind="if: calendarStatus() == 'unpublished' || calendarStatus() == 'published'">
                    <button type="submit" data-bind="click: previewCalendar" class="btn btn-info"><i class="fa fa-eye"></i> Preview</button>
                    <button type="submit" data-bind="click: deleteCalendar" class="btn btn-danger"><i class="fa fa-close"></i> Delete</button>
                </span>
            </div>
        </div>
    </div>

</div>