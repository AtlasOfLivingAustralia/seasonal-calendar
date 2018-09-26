import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from "@angular/router";
import {ICalendar} from "./model/calendar";
import {Observable, of} from "rxjs";
import {Inject, Injectable} from "@angular/core";
import {CalendarService} from "./calendar.service";
import {catchError} from "rxjs/operators";
import {MessageService} from "./messages/message.service";

@Injectable()
export class CalendarsResolverService implements Resolve<ICalendar[]> {

  constructor(
    private calendarService: CalendarService,
    private router: Router,
    private messageService: MessageService,
    @Inject('PublishedOnly') private publishedOnly: boolean = true) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICalendar[]> {
    // let parts = route.url;
    // let isAdmin = false;
    // if (parts.length > 0) {
    //   let first = parts[0];
    //   isAdmin = first.path.startsWith("admin")
    // }

    return this.calendarService.getCalendars(this.publishedOnly).pipe(
      catchError(e => {
        this.messageService.add({text: "There was an error loading the calendars", action: { text: "Dismiss" }});
        return of([]);
      })
    );
  }

}
