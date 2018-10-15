import {NgModule} from "@angular/core";
import {AdminDashboardComponent} from "./admin-dashboard/admin-dashboard.component";
import {CalendarEditComponent} from "./calendar-edit/calendar-edit.component";
import {ImageUploadModalComponent} from "./image-upload-modal/image-upload-modal.component";
import {ImageUploadComponent} from "./image-upload/image-upload.component";
import {ListInputComponent} from "./list-input/list-input.component";
import {CalendarDrawMapComponent} from "../calendar-map/calendar-draw-map.component";
import {FeatureEditComponent} from "./feature-edit/feature-edit.component";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {LeafletDrawModule} from "@asymmetrik/ngx-leaflet-draw";
import {
  NgbModalModule,
  NgbProgressbarModule,
  NgbTabsetModule,
  NgbTooltipModule,
  NgbTypeaheadModule,
  NgbButtonsModule
} from "@ng-bootstrap/ng-bootstrap";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {AdminRoutingModule} from "./admin.routing.module";
import {CommonModule} from "@angular/common";
import {library} from "@fortawesome/fontawesome-svg-core";
import {faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import { CalendarUsersModalComponent } from './calendar-users-modal/calendar-users-modal.component';
import {ConfirmModalComponent} from "./confirm-modal/confirm-modal.component";
import {HasRolePipe} from "./pipes/has-role.pipe";

// Add an icon to the library for convenient access in other components
library.add(faQuestionCircle);

@NgModule({
  declarations: [
    AdminDashboardComponent,
    CalendarEditComponent,
    ImageUploadModalComponent,
    ImageUploadComponent,
    ListInputComponent,
    CalendarDrawMapComponent,
    FeatureEditComponent,
    CalendarUsersModalComponent,
    ConfirmModalComponent,
    HasRolePipe
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
    NgbButtonsModule,
    FontAwesomeModule,
    // SharedModule
  ],
  entryComponents: [
    ImageUploadModalComponent,
    CalendarUsersModalComponent,
    ConfirmModalComponent
  ]
})
export class AdminModule { }
