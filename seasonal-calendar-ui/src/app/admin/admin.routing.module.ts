import {AdminDashboardComponent} from "./admin-dashboard/admin-dashboard.component";
import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {CalendarResolverService, NewCalendarResolverService} from "../resolvers/calendar-resolver.service";
import {CalendarsResolverService} from "../resolvers/calendars-resolver.service";
import {CalendarEditComponent} from "./calendar-edit/calendar-edit.component";
import {
  RoleCanActivateService,
  UserCanActivateAdminService,
  UserCanEditCalendarService
} from "./route-guards/role-can-activate.service";
import {ADMIN_ROLES} from "../shared/roles";

const routes : Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    data: { publishedCalendarsOnly: false },
    canActivate: [ UserCanActivateAdminService ],
    resolve: { calendars: CalendarsResolverService }
  },
  {
    path: 'create-calendar',
    component: CalendarEditComponent,
    data: { roles: ADMIN_ROLES},
    canActivate: [ RoleCanActivateService ],
    resolve: { calendar: NewCalendarResolverService }
  },
  {
    path: 'calendar/:id',
    component: CalendarEditComponent,
    canActivate: [ UserCanEditCalendarService ],
    resolve: { calendar: CalendarResolverService }
  },
];

@NgModule({
  imports: [RouterModule.forChild(
    routes
  )
  ],
  exports: [RouterModule],
  providers: [
    NewCalendarResolverService,
    CalendarsResolverService,
    RoleCanActivateService,
    UserCanActivateAdminService,
    UserCanEditCalendarService
  ]
})
export class AdminRoutingModule { }
