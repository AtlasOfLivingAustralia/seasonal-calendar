import { BrowserModule, Title } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
// import { LeafletDrawModule } from '@asymmetrik/ngx-leaflet-draw';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import { ReportingErrorHandler } from "./shared/reporting-error-handler";
import { windowProvider, WindowToken } from "./shared/window";
import { Logger } from "./shared/logger.service";
import { NavbarComponent } from './navbar/navbar.component';
import { LoadingBarHttpClientModule } from "@ngx-loading-bar/http-client";
import { LoadingBarRouterModule } from "@ngx-loading-bar/router";
import { MessageService } from "./messages/message.service";
import { MessageBarComponent } from "./messages/message-bar.component";
import { MessageComponent } from "./messages/message.component";

@NgModule({
  declarations: [
    AppComponent,
    CalendarsGalleryComponent,
    CalendarLandingComponent,
    PageNotFoundComponentComponent,
    NavbarComponent,
    MessageBarComponent,
    MessageComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    LeafletModule,
    // LeafletDrawModule,
    LoadingBarHttpClientModule,
    LoadingBarRouterModule
  ],
  entryComponents: [],
  providers: [
    { provide: WindowToken, useFactory: windowProvider },
    { provide: ErrorHandler, useClass: ReportingErrorHandler },
    { provide: 'PublishedOnly', useValue: true },
    Logger,
    Title,
    MessageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
