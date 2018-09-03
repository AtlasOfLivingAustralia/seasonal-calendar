import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CalendarsGalleryComponent } from './calendars-gallery/calendars-gallery.component';
import { CalendarLandingComponent } from './calendar-landing/calendar-landing.component';
import { AdminComponent } from './admin/admin.component';
import { CalendarEditComponent } from './calendar-edit/calendar-edit.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';

const routes: Routes = [
  { path: '', component: CalendarsGalleryComponent },
  { path: ':name', component: CalendarLandingComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'admin/calendar/:id', component: CalendarEditComponent },
  { path: '**', component: PageNotFoundComponentComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(
    routes,
    { enableTracing: true }
    )
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
