import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { AdminComponent } from './admin/admin.component';
import { CalendarEditComponent } from './calendar-edit/calendar-edit.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {CalendarResolverService, NewCalendarResolverService} from "./calendar-resolver.service";
import { CalendarsResolverService } from "./calendars-resolver.service";

const routes: Routes = [
  { path: '', component: CalendarsGalleryComponent, resolve: { calendars: CalendarsResolverService } },
  { path: 'admin', component: AdminComponent, resolve: { calendars: CalendarsResolverService } },
  { path: 'admin/calendar/create', component: CalendarEditComponent, resolve: { calendar: NewCalendarResolverService } },
  { path: 'admin/calendar/:id', component: CalendarEditComponent, resolve: { calendar: CalendarResolverService } },
  { path: ':name', component: CalendarLandingComponent },
  { path: '**', component: PageNotFoundComponentComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(
    routes,
    { enableTracing: true }
    )
  ],
  exports: [RouterModule],
  providers: [
    CalendarResolverService,
    CalendarsResolverService,
    NewCalendarResolverService
  ]
})
export class AppRoutingModule { }
