import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Calendar} from "./model/calendar";
import {Observable, of as observableOf} from "rxjs";
import {CalendarService} from "./calendar.service";
import {MessageService} from "./messages/message.service"
import {catchError, map, take} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {Logger} from "./shared/logger.service";

@Injectable()
export class CalendarResolverService implements Resolve<Calendar> {

  constructor(
    private calendarService: CalendarService,
    private messageService: MessageService,
    private logger: Logger,
    private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Calendar> {
    let id = route.paramMap.get("id") || route.paramMap.get("name");
    return this.calendarService.findCalendar(id!!)
      .pipe(
        catchError(e => {
          this.logger.log("Caught error", e);
          this.messageService.add({text: `${id} not found`, action: { text: 'Dismiss' } });
          return observableOf(null);
        }),
        take(1),
        map(calendar => {
          if (calendar) {
            return Calendar.fromJson(calendar);
          } else {
            // Would like to present the 404 page here but requires a fix for
            // https://github.com/angular/angular/issues/17004
            this.router.navigate(['']);
            return null;
          }
        }
      )
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
