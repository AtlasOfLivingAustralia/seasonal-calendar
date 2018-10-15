import { BrowserModule, Title } from '@angular/platform-browser';
import {APP_INITIALIZER, ErrorHandler, NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import { ReportingErrorHandler } from "./shared/services/reporting-error-handler";
import { windowProvider, WindowToken } from "./shared/window";
import { Logger } from "./shared/services/logger.service";
import { NavbarComponent } from './navbar/navbar.component';
import { LoadingBarHttpClientModule } from "@ngx-loading-bar/http-client";
import { LoadingBarRouterModule } from "@ngx-loading-bar/router";
import { MessageService } from "./shared/services/message.service";
import { MessageBarComponent } from "./messages/message-bar.component";
import { MessageComponent } from "./messages/message.component";
import { LanguageGroupComponent } from './language-group/language-group.component';
import { CalendarMapComponent } from './calendar-map/calendar-map.component';
import {
  AuthModule,
  OidcSecurityService,
  OpenIDImplicitFlowConfiguration,
  OidcConfigService,
  AuthWellKnownEndpoints
} from 'angular-auth-oidc-client';
import {environment} from "../environments/environment";
import {TokenInterceptor} from "./shared/services/token-interceptor";
import {UserInfoService} from "./shared/services/calendar.service";

export function loadConfig(oidcConfigService: OidcConfigService) {
  console.log('APP_INITIALIZER STARTING');
  return () => oidcConfigService.load_using_stsServer(environment.stsServer);
}

@NgModule({
  declarations: [
    AppComponent,
    CalendarsGalleryComponent,
    CalendarLandingComponent,
    PageNotFoundComponentComponent,
    NavbarComponent,
    MessageBarComponent,
    MessageComponent,
    LanguageGroupComponent,
    CalendarMapComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    LeafletModule,
    LoadingBarHttpClientModule,
    LoadingBarRouterModule,
    AuthModule.forRoot(),
    // SharedModule
  ],
  entryComponents: [],
  providers: [
    { provide: WindowToken, useFactory: windowProvider },
    { provide: ErrorHandler, useClass: ReportingErrorHandler },
    Logger,
    Title,
    MessageService,
    OidcConfigService,
    UserInfoService,
    {
      provide: APP_INITIALIZER,
      useFactory: loadConfig,
      deps: [OidcConfigService],
      multi: true,
    },
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(
    private oidcSecurityService: OidcSecurityService,
    private oidcConfigService: OidcConfigService
  ) {
    this.oidcConfigService.onConfigurationLoaded.subscribe(() => {
      const openIDImplicitFlowConfiguration = new OpenIDImplicitFlowConfiguration();

      openIDImplicitFlowConfiguration.stsServer = environment.stsServer;
      // openIDImplicitFlowConfiguration.redirect_url = document.baseURI;
      openIDImplicitFlowConfiguration.redirect_url = document.baseURI + 'assets/silent_login.html';
      // The Client MUST validate that the aud (audience) Claim contains its client_id value registered at the Issuer
      // identified by the iss (issuer) Claim as an audience.
      // The ID Token MUST be rejected if the ID Token does not list the Client as a valid audience,
      // or if it contains additional audiences not trusted by the Client.
      openIDImplicitFlowConfiguration.client_id = environment.clientId;
      openIDImplicitFlowConfiguration.response_type = 'id_token token';
      openIDImplicitFlowConfiguration.scope = 'openid profile email offline_access ala roles';
      openIDImplicitFlowConfiguration.post_logout_redirect_uri = document.baseURI;
      openIDImplicitFlowConfiguration.start_checksession = false; // CAS doesn't support this
      openIDImplicitFlowConfiguration.silent_renew = true;
      openIDImplicitFlowConfiguration.silent_renew_offset_in_seconds = 3600;
      openIDImplicitFlowConfiguration.silent_renew_url = document.baseURI + 'assets/silent_renew.html';
      openIDImplicitFlowConfiguration.silent_redirect_url = document.baseURI + 'assets/silent_renew.html';
      // openIDImplicitFlowConfiguration.storage = window.localStorage;
      // openIDImplicitFlowConfiguration.post_login_route = '/';
      // HTTP 403
      openIDImplicitFlowConfiguration.forbidden_route = '/403';
      // HTTP 401
      openIDImplicitFlowConfiguration.unauthorized_route = '/401';
      openIDImplicitFlowConfiguration.log_console_warning_active = true; // false
      openIDImplicitFlowConfiguration.log_console_debug_active = true; // false
      // id_token C8: The iat Claim can be used to reject tokens that were issued too far away from the current time,
      // limiting the amount of time that nonces need to be stored to prevent attacks.The acceptable range is Client specific.
      openIDImplicitFlowConfiguration.max_id_token_iat_offset_allowed_in_seconds = 3600; // 3
      openIDImplicitFlowConfiguration.auto_userinfo = true;
      openIDImplicitFlowConfiguration.auto_clean_state_after_authentication = true;
      openIDImplicitFlowConfiguration.trigger_authorization_result_event = true;
      // openIDImplicitFlowConfiguration.post_login_route = '/';
      // openIDImplicitFlowConfiguration.resource = '';
      // openIDImplicitFlowConfiguration.silent_renew = true;
      // openIDImplicitFlowConfiguration.trigger_authorization_result_event = false;

      const authWellKnownEndpoints = new AuthWellKnownEndpoints();
      authWellKnownEndpoints.setWellKnownEndpoints(this.oidcConfigService.wellKnownEndpoints);

      window.addEventListener("sc-login-message", (evt: CustomEvent) => {
        console.log("sc-login-message", evt.detail);
        this.oidcSecurityService.authorizedCallback(evt.detail)
      });
      this.oidcSecurityService.setupModule(
        openIDImplicitFlowConfiguration,
        authWellKnownEndpoints
      );

    });

    console.log('APP STARTING');
  }
}
