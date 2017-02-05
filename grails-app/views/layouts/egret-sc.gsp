<!DOCTYPE HTML>
<html lang="en-US">
<head>
    <title><g:layoutTitle/></title>
    <meta charset="utf-8">

    <meta name="app.version" content="${g.meta(name:'app.version')}"/>
    <meta name="app.build" content="${g.meta(name:'app.build')}"/>
    <meta name="app.name" content="${g.meta(name:'app.name')}"/>
    <meta name="description" content="Indigenous Seasonal Calendars - Atlas of Living Australia"/>
    <meta name="author" content="Atlas of Living Australia">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!--=== Google Fonts ===-->
    <link href='https://fonts.googleapis.com/css?family=Bangers' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Roboto+Slab:300,700,400' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Raleway:600,400,300' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700' rel='stylesheet' type='text/css'>

    <!--=== Internet explorer fix ===-->
    <!-- [if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
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
                                <sc:ifUserisScAdmin>
                                    <li><a href="${request.contextPath}/calendar/settings"><i class="fa fa-calendar"></i> Manage Calendars</a></li>
                                </sc:ifUserisScAdmin>

                                <li class="dropdown pull-left">
                                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                                        <i class="fa fa-user"></i>
                                        <sc:currentUserDisplayName/>
                                    </a>
                                    <ul class="dropdown-menu">
                                        <li role="menuitem">
                                            <a href="${grailsApplication.config.myprofile.baseUrl}">
                                                <i class="fa fa-user"></i>
                                                My Profile
                                            </a>
                                        </li>
                                        <li role="menuitem">
                                            <a href="${request.contextPath}/calendar/myCalendars">
                                                <i class="fa fa-calendar"></i>
                                                My Calendars
                                            </a>
                                        </li>
                                        <li role="menuitem">
                                            <a href="${grailsApplication.config.grails.serverURL}/logout/logout?casUrl=${grailsApplication.config.casServerUrlPrefix}/logout&appUrl=${grailsApplication.config.grails.serverURL}/">
                                                <i class="fa fa-sign-out"></i>
                                                Logout
                                            </a>
                                        </li>
                                    </ul>
                                </li>

                            </auth:ifLoggedIn>
                        </ul>
                    </div>
                    <!-- Main Navigation menu ends-->
                </nav>
            </div>
        </div>
        <ala:systemMessage showTimestamp="true"/>
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
            <div>
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
                <p class="copyright">Intellectual Property</p>
                <p>The material in the seasonal calendars may be protected by copyright laws and may be used as permitted under the Copyright Act 1968 or in accordance with licences or notices/labels granted by the copyright owner and/or Indigenous knowledge holder(s). Even though the material exists in a digital context on this site, it is important to respect and acknowledge the community and/or cultural context within which it was created and how it can be used and shared.</p>
                <p>The material in the seasonal calendars may also include Indigenous cultural and intellectual property (ICIP) which is the cultural heritage of Indigenous people which comprises all objects, sites and knowledge, the nature or use of which has been transmitted or continues to be transmitted from generation to generation, and which is regarded as pertaining to a particular Indigenous group or its territory. The heritage of an Indigenous people is a living one and includes objects, knowledge and literary and artistic works which may be created in the future based on that heritage. Users of this website acknowledge that Indigenous people have the right to control, own and maintain their ICIP in accordance with Article 31 of the Declaration on the Rights of Indigenous Peoples.</p>
                <small><a target="_blank" href="http://www.ala.org.au/about-the-atlas/terms-of-use/">Terms of use for the Atlas of Living Australia </a></small>
            </div>
        </div>
    </div>
</div>
<!--=== Footer section Ends ===-->

<r:layoutResources/>
</body>
</html>