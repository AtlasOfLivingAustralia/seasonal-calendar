<div class="col-sm-4">
    <ul class="features-hold">

        <li class="single-feature" title="unlimited Google fonts">
            <!-- ko if: thumbImages().length > 0 -->
            <img data-bind="attr:{'src': thumbImages()[0].url}" alt="" class="feature-image" /><!-- Feature Icon -->
            <!-- /ko -->

            <!-- ko if: thumbImages().length == 0 -->
            <img src="${request.contextPath}/images/no-image.png" alt="" class="feature-image" /><!-- Feature Icon -->
            <!-- /ko -->

            <!-- ko if: featureName() -->
            <h5 class="feature-title color-scheme" data-bind="text: featureName()"></h5>
            <!-- /ko -->
            <!-- ko if: featureNameEnglish()-->
            <h5 class="feature-title color-scheme" data-bind="text: featureNameEnglish()"></h5>
            <!-- /ko -->

            <p class="feature-text" data-bind="text: transients.shortDescription"></p>

            <a data-bind="attr:{href: '#modal_'+transients.id()}" href="#news-modal" data-toggle="modal" class="fancy-button button-line btn-col small zoom">
                More..
                <span class="icon">
                    <i class="fa fa-expand"></i>
                </span>
            </a>
        </li>

    </ul>
</div>