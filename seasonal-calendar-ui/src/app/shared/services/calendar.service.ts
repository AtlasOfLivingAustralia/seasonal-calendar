import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {Observable, of} from 'rxjs';
import {Calendar, CalendarSaved, ICalendar} from "../model/calendar";
import { environment } from "../../../environments/environment";
import {BieSearchResponse, UserDetails} from "../model/bie-search-response";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {catchError, map, share, shareReplay, switchMap} from "rxjs/operators";
import {MessageService} from "./message.service";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private readonly calendarsEndpoint : string = `${environment.api}calendars`;
  private readonly rolesEndpoint : string = `${environment.api}roles`;
  private readonly speciesSearchEndpoint : string = `${environment.api}search/bie`;
  private readonly userSearchEndpoint : string = `${environment.api}search/users`;
  private readonly imageEndpoint: string = `${environment.api}images/upload`;
  private readonly languageGroupEndpoint: string = `${environment.api}language`;

  constructor(private httpClient: HttpClient) { }

  private sharedPublishedCalendars$: Observable<ICalendar[]>;

  getCalendars(publishedOnly: boolean = true) : Observable<ICalendar[]> {
    if (publishedOnly) {
      if (!this.sharedPublishedCalendars$) {
        this.sharedPublishedCalendars$ = this.getCalendarsInternal$(true).pipe(share());
      }

      return this.sharedPublishedCalendars$;
    } else {
      return this.getCalendarsInternal$(false);
    }

  }

  private getCalendarsInternal$(publishedOnly: boolean) {
    return this.httpClient.get<Calendar[]>(this.calendarsEndpoint, { params: { publishedOnly: `${publishedOnly}`}});
  }

  findCalendar(id: string) : Observable<ICalendar> {
    return this.httpClient.get<ICalendar>(`${this.calendarsEndpoint}/${id}`);
  }

  findCalendarByLanguage(language: string) : Observable<ICalendar> {
    return this.httpClient.get<ICalendar>(`${this.languageGroupEndpoint}/${language}`);
  }

  speciesSearch(query: string) : Observable<BieSearchResponse> {
    return this.httpClient.get<BieSearchResponse>(`${this.speciesSearchEndpoint}`, { params : {'q': query} })
  }

  userSearch(query: string) : Observable<UserDetails> {
    return this.httpClient.get<UserDetails>(this.userSearchEndpoint, { params: { 'q': query } });
  }

  newCalendar() {
    return new Calendar();
  }

  save(calendar: Calendar) {
    let endpoint = `${this.calendarsEndpoint}${calendar.collectionUuid ? `/${calendar.collectionUuid}` : ''}`;
    return this.httpClient.post<CalendarSaved>(endpoint, this.sanitizeCalendarForUpload(calendar));
  }

  delete(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}`;
    return this.httpClient.delete(endpoint);
  }

  publish(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}/publish`;
    return this.httpClient.put(endpoint, {});
  }

  unpublish(calendar: ICalendar) {
    let endpoint = `${this.calendarsEndpoint}/${calendar.collectionUuid}/unpublish`;
    return this.httpClient.put(endpoint, {});
  }

  uploadImages(files/*: FileList*/): Observable<HttpEvent<Object>> {
    const formData = new FormData();

    for (let i = 0; i < files.length; i++) {
      formData.append("images", files[i], files[i].name);
    }

    return this.httpClient.post<string[]>(this.imageEndpoint, formData, {
      observe: 'events',
      reportProgress: true,
      responseType: 'json',
      withCredentials: true
    });
  }

  saveUsers(calendar: ICalendar, calendarUsers: Array<{ username: string; admin: boolean }>) {
    return this.httpClient.post(`${this.calendarsEndpoint}/${calendar.collectionUuid}/permissions`, calendarUsers.map((value) => { return { 'editor': !value.admin, ...value} }));
  }

  private sanitizeCalendarForUpload(calendar: ICalendar): ICalendar {
    return Calendar.fromJson(calendar)
  }

  userRoles() {
    return this.httpClient.get<{[role: string] : Array<string>}>(this.rolesEndpoint);
  }
}

@Injectable()
export class UserInfoService {
  constructor(
    private calendarService: CalendarService,
    private oidcSecurityService: OidcSecurityService,
    private messageService: MessageService
  ) {}

  private cache$;

  get userinfo$(): Observable<any> {
    if (!this.cache$) {
      this.cache$ = this.requestFullUserInfo().pipe(
        shareReplay(1)
      )
    }

    return this.cache$;
  }

  private requestFullUserInfo(): Observable<any> {
    return this.oidcSecurityService.getIsAuthorized().pipe(
      switchMap((value, index) => {
        if (value) {
          return this.oidcSecurityService.getUserData().pipe(
            switchMap(userinfo => {
              return this.calendarService.userRoles().pipe(
                catchError( () => { this.messageService.add({ text: "Error getting user roles, some functions unavailable", action: { text: "Dismiss" }}); return of({}) }),
                map( roles => { return { calendarRoles: roles, ...userinfo } })
              );
            })
          )
        } else {
          return of(null);
        }
      }),

    );
  }
}
