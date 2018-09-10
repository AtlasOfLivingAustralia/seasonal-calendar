import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ICalendar} from "./model/calendar";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CalendarService} from "./calendar.service";

@Injectable()
export class CalendarsResolverService implements Resolve<ICalendar[]> {

  constructor(private calendarService: CalendarService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICalendar[]> {
    return this.calendarService.calendars;
  }

}
