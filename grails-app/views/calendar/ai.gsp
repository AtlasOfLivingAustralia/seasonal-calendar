<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="egret-ai"/>
    <title> AI | Seasonal Calendars</title>
    <script type="text/javascript">
        var scConfig = {
            serverUrl: "${grailsApplication.config.grails.serverURL}",
            imagePostUrl: "${createLink(controller: 'calendar', action: 'imageIdentify')}"
        };
    </script>
    <asset:stylesheet src="jqueryValidationEngine.css" />
    <asset:stylesheet src="ala-map.css" />
    <asset:javascript src="jqueryValidationEngine.js" asset-defer="" />
    <asset:javascript src="seasonal_calendar.js" asset-defer="" />
    <asset:javascript src="ala-map-no-jquery-us.js" asset-defer="" />
</head>
<body>

<div class="margin-bottom-10"></div>

<!--=== Features section Starts ===-->
<div class="icon-sc-rain"></div>
<div class="container validationEngineContainer" id="calendar-validation">
    <div class="row" id="imageIdentification">

        <div class="col-lg-12">
            <div class="section-title section-text text-center">


                <div class="well">
                    <div class="row">
                        <div class="col-lg-12">
                            <h5 class="text-center">
                                <h3>Enter the species image URL</h3>
                                <br>
                                <span class="label label-success">PROTOTYPE</span>
                                <br>
                                <br>
                                <input data-validation-engine="validate[required]" data-bind="value: imageUrl" type="text" class="form-control large" id="imageurl-name" placeholder="Enter Image URL">
                            </h5>
                            <div class="form-group text-center">
                                <br>
                                <button type="submit"  data-bind="click: load" class="btn btn-default btn-lg text-center"><i class="fa fa-search"></i> Identify the image</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="well">
                    <div class="row">

                        <div class="col-lg-6">
                            <img height="400" data-bind="attr:{'src': imageUrl}"/>
                        </div>
                        <div class="col-lg-6 text-left">
                            <h3>Result</h3>
                            <span data-bind="if: loadTime()">

                                <h5>Load time: <span data-bind="text: loadTime()"></span> ms</h5>
                            </span>
                            <h5 class="text-left">
                                <span data-bind="if:loading()"><strong>Loading....</strong></span>
                                <span data-bind="visible: matched() == 1">
                                    <h5 class="text-success"><span class="fa fa-check-circle"></span> Match found</h5>
                                </span>
                                <span data-bind="visible: matched() == 2">
                                <h5 class="text-success"><span class="fa fa-exclamation-circle"></span> Match not found</h5>
                                </span>
                                <span data-bind="foreach: concepts">
                                    <!-- ko if : value() >= 0.2 -->
                                    <h5>Common name: <span data-bind="text: commonName"></span></h5>
                                    <h5>Scientific name: <span data-bind="text: scientificName"> </span></h5>
                                    <h5>Confidence value: <span data-bind="text: value"></span></h5>

                                    <hr>
                                    <!-- /ko -->
                                </span>

                            </h5>

                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <div class="well">
        <h5>Example images to test:</h5>
        <code>
            <h5> Redback_Spider</h5>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=2cbfcbf0-ae37-4f36-87b9-95a54b4ffdff
            <br> http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=2927420e-1708-47bb-b532-aa1e3c5972a8
            <br> http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=b8cd4b31-08f6-42b8-88a7-a9831cf05db2
        </code>
        <!--
        <code>
            <h5>Little raven</h5>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=367e207f-ad1f-492e-886b-b57fbc822b60
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=a4362ff5-5a77-42d2-93f8-7573eef7a70b
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=adb0f478-e0ec-4cf8-a9fe-6660200a16b0
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=dac81f31-875c-46bf-b5ce-8e1890973aa1
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=f155954e-0866-4946-94fc-9f25e5a41b95
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=1e8ff36c-7011-4999-a706-0b6c3b0b4f5c
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=d951fe30-691a-47f5-b05c-7ad53eef631d
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=4561fdcc-619f-4766-bb2d-57eb74c3f135
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=98b1453d-a26a-43aa-8c8b-fed46374f7de
            <br>
            http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=539670ca-f96a-406b-87cb-f71aeb805fd6
        </code>
        -->
         <code>

             <h5>Magpie</h5>

             http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=b121f67d-8009-4f69-8e5a-06c88d2b6551
             <br>
             http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=9192ca13-27b7-4005-87f9-0608e4f4b269
             <br>
             http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=d40702b8-cddb-4cca-a9af-6111c8ebfc30
             <br>
             Invalid
             <br>
                http://images.ala.org.au/image/proxyImageThumbnailLarge?imageId=51633f8e-697e-4507-a249-54aaf05b3bca
        </code>
    </div>


    <div class="margin-bottom-10"></div>
</div>

<!--=== Features section Ends ===-->

<asset:script type="text/javascript">
    $(window).load(function () {
        ko.applyBindings(SeasonalCalendarsImageIdentification(), document.getElementById('imageIdentification'));
        $('#calendar-validation').validationEngine();
        $('.helphover').popover({animation: true, trigger:'hover'});
    });
</asset:script>

</body>
</html>