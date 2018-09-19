import { BrowserModule, Title } from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import {FormsModule} from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import { NgbProgressbarModule, NgbTabsetModule, NgbModalModule, NgbTypeaheadModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { LeafletDrawModule } from '@asymmetrik/ngx-leaflet-draw';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { AdminComponent } from './admin/admin.component';
import { CalendarEditComponent } from './calendar-edit/calendar-edit.component';
import { FeatureEditComponent } from './feature-edit/feature-edit.component';
import { ImageUploadModalComponent } from './image-upload-modal/image-upload-modal.component';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { ListInputComponent } from './list-input/list-input.component';
import { CalendarMapComponent } from './calendar-map/calendar-map.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {ReportingErrorHandler} from "./shared/reporting-error-handler";
import {windowProvider, WindowToken} from "./shared/window";
import {Logger} from "./shared/logger.service";
import { NavbarComponent } from './navbar/navbar.component';
import {LoadingBarHttpClientModule} from "@ngx-loading-bar/http-client";
import {LoadingBarRouterModule} from "@ngx-loading-bar/router";
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';

// Add an icon to the library for convenient access in other components
library.add(faQuestionCircle);

@NgModule({
  declarations: [
    AppComponent,
    CalendarsGalleryComponent,
    CalendarLandingComponent,
    AdminComponent,
    CalendarEditComponent,
    PageNotFoundComponentComponent,
    ImageUploadModalComponent,
    ImageUploadComponent,
    ListInputComponent,
    CalendarMapComponent,
    FeatureEditComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    LeafletModule.forRoot(),
    LeafletDrawModule.forRoot(),
    NgbTabsetModule,
    NgbProgressbarModule,
    NgbModalModule,
    NgbTooltipModule,
    NgbTypeaheadModule,
    LoadingBarHttpClientModule,
    LoadingBarRouterModule,
    FontAwesomeModule
  ],
  entryComponents: [
    ImageUploadModalComponent
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
