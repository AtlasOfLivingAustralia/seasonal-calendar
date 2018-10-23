import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {CalendarResolverService} from "./resolvers/calendar-resolver.service";
import { CalendarsResolverService } from "./resolvers/calendars-resolver.service";
import { LanguageGroupComponent } from './language-group/language-group.component';
import {UserCanActivateAdminService} from "./admin/route-guards/role-can-activate.service";
import {CalendarLandingComponent} from "./calendar-landing/calendar-landing.component";

const routes: Routes = [
  { path: '', component: CalendarsGalleryComponent, data: {publishedCalendarsOnly: true }, resolve: { calendars: CalendarsResolverService } },
  { path: 'admin', loadChildren: './admin/admin.module#AdminModule', canActivate: [ UserCanActivateAdminService ]},
  { path: '401', component: PageNotFoundComponentComponent },
  { path: '403', component: PageNotFoundComponentComponent },
  { path: '404', component: PageNotFoundComponentComponent },
  { path: ':id', component: LanguageGroupComponent, resolve: { calendar: CalendarResolverService } },
  { path: ':id/calendar', component: CalendarLandingComponent, resolve: { calendar: CalendarResolverService } }
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
    CalendarsResolverService,
    UserCanActivateAdminService
  ]
})
export class AppRoutingModule { }
