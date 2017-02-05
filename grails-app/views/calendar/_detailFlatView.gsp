<ul class="nav nav-tabs nav-justified">
    <!-- ko foreach : seasons -->
    <!-- ko if: $index() == 0 -->
        <li class="active">
            <a data-toggle="pill" data-bind="attr:{'href': '#tab_'+$index()}">
            <!-- ko if: seasonName() -->
                <strong><p data-bind="text: seasonName()"></p></strong>
            <!-- /ko -->
            <!-- ko if: seasonNameEnglish() -->
                <strong><p data-bind="text: seasonNameEnglish()"></p></strong>
            <!-- /ko -->
            </a>
        </li>
    <!-- /ko -->

    <!-- ko if: $index() > 0 -->
        <li>
            <a data-toggle="pill" data-bind="attr:{'href': '#tab_'+$index()}">
                <!-- ko if: seasonName() -->
                <strong><p data-bind="text: seasonName()"></p></strong>
                <!-- /ko -->
                <!-- ko if: seasonNameEnglish() -->
                <strong><p data-bind="text: seasonNameEnglish()"></p></strong>
                <!-- /ko -->
            </a>
        </li>
    <!-- /ko -->
    <!-- /ko -->
</ul>
<div class="margin-bottom-1"></div>

<div class="tab-content">
    <!-- ko foreach : seasons -->
    <div data-bind="attr:{'class': 'tab-pane fade in ' + ($index() == 0 ? 'active' : ''), 'id': 'tab_'+$index()}">
    <div class="row">
        <div class="col-lg-5"></div>
        <div class="col-lg-2">
            <!-- ko if: weatherIcon() -->
            <div data-bind="attr:{class: weatherIcon()}"></div>
            <!--  /ko -->
        </div>
        <div class="col-lg-5"></div>
    </div>
        <div class="row">
            <div class="col-lg-6 text-center">

                    <div class="section-title-tab text-center">

                        <!-- ko if: seasonName() -->
                        <h2><strong data-bind="text: seasonName()"></strong></h2>
                        <!-- /ko -->
                        <!-- ko if: seasonNameEnglish() -->
                        <h2><strong data-bind="text: seasonNameEnglish()"></strong></h2>
                        <!-- /ko -->

                    </div>
            </div>
            <div class="col-lg-6 text-center">

                <div class="section-title-tab text-center">
                    <h4><span data-bind="text: description"></span></h4>
                    <h4 data-bind="text: seasonMonths"></h4>
                </div>
            </div>
        </div>

        <!-- ko foreach : features -->
        <span data-bind="if: ($index()+1) % 3 == 0">
            <div class="row">
                <g:render template="features"/>
            </div>
        </span>

        <span data-bind="if: ($index()+1) % 3 != 0">
            <g:render template="features"/>
        </span>
        <!-- /ko -->

    </div>
    <!-- /ko -->
</div>
