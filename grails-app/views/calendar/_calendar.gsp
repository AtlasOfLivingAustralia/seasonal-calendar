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
