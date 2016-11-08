<div class="row">
    <div class="col-lg-12">
        <p class="text-center text-danger">
            a. <strong>WHO</strong> - background on the organisations and individuals responsible for developing the calendar
        </p>
    </div>
</div>

<div class="well">
    <div class="margin-bottom-1"></div>
    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="org-name">Organisation Name: </label>
                    <input data-bind="value: organisation.name" type="text" class="form-control" id="org-name" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <label for="contributors">Contributors: </label>
                <input data-bind="value: organisation.contributors" type="text" class="form-control" id="contributors" placeholder="">

            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="org-url">Organisation URL:</label>
                    <input data-bind="value: organisation.url" type="text" class="form-control" id="org-url" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="logo-url">Logo Url: </label>
                    <input data-bind="value: organisation.logo" type="text" class="form-control" id="logo-url" placeholder="">
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-name">Contact name:</label>
                    <input  data-bind="value: organisation.contactName" type="text" class="form-control" id="contact-name" placeholder="">
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="contact-email">Contact email: </label>
                    <input data-bind="value: organisation.email" type="text" class="form-control" id="contact-email" placeholder="">
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-description">General Description: </label>
                    <textarea data-bind="value: organisation.orgDescription" rows="5" class="form-control" id="organisation-description" placeholder=""></textarea>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="form-group">
                    <label for="organisation-keywords">keywords: </label>
                    <input data-bind="value: organisation.keywords"  type="text" class="form-control" id="organisation-keywords" placeholder="">
                </div>
            </div>

        </div>
    </div>

</div>