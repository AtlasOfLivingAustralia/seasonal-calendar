<section id="section-maintabs">

    <div class="container maintabs center">

        <div class="row">
            <div class="col-lg-12">
                <div class="container">

                    <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">

                        <div class="panel panel-default">

                            <div class="panel-heading" role="tab" id="headingOne">
                                <h4 class="panel-title">
                                    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                        <h4>1. Seasonal Calendar</h4>
                                    </a>
                                </h4>
                            </div>

                            <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                                <div class="panel-body">
                                    <g:render template="detailFlatView"/>
                                </div>
                            </div>
                        </div>

                        <span data-bind="if: imageUrl()">
                            <div class="panel panel-default">

                                <div class="panel-heading" role="tab" id="headingTwo">
                                    <h4 class="panel-title">
                                        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                            <h4>2. Chart</h4>
                                        </a>
                                    </h4>
                                </div>

                                <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                                    <div class="panel-body">
                                        <span data-bind="if: imageUrl()">
                                            <img data-bind="attr:{src: imageUrl()}" alt="" />
                                        </span>

                                    </div>
                                </div>
                            </div>
                        </span>

                        <span data-bind="if: multimedia()">
                            <div class="panel panel-default">

                                <div class="panel-heading" role="tab" id="headingThree">
                                    <h4 class="panel-title">
                                        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseTwo">
                                            <h4>3. Multimedia</h4>
                                        </a>
                                    </h4>
                                </div>

                                <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                                    <div class="panel-body">
                                        <span data-bind="html: transients.iframe()"></span>
                                    </div>
                                </div>
                            </div>
                        </span>

                    </div>

                </div>
            </div>
        </div>

    </div>

</section>
<!--=== Features section Ends ===-->


<!-- ko foreach : seasons -->
    <!-- ko foreach : features -->
        <!-- Start Latest News Section -->
<div class="section-modal modal fade" data-bind="attr:{id: 'modal_'+ transients.id()}" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-content">
        <div class="close-modal" data-dismiss="modal">
            <div class="lr">
                <div class="rl">
                </div>
            </div>
        </div>

        <div class="container">

            <div class="row text-center">
                <div class="col-md-12">
                    <div class="section-title text-center">
                        <h2>
                            <span data-bind="text: $parent.seasonName"></span> -
                            <span data-bind="text: $parent.description"></span>
                        </h2>

                        <h3>
                            <span data-bind="text: featureName"></span>
                            <span data-bind="if: speciesName() && speciesLink()">
                            </br>
                                <a target="_blank" data-bind="attr:{'href': speciesLink()}">Species: <span data-bind="text: speciesName"></span></a>
                            </span>
                        </h3>
                    </div>

                </div>
            </div>

            <div class="row text-center">

                <div class="col-md-6">
                    <!-- ko if: thumbImages().length > 0 -->
                    <div class="row text-center">
                        <div class="col-md-12">
                            <div class="latest-post">
                                <div class="c-wrapper">
                                    <div data-bind="attr:{'id': 'carousel_'+ transients.id()}" class="carousel slide" data-ride="carousel">
                                        <!-- Indicators -->
                                        <ol class="carousel-indicators">
                                            <!-- ko foreach: thumbImages -->
                                            <li data-bind="attr:{'data-target': '#carousel_'+ $parent.transients.id(), 'class': $index() == 0 ? 'active': '','data-slide-to': ''+$index()}"></li>
                                            <!-- /ko -->
                                        </ol>

                                        <!-- Wrapper for slides -->
                                        <div class="carousel-inner">
                                            <!-- ko foreach: thumbImages -->
                                            <div data-bind="attr:{'class': $index() == 0 ? 'item active': 'item'}">
                                                <img data-bind="attr:{'src': url}" alt="...">
                                                <div class="carousel-caption">
                                                    <!--<h3>Caption Text</h3> -->
                                                </div>
                                            </div>
                                            <!-- /ko -->

                                        </div>

                                        <!-- Controls -->
                                        <a class="left carousel-control" data-bind="attr:{'href': '#carousel_'+ transients.id()}"  role="button" data-slide="prev">
                                            <span class="glyphicon glyphicon-chevron-left"></span>
                                        </a>
                                        <a class="right carousel-control" data-bind="attr:{'href': '#carousel_'+ transients.id()}"  role="button" data-slide="next">
                                            <span class="glyphicon glyphicon-chevron-right"></span>
                                        </a>
                                    </div> <!-- Carousel -->

                                </div>
                            </div>
                        </div>

                    </div>
                    <!-- /ko -->
                </div>

                <div class="col-md-6">
                    <p><span data-bind="text: description"></span></p>
                </div>
            </div>

            <div class="margin-bottom-10"></div>
            <div class="row text-center">
                <div class="col-md-12">
                    <button class="btn btn-lg btn-default" data-dismiss="modal"><i class="fa fa-close"></i> Close</button>
                </div>
            </div>
        </div>

    </div>
</div>
<!-- End Latest News Section -->
<!-- /ko -->
<!-- /ko -->