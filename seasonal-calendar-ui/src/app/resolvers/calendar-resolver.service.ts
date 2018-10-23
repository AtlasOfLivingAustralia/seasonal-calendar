import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {Observable, of as observableOf} from "rxjs";
import {catchError, map, take} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {Calendar, ICalendar} from "../shared/model/calendar";
import {CalendarService} from "../shared/services/calendar.service";
import {MessageService} from "../shared/services/message.service";
import {Logger} from "../shared/services/logger.service";


@Injectable()
export class CalendarResolverService implements Resolve<Calendar> {

  constructor(
    private calendarService: CalendarService,
    private messageService: MessageService,
    private logger: Logger,
    private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Calendar> {
    let id = route.paramMap.get("id") || route.paramMap.get("name");
    let language = route.paramMap.get("language");

    let calendar$: Observable<ICalendar>;
    if (id) {
      calendar$ = this.calendarService.findCalendar(id);
    } else {
      calendar$ = this.calendarService.findCalendarByLanguage(language);
    }
    return calendar$.pipe(
      catchError(e => {
        let name = id ? id : language;
        this.logger.log("Caught error", e);
        this.messageService.add({text: `${name} not found`, action: { text: 'Dismiss' } });
        return observableOf(null);
      }),
      take(1),
      map(calendar => {
          if (calendar) {
            return Calendar.fromJson(calendar);
          } else {
            // Would like to present the 404 page here but requires a fix for
            // https://github.com/angular/angular/issues/17004
            this.router.navigateByUrl('/404', { skipLocationChange: true });
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
