import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { Calendar } from "./model/calendar";

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  private static baseUrl: string = "http://devt.ala.org.au/seasonal-calendar/api/";

  constructor(private httpClient: HttpClient) { }

  getCalendars() : Observable<Calendar[]> {
    return this.httpClient.get<Calendar[]>(`${CalendarService.baseUrl}calendars`);
  }
}
