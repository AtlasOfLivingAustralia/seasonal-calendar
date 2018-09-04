import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Calendar} from "./model/calendar";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CalendarService} from "./calendar.service";
import {take} from "rxjs/operators";

@Injectable()
export class CalendarsResolverService implements Resolve<Calendar[]> {

  constructor(private calendarService: CalendarService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Calendar[]> {
    return this.calendarService.calendars;
  }

}
