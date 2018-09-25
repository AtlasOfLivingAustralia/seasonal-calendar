import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {CalendarResolverService} from "./calendar-resolver.service";
import { CalendarsResolverService } from "./calendars-resolver.service";

const routes: Routes = [
  { path: '', component: CalendarsGalleryComponent, resolve: { calendars: CalendarsResolverService } },
  { path: 'admin', loadChildren: './admin/admin.module#AdminModule' },
  { path: ':name', component: CalendarLandingComponent, resolve: { calendar: CalendarResolverService } },
  { path: '**', component: PageNotFoundComponentComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(
    routes,
    {
      // enableTracing: true
    })
  ],
  exports: [RouterModule],
  providers: [
    CalendarResolverService,
    CalendarsResolverService
  ]
})
export class AppRoutingModule { }
