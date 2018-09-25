import {NgModule} from "@angular/core";
import {AdminComponent} from "./admin.component";
import {CalendarEditComponent} from "../calendar-edit/calendar-edit.component";
import {ImageUploadModalComponent} from "../image-upload-modal/image-upload-modal.component";
import {ImageUploadComponent} from "../image-upload/image-upload.component";
import {ListInputComponent} from "../list-input/list-input.component";
import {CalendarMapComponent} from "../calendar-map/calendar-map.component";
import {FeatureEditComponent} from "../feature-edit/feature-edit.component";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {LeafletDrawModule} from "@asymmetrik/ngx-leaflet-draw";
import {
  NgbModalModule,
  NgbProgressbarModule,
  NgbTabsetModule,
  NgbTooltipModule,
  NgbTypeaheadModule
} from "@ng-bootstrap/ng-bootstrap";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {AdminRoutingModule} from "./admin.routing.module";
import {CommonModule} from "@angular/common";
import {library} from "@fortawesome/fontawesome-svg-core";
import {faQuestionCircle} from "@fortawesome/free-solid-svg-icons";

// Add an icon to the library for convenient access in other components
library.add(faQuestionCircle);

@NgModule({
  declarations: [
    AdminComponent,
    CalendarEditComponent,
    ImageUploadModalComponent,
    ImageUploadComponent,
    ListInputComponent,
    CalendarMapComponent,
    FeatureEditComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    AdminRoutingModule,
    LeafletModule,
    LeafletDrawModule,
    NgbTabsetModule,
    NgbProgressbarModule,
    NgbModalModule,
    NgbTooltipModule,
    NgbTypeaheadModule,
    FontAwesomeModule
  ],
  entryComponents: [
    ImageUploadModalComponent
  ],
  providers: [
    { provide: 'PublishedOnly', useValue: false }
  ]
})
export class AdminModule { }
