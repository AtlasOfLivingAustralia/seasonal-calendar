<!DOCTYPE HTML>
<html lang="en-US">
<head>
    <title><g:layoutTitle/></title>
    <meta charset="utf-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!--=== Google Fonts ===-->
    <link href='http://fonts.googleapis.com/css?family=Bangers' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Roboto+Slab:300,700,400' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Raleway:600,400,300' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700' rel='stylesheet' type='text/css'>
    <!--=== Color Scheme, three colors are available red.css, orange.css and gray.css ===-->
    <link rel="stylesheet" id="scheme-source" href="${request.contextPath}/egret/css/schemes/red.css" />

    <!--=== Internet explorer fix ===-->
    <!-- [if lt IE 9]>
    <script src="http://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="http://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif] -->
    <r:require modules="sc_calendar" />
    <r:layoutResources/>
</head>
<body>
<!--=== Preloader section starts ===-->
<section id="preloader">
    <div class="loading-circle fa-spin"></div>
</section>
<!--=== Preloader section Ends ===-->

<!--=== Header section Starts ===-->
<div id="header" class="header-section">
    <!-- sticky-bar Starts-->
    <div class="sticky-bar-wrap">
        <div class="sticky-section">
            <div id="topbar-hold" class="nav-hold container">
                <nav class="navbar" role="navigation">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-responsive-collapse">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <!--=== Site Name ===-->
                        <a class="site-name navbar-brand" href="#">Seasonal Calendar</a>
                    </div>

                    <!-- Main Navigation menu Starts -->
                    <div class="collapse navbar-collapse navbar-responsive-collapse">
                        <ul class="nav navbar-nav navbar-right">
                            <li>
                                <a href="${request.contextPath}/"><i class="fa fa-home"></i>  Home</a>
                            </li>

                            <auth:ifNotLoggedIn>
                                <li>
                                    <a href="${grailsApplication.config.casServerLoginUrl}?service=${grailsApplication.config.serverName}${request.forwardURI}">
                                        <i class="fa fa-sign-in"></i>
                                        Login
                                    </a>
                                </li>
                            </auth:ifNotLoggedIn>
                            <auth:ifLoggedIn>
                                <li><a href="${request.contextPath}/calendar/settings"><i class="fa fa-plus"></i> Seasonal Calendar</a></li>
                                <li>
                                    <a href="${grailsApplication.config.grails.serverURL}/logout/logout?casUrl=${grailsApplication.config.casServerUrlPrefix}/logout&appUrl=${grailsApplication.config.grails.serverURL}/">
                                        <i class="fa fa-sign-out"></i>
                                        Logout
                                    </a>
                                </li>
                            </auth:ifLoggedIn>
                        </ul>
                    </div>
                    <!-- Main Navigation menu ends-->
                </nav>
            </div>
        </div>
    </div>
    <!-- sticky-bar Ends-->
    <!--=== Header section Ends ===-->
</div>

<!--  Main content goes here. -->
<g:layoutBody/>


<!--=== Footer section Starts ===-->
<div id="section-footer" class="footer-wrap">
    <div class="container footer center">
        <div class="row">
            <div class="col-lg-12">
                <h4 class="footer-title"><!-- Footer Title -->
                    <a class="site-name" href="#">Seasonal Calendars</a>
                </h4>

                <!--
                <div class="social-icons">
                    <ul>
                        <li><a href="#"><i class="fa fa-facebook-square"></i></a></li>
                        <li><a href="#"><i class="fa fa-twitter-square"></i></a></li>
                        <li><a href="#"><i class="fa fa-google-plus-square"></i></a></li>
                        <li><a href="#"><i class="fa fa-pinterest-square"></i></a></li>
                        <li><a href="#"><i class="fa fa-flickr"></i></a></li>
                        <li><a href="#"><i class="fa fa-linkedin-square"></i></a></li>
                    </ul>
                </div>
                -->

                <p class="copyright">Atlas of Living Australia, All rights reserved &copy; 2016</p>
            </div>
        </div>
    </div>
</div>
<!--=== Footer section Ends ===-->

<r:layoutResources/>
</body>
</html>