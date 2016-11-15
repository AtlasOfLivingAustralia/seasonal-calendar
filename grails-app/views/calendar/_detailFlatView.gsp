<ul class="nav nav-tabs nav-justified">
    <!-- ko foreach : seasons -->
    <!-- ko if: $index() == 0 -->
    <li class="active"><a data-toggle="pill" data-bind="attr:{'href': '#tab_'+$index()}"><h4 data-bind="text: seasonName() + '('+ seasonNameEnglish()+')' "></h4></a>
    </li>
    <!-- /ko -->

    <!-- ko if: $index() > 0 -->
    <li class=""><a data-toggle="pill" data-bind="attr:{'href': '#tab_'+$index()}"><h4 data-bind="text: seasonName() + '('+ seasonNameEnglish()+')'"></h4></a></li>
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
            <div class="col-lg-12">

                    <div class="section-title-tab text-center">
                        <h2><span data-bind="text: seasonName() + '('+ seasonNameEnglish()+')'"></span> - <span data-bind="text: description"></span></h2>
                        <p><label class="label label-default" data-bind="text: seasonMonths"></label></p>
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
