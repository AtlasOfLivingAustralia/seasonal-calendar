<!-- JUSTIFIED TAB -->
<div class="margin-bottom-10"></div>
<div id="calendar-homepage">
    <div class="tabs nomargin-top">
        <!-- tabs -->
        <ul class="nav nav-tabs nav-justified">
            <li class="active">
                <a href="#calendar-about" id="calendar-about-tab" data-toggle="tab">
                    <h4><i class="fa fa-home"></i> About</h4>
                </a>
            </li>
            <li>
                <a href="#calendar-detail" id="calendar-detail-tab" data-toggle="tab">
                    <h4><i class="fa fa-calendar"></i> Calendar</h4>
                </a>
            </li>
        </ul>

        <!-- tabs content -->
        <div class="tab-content">
            <div id="calendar-about" class="tab-pane active well">
                <g:render template="/calendar/detailAbout"/>
            </div>

            <div id="calendar-detail" class="tab-pane">
                <g:render template="/calendar/detailCalendar"/>
            </div>
        </div>

    </div>

    <!-- JUSTIFIED TAB -->
</div>