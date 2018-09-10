import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Calendar} from "./model/calendar";
import {Observable, of as observableOf} from "rxjs";
import {CalendarService} from "./calendar.service";
import {map, take} from "rxjs/operators";
import {Injectable} from "@angular/core";

@Injectable()
export class CalendarResolverService implements Resolve<Calendar> {

  constructor(private calendarService: CalendarService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Calendar> {
    let id = route.paramMap.get("id") || route.paramMap.get("name");
    return this.calendarService.findCalendar(id!!).pipe(
      take(1),
      map(calendar => {
        if (calendar) {
          return Calendar.fromJson(calendar);
        } else {
          this.router.navigate(['/']);
          return null;
        }
      })
    );
  }

}

@Injectable()
export class NewCalendarResolverService implements Resolve<Calendar> {

  constructor(private calendarService: CalendarService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Calendar> {
    return observableOf(this.calendarService.newCalendar());
  }

}
