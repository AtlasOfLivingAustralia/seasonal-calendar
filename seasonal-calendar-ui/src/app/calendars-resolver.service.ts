import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ICalendar} from "./model/calendar";
import {Observable} from "rxjs";
import {Inject, Injectable} from "@angular/core";
import {CalendarService} from "./calendar.service";

@Injectable()
export class CalendarsResolverService implements Resolve<ICalendar[]> {

  constructor(
    private calendarService: CalendarService,
    private router: Router,
    @Inject('PublishedOnly') private publishedOnly: boolean = true) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICalendar[]> {
    // let parts = route.url;
    // let isAdmin = false;
    // if (parts.length > 0) {
    //   let first = parts[0];
    //   isAdmin = first.path.startsWith("admin")
    // }

    return this.calendarService.getCalendars(this.publishedOnly);
  }

}
