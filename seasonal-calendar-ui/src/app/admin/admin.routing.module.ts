import {AdminComponent} from "./admin.component";
import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {CalendarResolverService, NewCalendarResolverService} from "../calendar-resolver.service";
import {CalendarsResolverService} from "../calendars-resolver.service";
import {CalendarEditComponent} from "../calendar-edit/calendar-edit.component";

const routes : Routes = [
  { path: '', component: AdminComponent, resolve: { calendars: CalendarsResolverService } },
  { path: 'calendar/create', component: CalendarEditComponent, resolve: { calendar: NewCalendarResolverService } },
  { path: 'calendar/:id', component: CalendarEditComponent, resolve: { calendar: CalendarResolverService } },
];

@NgModule({
  imports: [RouterModule.forChild(
    routes
  )
  ],
  exports: [RouterModule],
  providers: [
    NewCalendarResolverService,
    CalendarsResolverService
  ]
})
export class AdminRoutingModule { }
