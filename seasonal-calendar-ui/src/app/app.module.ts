import { BrowserModule, Title } from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {FormsModule} from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import { NgbTabsetModule } from '@ng-bootstrap/ng-bootstrap';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { AdminComponent } from './admin/admin.component';
import { CalendarEditComponent } from './calendar-edit/calendar-edit.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {ReportingErrorHandler} from "./shared/reporting-error-handler";
import {windowProvider, WindowToken} from "./shared/window";
import {Logger} from "./shared/logger.service";

@NgModule({
  declarations: [
    AppComponent,
    CalendarsGalleryComponent,
    CalendarLandingComponent,
    AdminComponent,
    CalendarEditComponent,
    PageNotFoundComponentComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    LeafletModule,
    NgbTabsetModule
  ],
  providers: [
    { provide: WindowToken, useFactory: windowProvider },
    { provide: ErrorHandler, useClass: ReportingErrorHandler },
    Logger,
    Title,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
