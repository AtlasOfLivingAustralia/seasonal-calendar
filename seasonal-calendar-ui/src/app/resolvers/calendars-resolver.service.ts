import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ICalendar} from "../shared/model/calendar";
import {Observable, of} from "rxjs";
import {Injectable} from "@angular/core";
import {CalendarService} from "../shared/services/calendar.service";
import {catchError} from "rxjs/operators";
import {MessageService} from "../shared/services/message.service";

@Injectable()
export class CalendarsResolverService implements Resolve<ICalendar[]> {

  constructor(
    private calendarService: CalendarService,
    private router: Router,
    private messageService: MessageService,
  ) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICalendar[]> {
    // let parts = route.url;
    // let isAdmin = false;
    // if (parts.length > 0) {
    //   let first = parts[0];
    //   isAdmin = first.path.startsWith("admin")
    // }

    let publishedOnly = route.data.publishedCalendarsOnly !== false;

    return this.calendarService.getCalendars(publishedOnly).pipe(
      catchError(e => {
        this.messageService.add({text: "There was an error loading the calendars", action: { text: "Dismiss" }});
        return of([]);
      })
    );
  }

}
