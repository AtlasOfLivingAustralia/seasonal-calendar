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
            <g:render template="/calendar/project"/>
        </div>

        <div id="tab_2" class="tab-pane">
            <g:render template="/calendar/calendar"/>
        </div>
    </div>

</div>
<!-- JUSTIFIED TAB -->

</div>